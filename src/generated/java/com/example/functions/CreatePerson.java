package com.example.functions;

import com.example.Person;
import com.example.Person.PersonBuilder;
import com.google.inject.ImplementedBy;
import com.rosetta.model.lib.functions.ModelObjectValidator;
import com.rosetta.model.lib.functions.RosettaFunction;
import java.util.Optional;
import javax.inject.Inject;


@ImplementedBy(CreatePerson.CreatePersonDefault.class)
public abstract class CreatePerson implements RosettaFunction {
	
	@Inject protected ModelObjectValidator objectValidator;

	/**
	* @param firstName 
	* @param lastName 
	* @param personAge 
	* @return result 
	*/
	public Person evaluate(String firstName, String lastName, Integer personAge) {
		Person.PersonBuilder resultBuilder = doEvaluate(firstName, lastName, personAge);
		
		final Person result;
		if (resultBuilder == null) {
			result = null;
		} else {
			result = resultBuilder.build();
			objectValidator.validate(Person.class, result);
		}
		
		return result;
	}

	protected abstract Person.PersonBuilder doEvaluate(String firstName, String lastName, Integer personAge);

	public static class CreatePersonDefault extends CreatePerson {
		@Override
		protected Person.PersonBuilder doEvaluate(String firstName, String lastName, Integer personAge) {
			Person.PersonBuilder result = Person.builder();
			return assignOutput(result, firstName, lastName, personAge);
		}
		
		protected Person.PersonBuilder assignOutput(Person.PersonBuilder result, String firstName, String lastName, Integer personAge) {
			result = toBuilder(Person.builder()
				.setFirstName(firstName)
				.setLastName(lastName)
				.setAge(personAge)
				.build());
			
			return Optional.ofNullable(result)
				.map(o -> o.prune())
				.orElse(null);
		}
	}
}
