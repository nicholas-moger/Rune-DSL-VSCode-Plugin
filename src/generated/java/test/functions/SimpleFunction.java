package test.functions;

import com.google.inject.ImplementedBy;
import com.rosetta.model.lib.functions.RosettaFunction;


@ImplementedBy(SimpleFunction.SimpleFunctionDefault.class)
public abstract class SimpleFunction implements RosettaFunction {

	/**
	* @param input 
	* @return result 
	*/
	public String evaluate(String input) {
		String result = doEvaluate(input);
		
		return result;
	}

	protected abstract String doEvaluate(String input);

	public static class SimpleFunctionDefault extends SimpleFunction {
		@Override
		protected String doEvaluate(String input) {
			String result = null;
			return assignOutput(result, input);
		}
		
		protected String assignOutput(String result, String input) {
			result = input;
			
			return result;
		}
	}
}
