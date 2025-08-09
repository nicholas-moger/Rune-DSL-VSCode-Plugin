package test.functions;

import com.google.inject.ImplementedBy;
import com.rosetta.model.lib.functions.RosettaFunction;


@ImplementedBy(TestFunction.TestFunctionDefault.class)
public abstract class TestFunction implements RosettaFunction {

	/**
	* @param inputString 
	* @param inputInt 
	* @return result 
	*/
	public String evaluate(String inputString, Integer inputInt) {
		String result = doEvaluate(inputString, inputInt);
		
		return result;
	}

	protected abstract String doEvaluate(String inputString, Integer inputInt);

	public static class TestFunctionDefault extends TestFunction {
		@Override
		protected String doEvaluate(String inputString, Integer inputInt) {
			String result = null;
			return assignOutput(result, inputString, inputInt);
		}
		
		protected String assignOutput(String result, String inputString, Integer inputInt) {
			result = inputString;
			
			return result;
		}
	}
}
