/*
 * Copyright 2024 REGnosys
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

import * as path from 'path';
import * as os from 'os';
import * as fs from 'fs';
import * as https from 'https';
import { spawn, ChildProcess } from 'child_process';

import { Trace } from 'vscode-jsonrpc';
import { commands, window, workspace, ExtensionContext, Uri, ConfigurationTarget, WorkspaceFolder, StatusBarAlignment, StatusBarItem, ProgressLocation } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient/node';

let lc: LanguageClient;
let statusBarItem: StatusBarItem;

const GITHUB_RAW_BASE_URL = 'https://raw.githubusercontent.com/finos/rune-dsl/main/rosetta-runtime/src/main/resources/model';

async function fetchFileFromGitHub(filename: string): Promise<string> {
    return new Promise((resolve, reject) => {
        const url = `${GITHUB_RAW_BASE_URL}/${filename}`;
        
        https.get(url, (response) => {
            if (response.statusCode !== 200) {
                reject(new Error(`Failed to fetch ${filename}: HTTP ${response.statusCode}`));
                return;
            }
            
            let data = '';
            response.on('data', (chunk) => {
                data += chunk;
            });
            
            response.on('end', () => {
                resolve(data);
            });
        }).on('error', (error) => {
            reject(new Error(`Network error fetching ${filename}: ${error.message}`));
        });
    });
}

function isWSL(): boolean {
    return process.platform === 'linux' && os.release().toLowerCase().includes('microsoft');
}

async function detectJavaCommand(): Promise<string> {
    return new Promise((resolve) => {
        // Try java command first
        const testProcess = spawn('java', ['-version'], { stdio: 'pipe' });
        testProcess.on('close', (code) => {
            if (code === 0) {
                resolve('java');
            } else {
                // Fallback to common Java installation paths
                const javaHome = process.env.JAVA_HOME;
                if (javaHome) {
                    const javaExe = process.platform === 'win32' ? 'java.exe' : 'java';
                    const javaPath = path.join(javaHome, 'bin', javaExe);
                    if (fs.existsSync(javaPath)) {
                        resolve(javaPath);
                    }
                }
                resolve('java'); // fallback to system java
            }
        });
        testProcess.on('error', () => {
            resolve('java'); // fallback to system java
        });
    });
}

function getLanguageServerPath(context: ExtensionContext): { scriptPath: string; jarPath: string } {
    // Look for the language server in the extension's embedded resources
    const languageServerDir = path.join(context.extensionPath, 'src', 'rosetta');
    
    const scriptPath = process.platform === 'win32'
        ? path.join(languageServerDir, 'bin', 'rune-dsl-ls.bat')
        : path.join(languageServerDir, 'bin', 'rune-dsl-ls');
    
    // Also find the JAR file for direct Java execution as fallback
    const repoPath = path.join(languageServerDir, 'repo');
    let jarPath = '';
    
    try {
        if (fs.existsSync(repoPath)) {
            const files = fs.readdirSync(repoPath);
            for (const file of files) {
                if (file.startsWith('com.regnosys.rosetta.ide-') && file.endsWith('.jar') && !file.includes('tests')) {
                    jarPath = path.join(repoPath, file);
                    break;
                }
            }
        }
    } catch (error) {
        console.warn('Could not locate JAR file for fallback execution:', error);
    }
    
    return { scriptPath, jarPath };
}

async function createServerOptions(context: ExtensionContext): Promise<ServerOptions> {
    const { scriptPath, jarPath } = getLanguageServerPath(context);
    const config = workspace.getConfiguration('runeDsl');
    const javaOpts = config.get<string>('languageServer.javaOpts', '');
    // Build JVM -D flags to control server-side codegen behavior
    const enableJavaGeneration = config.get<boolean>('codeGeneration.enableJavaGeneration', false);
    const javaGenOutputPath = config.get<string>('codeGeneration.outputPath', 'src/generated');
    const dProps: string[] = [
        `-Drune.codegen.enableJava=${enableJavaGeneration}`,
    ];
    if (javaGenOutputPath) {
        dProps.push(`-Drune.codegen.outputDir=${javaGenOutputPath}`);
    }
    const dPropsString = dProps.map(p => p.includes(' ') ? `"${p}"` : p).join(' ');
    
    // Check if script exists and is executable
    if (fs.existsSync(scriptPath)) {
        try {
            // On Unix systems, check if script is executable
            if (process.platform !== 'win32') {
                const stats = fs.statSync(scriptPath);
                if (!(stats.mode & parseInt('111', 8))) {
                    // Make script executable
                    fs.chmodSync(scriptPath, stats.mode | parseInt('755', 8));
                }
            }
            
            let runOptions: any = { shell: true };
            let debugOptions: any = { shell: true, env: createDebugEnv() };
            
            // For WSL, we might need to convert Windows paths to WSL paths
            let effectiveScriptPath = scriptPath;
            if (isWSL() && scriptPath.includes('\\')) {
                // Convert Windows path to WSL path if needed
                effectiveScriptPath = scriptPath.replace(/\\/g, '/');
            }
            
            const combinedJavaOpts = [javaOpts, dPropsString].filter(Boolean).join(' ').trim();
            runOptions.env = { ...process.env, ...(combinedJavaOpts ? { JAVA_OPTS: combinedJavaOpts } : {}) };
            debugOptions.env = { ...debugOptions.env, ...(combinedJavaOpts ? { JAVA_OPTS: combinedJavaOpts } : {}) };
            
            return {
                run: { command: effectiveScriptPath, options: runOptions },
                debug: { command: effectiveScriptPath, args: ['-trace'], options: debugOptions }
            };
        } catch (error) {
            console.warn('Error setting up script execution, falling back to JAR:', error);
        }
    }
    
    // Fallback to direct JAR execution
    if (jarPath && fs.existsSync(jarPath)) {
        const javaCommand = await detectJavaCommand();
        const baseArgs = ['-jar', jarPath];
        
        const preArgs: string[] = [];
        if (javaOpts) {
            preArgs.push(...javaOpts.split(' ').filter(opt => opt.trim()));
        }
        // Add our -D flags ahead of -jar
        preArgs.push(...dProps);
        if (preArgs.length > 0) {
            baseArgs.unshift(...preArgs);
        }
        
        return {
            run: { command: javaCommand, args: baseArgs },
            debug: { 
                command: javaCommand, 
                args: [...baseArgs, '-trace'], 
                options: { env: createDebugEnv() }
            }
        };
    }
    
    // If neither script nor JAR is found, throw an error
    throw new Error(`Language server not found. Expected script at: ${scriptPath} or JAR at: ${jarPath}`);
}

function updateStatusBar(status: string, color?: string) {
    if (statusBarItem) {
        statusBarItem.text = `$(pulse) Rune DSL: ${status}`;
        statusBarItem.color = color;
        statusBarItem.show();
    }
}

async function copyBasicTypesToWorkspace() {
    try {
        const workspaceFolders = workspace.workspaceFolders;
        if (!workspaceFolders || workspaceFolders.length === 0) {
            window.showErrorMessage('No workspace folder is opened. Please open a folder first.');
            return;
        }

        const targetFolder = workspaceFolders[0].uri.fsPath;
        const config = workspace.getConfiguration('runeDsl');
        const customPath = config.get<string>('templates.basicTypesPath');
        
        // Get the extension path for embedded resources
        const extensionPath = path.dirname(path.dirname(__dirname)); // Go up to extension root
        let sourcePath: string;
        
        if (customPath && customPath.trim()) {
            // Use custom path if specified
            sourcePath = path.isAbsolute(customPath) ? customPath : path.join(extensionPath, customPath);
        } else {
            // Use embedded resources path
            sourcePath = path.join(extensionPath, 'resources', 'templates');
        }
        
        if (!fs.existsSync(sourcePath)) {
            window.showErrorMessage(`Source path not found: ${sourcePath}. Please ensure the extension is built properly or check your custom path setting.`);
            return;
        }
        
        const files = ['annotations.rosetta', 'basictypes.rosetta'];
        let copiedFiles: string[] = [];
        let failedFiles: string[] = [];
        
        for (const file of files) {
            const sourceFile = path.join(sourcePath, file);
            const targetFile = path.join(targetFolder, file);
            
            try {
                if (fs.existsSync(sourceFile)) {
                    const content = fs.readFileSync(sourceFile, 'utf8');
                    
                    // Check if target file already exists and ask for confirmation
                    if (fs.existsSync(targetFile)) {
                        const choice = await window.showWarningMessage(
                            `File ${file} already exists in workspace. Overwrite?`,
                            'Yes', 'No', 'Yes to All'
                        );
                        
                        if (choice === 'No') {
                            continue;
                        } else if (choice === 'Yes to All') {
                            // Continue with this and remaining files
                        } else if (choice !== 'Yes') {
                            continue; // User cancelled
                        }
                    }
                    
                    fs.writeFileSync(targetFile, content);
                    copiedFiles.push(file);
                } else {
                    console.warn(`Source file not found: ${sourceFile}`);
                    failedFiles.push(file);
                }
            } catch (error: any) {
                console.error(`Failed to copy ${file}:`, error);
                failedFiles.push(`${file} (${error.message})`);
            }
        }
        
        // Show results
        if (copiedFiles.length > 0) {
            window.showInformationMessage(`Successfully copied: ${copiedFiles.join(', ')}`);
        }
        
        if (failedFiles.length > 0) {
            window.showWarningMessage(`Failed to copy: ${failedFiles.join(', ')}`);
        }
        
        if (copiedFiles.length === 0 && failedFiles.length === 0) {
            window.showWarningMessage('No basic type files were found to copy');
        }
        
    } catch (error: any) {
        const errorMsg = `Failed to copy basic types: ${error.message}`;
        console.error(errorMsg);
        window.showErrorMessage(errorMsg);
    }
}

async function copyBasicTypesFromGitHub() {
    try {
        const workspaceFolders = workspace.workspaceFolders;
        if (!workspaceFolders || workspaceFolders.length === 0) {
            window.showErrorMessage('No workspace folder is opened. Please open a folder first.');
            return false;
        }

        const targetFolder = workspaceFolders[0].uri.fsPath;
        const files = ['annotations.rosetta', 'basictypes.rosetta'];
        let copiedFiles: string[] = [];
        let failedFiles: string[] = [];
        
        // Show progress
        await window.withProgress({
            location: ProgressLocation.Notification,
            title: 'Downloading Rune DSL basic types from GitHub...',
            cancellable: false
        }, async (progress) => {
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                progress.report({ 
                    increment: (i / files.length) * 100, 
                    message: `Downloading ${file}...` 
                });
                
                const targetFile = path.join(targetFolder, file);
                
                try {
                    // Check if file exists and ask for confirmation
                    if (fs.existsSync(targetFile)) {
                        const choice = await window.showWarningMessage(
                            `File ${file} already exists in workspace. Overwrite?`,
                            'Yes', 'No', 'Yes to All'
                        );
                        
                        if (choice === 'No') {
                            continue;
                        } else if (choice !== 'Yes' && choice !== 'Yes to All') {
                            continue; // User cancelled
                        }
                    }
                    
                    // Fetch file content from GitHub
                    const content = await fetchFileFromGitHub(file);
                    fs.writeFileSync(targetFile, content, 'utf8');
                    copiedFiles.push(file);
                    
                } catch (error: any) {
                    console.error(`Failed to download ${file}:`, error);
                    failedFiles.push(`${file} (${error.message})`);
                }
            }
        });
        
        // Show results
        if (copiedFiles.length > 0) {
            window.showInformationMessage(`Successfully downloaded from GitHub: ${copiedFiles.join(', ')}`);
        }
        
        if (failedFiles.length > 0) {
            window.showWarningMessage(`Failed to download: ${failedFiles.join(', ')}`);
        }
        
        return copiedFiles.length > 0;
        
    } catch (error: any) {
        const errorMsg = `Failed to download basic types from GitHub: ${error.message}`;
        console.error(errorMsg);
        window.showErrorMessage(errorMsg);
        return false;
    }
}

async function showStartupBasicTypesPrompt(context: ExtensionContext): Promise<void> {
    try {
        // Check if startup prompts are enabled
        const config = workspace.getConfiguration('runeDsl');
        const promptEnabled = config.get<boolean>('startup.promptForBasicTypes', true);
        
        if (!promptEnabled) {
            console.log('Basic types startup prompt is disabled in settings');
            return;
        }
        
        const hasBeenPrompted = context.globalState.get<boolean>('runeDsl.hasBeenPromptedForBasicTypes', false);
        console.log('Basic types prompt state:', { hasBeenPrompted, promptEnabled });
        
        if (!hasBeenPrompted) {
            const choice = await window.showInformationMessage(
                'Would you like to copy basicTypes.rosetta and annotations.rosetta from the Rune DSL repository to your workspace?',
                'Yes', 'No', 'Don\'t ask again'
            );
            
            console.log('User choice for basic types prompt:', choice);
            
            if (choice === 'Yes') {
                const success = await copyBasicTypesFromGitHub();
                if (success) {
                    // Mark as completed so we don't ask again
                    await context.globalState.update('runeDsl.hasBeenPromptedForBasicTypes', true);
                    console.log('Basic types copied successfully, marked as prompted');
                }
            } else if (choice === 'Don\'t ask again') {
                await context.globalState.update('runeDsl.hasBeenPromptedForBasicTypes', true);
                console.log('User chose not to see basic types prompt again');
                window.showInformationMessage('You can copy basic types later using the "Copy Basic Rune DSL Types from GitHub" command.');
            }
            // If "No", we'll ask again next time
        } else {
            console.log('Basic types prompt already shown, skipping');
        }
    } catch (error) {
        console.error('Error in showStartupBasicTypesPrompt:', error);
    }
}

async function showCodeGenerationInfoPrompt(context: ExtensionContext): Promise<void> {
    try {
        const config = workspace.getConfiguration('runeDsl');
        const showInfo = config.get<boolean>('startup.showCodeGenerationInfo', true);
        const javaGenerationEnabled = config.get<boolean>('codeGeneration.enableJavaGeneration', false);
        
        if (!showInfo) {
            console.log('Code generation info prompt is disabled in settings');
            return;
        }
        
        const hasBeenInformed = context.globalState.get<boolean>('runeDsl.hasBeenInformedAboutCodeGeneration', false);
        console.log('Code generation prompt state:', { hasBeenInformed, showInfo });
        
        if (!hasBeenInformed) {
            const message = javaGenerationEnabled 
                ? 'Java code generation is enabled. Generated files will be placed in the configured output directory.'
                : 'Java code generation is currently disabled. You can enable it in the extension settings if needed.';
                
            const choice = await window.showInformationMessage(
                message,
                'Open Settings', 'Close', 'Don\'t show again'
            );
            
            console.log('User choice for code generation prompt:', choice);
            
            if (choice === 'Open Settings') {
                await openSettings();
            } else if (choice === 'Don\'t show again') {
                await context.globalState.update('runeDsl.hasBeenInformedAboutCodeGeneration', true);
                console.log('User chose not to see code generation prompt again');
            }
            // If "Close", we'll show again next time
        } else {
            console.log('Code generation prompt already shown, skipping');
        }
    } catch (error) {
        console.error('Error in showCodeGenerationInfoPrompt:', error);
    }
}

async function openSettings() {
    commands.executeCommand('workbench.action.openSettings', 'runeDsl');
}

async function resetPromptStates(context: ExtensionContext) {
    try {
        await context.globalState.update('runeDsl.hasBeenPromptedForBasicTypes', false);
        await context.globalState.update('runeDsl.hasBeenInformedAboutCodeGeneration', false);
        window.showInformationMessage('Startup prompt states have been reset. Restart VS Code or reload the window to see prompts again.');
        console.log('Prompt states reset successfully');
    } catch (error: any) {
        window.showErrorMessage(`Failed to reset prompt states: ${error.message}`);
        console.error('Error resetting prompt states:', error);
    }
}

async function toggleJavaGeneration() {
    const config = workspace.getConfiguration('runeDsl');
    const currentValue = config.get<boolean>('codeGeneration.enableJavaGeneration', false);
    const newValue = !currentValue;
    
    try {
        await config.update('codeGeneration.enableJavaGeneration', newValue, ConfigurationTarget.Workspace);
        
        const status = newValue ? 'enabled' : 'disabled';
        const choice = await window.showInformationMessage(
            `Java code generation ${status}. Restart the language server to apply changes.`,
            'Restart Now', 'Later'
        );
        
        if (choice === 'Restart Now') {
            await restartLanguageServer();
        }
    } catch (error: any) {
        window.showErrorMessage(`Failed to toggle Java generation: ${error.message}`);
    }
}

async function restartLanguageServer() {
    try {
        updateStatusBar('Restarting...', '#FFA500');
        if (lc) {
            await lc.stop();
        }
        await startLanguageServer();
        window.showInformationMessage('Rune DSL Language Server restarted successfully');
    } catch (error: any) {
        const errorMsg = `Failed to restart language server: ${error.message}`;
        console.error(errorMsg);
        window.showErrorMessage(errorMsg);
        updateStatusBar('Restart Failed', '#FF0000');
    }
}

async function startLanguageServer() {
    if (!lc) {
        throw new Error('Language client not initialized');
    }
    
    const config = workspace.getConfiguration('runeDsl');
    
    // Get settings
    const enableDiagnostics = config.get<boolean>('linting.enableDiagnostics', true);
    const enableSemanticHighlighting = config.get<boolean>('linting.enableSemanticHighlighting', true);
    const enableHover = config.get<boolean>('linting.enableHover', true);
    const enableCompletion = config.get<boolean>('linting.enableCompletion', true);
    const enableFormatting = config.get<boolean>('linting.enableFormatting', true);
    const enableInlayHints = config.get<boolean>('linting.enableInlayHints', true);
    const enableCodeActions = config.get<boolean>('linting.enableCodeActions', true);
    const traceLevel = config.get<string>('languageServer.traceLevel', 'off');
    
    // Code generation settings
    const enableJavaGeneration = config.get<boolean>('codeGeneration.enableJavaGeneration', false);
    const outputPath = config.get<string>('codeGeneration.outputPath', 'src/generated');
    
    // Update client options with settings
    const initializationOptions = {
        enableDiagnostics,
        enableSemanticHighlighting,
        enableHover,
        enableCompletion,
        enableFormatting,
        enableInlayHints,
        enableCodeActions,
        enableJavaGeneration,
        javaGenerationOutputPath: outputPath,
        // Workspace scoping preferences (consumed if supported by LS)
        workspaceRoots: config.get<string[]>('workspace.roots', []),
        onlyOpenFiles: config.get<boolean>('workspace.onlyOpenFiles', false),
        excludeGlobs: config.get<string[]>('workspace.excludeGlobs', [])
    };
    
    // Update the language client with new initialization options
    if (lc.clientOptions) {
        lc.clientOptions.initializationOptions = initializationOptions;
    }
    
    // Set trace level
    let trace: Trace;
    switch (traceLevel) {
        case 'messages': trace = Trace.Messages; break;
        case 'verbose': trace = Trace.Verbose; break;
        default: trace = Trace.Off; break;
    }
    
    lc.setTrace(trace);
    
    updateStatusBar('Starting...', '#FFFF00');
    console.log("Launching Rune DSL Language Server...");
    
    try {
        await lc.start();
        console.log("Rune DSL Language Server started.");
        updateStatusBar('Running', '#00FF00');
    } catch (err: any) {
        console.error("Failed to launch Rune DSL Language Server.");
        console.error(err);
        updateStatusBar('Failed', '#FF0000');
        
        // Show user-friendly error message
        let errorMsg = 'Failed to start Rune DSL Language Server. ';
        if (err.message?.includes('ENOENT')) {
            errorMsg += 'Language server executable not found. Please ensure the project is built with Maven.';
        } else if (err.message?.includes('Java') || err.message?.includes('java')) {
            errorMsg += 'Java runtime not found. Please ensure Java 11+ is installed and in PATH.';
        } else if (err.message?.includes('spawn') || err.message?.includes('EACCES')) {
            errorMsg += 'Permission denied. Please check file permissions or run VS Code as administrator.';
        } else {
            errorMsg += `Error: ${err.message}`;
        }
        
        const actions = ['Open Settings', 'Restart', 'Show Logs'];
        const selection = await window.showErrorMessage(errorMsg, ...actions);
        
        switch (selection) {
            case 'Open Settings':
                await openSettings();
                break;
            case 'Restart':
                await restartLanguageServer();
                break;
            case 'Show Logs':
                commands.executeCommand('workbench.action.toggleDevTools');
                break;
        }
        
        throw err;
    }
}

export async function activate(context: ExtensionContext) {
    console.log('Activating Rune DSL extension...');
    
    // Create status bar item
    statusBarItem = window.createStatusBarItem(StatusBarAlignment.Left, 100);
    statusBarItem.command = 'runeDsl.openSettings';
    context.subscriptions.push(statusBarItem);
    
    // Register commands
    const copyBasicTypesCommand = commands.registerCommand('runeDsl.copyBasicTypes', copyBasicTypesToWorkspace);
    const copyBasicTypesFromGitHubCommand = commands.registerCommand('runeDsl.copyBasicTypesFromGitHub', copyBasicTypesFromGitHub);
    const openSettingsCommand = commands.registerCommand('runeDsl.openSettings', openSettings);
    const restartLanguageServerCommand = commands.registerCommand('runeDsl.restartLanguageServer', restartLanguageServer);
    const toggleJavaGenerationCommand = commands.registerCommand('runeDsl.toggleJavaGeneration', toggleJavaGeneration);
    const resetPromptStatesCommand = commands.registerCommand('runeDsl.resetPromptStates', () => resetPromptStates(context));
    
    context.subscriptions.push(
        copyBasicTypesCommand, 
        copyBasicTypesFromGitHubCommand, 
        openSettingsCommand, 
        restartLanguageServerCommand, 
        toggleJavaGenerationCommand,
        resetPromptStatesCommand
    );
    
    try {
        // Create server options with cross-platform compatibility
        const serverOptions = await createServerOptions(context);
        
        // Create the language client and options with scoped watching
        const cfg = workspace.getConfiguration('runeDsl');
        const roots = cfg.get<string[]>('workspace.roots', []);
        const onlyOpenFiles = cfg.get<boolean>('workspace.onlyOpenFiles', false);

        const watchers = [] as Array<ReturnType<typeof workspace.createFileSystemWatcher>>;
        if (!onlyOpenFiles) {
            if (roots.length > 0) {
                for (const r of roots) {
                    const pattern = path.posix.join(r.replace(/\\/g, '/'), '**/*.rosetta');
                    watchers.push(workspace.createFileSystemWatcher(pattern));
                }
            } else {
                watchers.push(workspace.createFileSystemWatcher('**/*.rosetta'));
            }
        }

        let clientOptions: LanguageClientOptions = {
            documentSelector: [
                { language: 'rosetta', scheme: 'file' },
                { language: 'rosetta', scheme: 'untitled' }
            ],
            synchronize: {
                fileEvents: watchers,
                configurationSection: 'runeDsl'
            },
            initializationOptions: {
                workspaceRoots: roots,
                onlyOpenFiles
            }
        };
        
        // Create the language client
        lc = new LanguageClient('Rune DSL Language Server', serverOptions, clientOptions);
        
        // Start the language server
        await startLanguageServer();
        
        // Show startup prompts (after a short delay to let everything initialize)
        // Show them sequentially to avoid conflicts
        setTimeout(async () => {
            try {
                await showStartupBasicTypesPrompt(context);
                // Wait a bit before showing the next prompt to avoid overwhelming the user
                setTimeout(async () => {
                    try {
                        await showCodeGenerationInfoPrompt(context);
                    } catch (error) {
                        console.error('Error showing code generation prompt:', error);
                    }
                }, 2000);
            } catch (error) {
                console.error('Error showing basic types prompt:', error);
                // Still try to show the code generation prompt even if basic types prompt fails
                setTimeout(async () => {
                    try {
                        await showCodeGenerationInfoPrompt(context);
                    } catch (error) {
                        console.error('Error showing code generation prompt:', error);
                    }
                }, 1000);
            }
        }, 2000);
        
    } catch (error) {
        const errorMsg = `Failed to initialize language server: ${error instanceof Error ? error.message : String(error)}`;
        console.error(errorMsg);
        updateStatusBar('Init Failed', '#FF0000');
        window.showErrorMessage(errorMsg + '. Please ensure the project is built and Java is installed.');
        return;
    }
    
    // Legacy command for backward compatibility
    const legacyCommand = commands.registerCommand("rosetta.a.proxy", async () => {
        let activeEditor = window.activeTextEditor;
        if (!activeEditor || !activeEditor.document || activeEditor.document.languageId !== 'rosetta') {
            return;
        }

        if (activeEditor.document.uri instanceof Uri) {
            commands.executeCommand("rosetta.a", activeEditor.document.uri.toString());
        }
    });
    context.subscriptions.push(legacyCommand);
}
export function deactivate() {
    console.log("Stopping Rune DSL Language Server...");
    if (statusBarItem) {
        statusBarItem.dispose();
    }
    return lc?.stop();
}

function createDebugEnv() {
    const config = workspace.getConfiguration('runeDsl');
    const customJavaOpts = config.get<string>('languageServer.javaOpts', '');
    
    let javaOpts = "-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n,quiet=y";
    
    if (customJavaOpts) {
        javaOpts = `${customJavaOpts} ${javaOpts}`;
    }
    
    return Object.assign({
        JAVA_OPTS: javaOpts
    }, process.env);
}
