# Change Log

All notable changes to the "Rune-DSL Language Support" extension will be documented in this file.

## [5.1.7] - 2025-08-09

### Fixed
- **Extension Activation**: Resolved dependency bundling issues
- **Language Server Startup**: Fixed missing vscode-jsonrpc module errors
- **Build Process**: Removed server overrides functionality that was causing conflicts
- **Documentation**: Updated repository URLs to new community repository

### Changed
- **Build Script**: Disabled server overrides to use standard rune-dsl build
- **Repository**: Updated links to https://github.com/nicholas-moger/Rune-DSL-VSCode-Plugin.git

## [5.1.6] - 2025-08-09

### Added
- **Marketplace Release**: First public release on VS Code Marketplace
- **Rebranding**: Updated to "Rune-DSL Language Support" 
- **Enhanced Documentation**: Comprehensive README with attribution and licensing
- **Java Code Generation**: Enabled by default with configurable output paths
- **Performance Warnings**: Clear documentation of known performance limitations
- **Community Attribution**: Proper credit to FINOS and open-source contributors

### Changed
- **Default Settings**: Java code generation now enabled by default
- **Package Metadata**: Updated for marketplace publication with proper keywords
- **Repository Links**: Updated to point to community extension repository
- **License**: Clarified Apache 2.0 license following upstream project

### Fixed
- **Dependency Issues**: Resolved missing vscode-jsonrpc dependencies
- **Extension Activation**: Improved reliability of language server startup

## [5.1.0] - 2025-06-05

### Added
- **Ultra Performance Optimization** for large projects (1000+ files)
- Three optimization profiles: Standard, Optimized, and Ultra
- Advanced configuration options for memory, concurrency, and caching
- Performance diagnostic tool with project analysis and recommendations
- Comprehensive performance guide (PERFORMANCE_GUIDE.md)
- Support for parallel building, async indexing, and batch processing
- Environment variable support for advanced JVM tuning
- File exclusion patterns for better workspace scanning
- Performance monitoring command: "Rosetta: Run Performance Diagnostic"
- **Status bar indicator** showing current optimization profile
- **Startup notifications** with launcher information
- **Profile switching command**: "Rosetta: Switch Optimization Profile"
- **Enhanced logging** with prominent launcher selection display
- Optimization mode guide (OPTIMIZATION_MODE_GUIDE.md)

### Enhanced
- Language server launcher with ultra-optimized JVM settings
- Dynamic memory allocation based on project size
- Advanced garbage collection tuning (G1GC with optimized pause times)
- **User visibility** of active optimization mode and launcher
- **One-click profile switching** with automatic reload prompts
- Configurable resource caching and lazy loading
- Improved file processing limits for large projects

### Performance Features
- Maximum project size support: 10,000+ files
- Memory range: 2GB - 16GB configurable
- Concurrent request handling: 2-8 requests
- Advanced caching with configurable sizes
- Batch processing for efficient file operations
- Asynchronous indexing for better responsiveness

## [5.0.0] - 2025-06-04

### Added
- Automatic model files setup - basic types and annotations are now automatically copied to the workspace
- Multi-workspace folder support - each workspace folder is properly set up with model files
- Improved error handling and user notifications
- Debug mode with enhanced logging
- Comprehensive user guide

### Changed
- Updated README with clearer installation and usage instructions
- Improved extension activation process
- Better handling of workspace folder changes

### Fixed
- Fixed issue where basic types weren't recognized without manual file copying
- Improved language server startup reliability
- Fixed path resolution issues on different platforms
- **CRITICAL: LSP Protocol Violation** - Fixed launcher scripts outputting startup messages to stdout instead of stderr, which caused "Message header must separate key and value using ':'" errors
- **Java Compatibility** - Removed deprecated `-XX:+UseCGroupMemoryLimitForHeap` JVM option that caused issues with Java 21
- **Client Connection Errors** - Enhanced error handling to prevent "Pending response rejected since connection got disposed" and "Client is not running and can't be stopped" errors
- **Recursive Activation** - Fixed workspace change listener to use restart function instead of recursive activation calls
- **Timeout Handling** - Added proper timeouts for client start/stop operations with fallback error handling
- **Async Deactivation** - Made extension deactivation async to prevent hanging on shutdown
- **Error Recovery** - Added automatic client cleanup and restart functionality for failed connections