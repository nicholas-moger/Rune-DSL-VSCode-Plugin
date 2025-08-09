package test.functions;

import com.google.inject.ImplementedBy;
import com.rosetta.model.lib.expression.MapperMaths;
import com.rosetta.model.lib.functions.RosettaFunction;
import com.rosetta.model.lib.mapper.MapperS;
import test.Person;


@ImplementedBy(Greet.GreetDefault.class)
public abstract class Greet implements RosettaFunction {

	/**
	* @param person 
	* @return greeting 
	*/
	public String evaluate(Person person) {
		String greeting = doEvaluate(person);
		
		return greeting;
	}

	protected abstract String doEvaluate(Person person);

	public static class GreetDefault extends Greet {
		@Override
		protected String doEvaluate(Person person) {
			String greeting = null;
			return assignOutput(greeting, person);
		}
		
		protected String assignOutput(String greeting, Person person) {
			greeting = MapperMaths.<String, String, String>add(MapperMaths.<String, String, String>add(MapperMaths.<String, String, String>add(MapperS.of("Hello, "), MapperS.of(person).<String>map("getFirstName", _person -> _person.getFirstName())), MapperS.of(" ")), MapperS.of(person).<String>map("getLastName", _person -> _person.getLastName())).get();
			
			return greeting;
		}
	}
}
