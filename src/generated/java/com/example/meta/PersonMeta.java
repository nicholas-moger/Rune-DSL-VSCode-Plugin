package com.example.meta;

import com.example.Person;
import com.example.validation.PersonTypeFormatValidator;
import com.example.validation.PersonValidator;
import com.example.validation.exists.PersonOnlyExistsValidator;
import com.rosetta.model.lib.annotations.RosettaMeta;
import com.rosetta.model.lib.meta.RosettaMetaData;
import com.rosetta.model.lib.qualify.QualifyFunctionFactory;
import com.rosetta.model.lib.qualify.QualifyResult;
import com.rosetta.model.lib.validation.Validator;
import com.rosetta.model.lib.validation.ValidatorFactory;
import com.rosetta.model.lib.validation.ValidatorWithArg;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;


/**
 * @version 0.0.0
 */
@RosettaMeta(model=Person.class)
public class PersonMeta implements RosettaMetaData<Person> {

	@Override
	public List<Validator<? super Person>> dataRules(ValidatorFactory factory) {
		return Arrays.asList(
		);
	}
	
	@Override
	public List<Function<? super Person, QualifyResult>> getQualifyFunctions(QualifyFunctionFactory factory) {
		return Collections.emptyList();
	}
	
	@Override
	public Validator<? super Person> validator(ValidatorFactory factory) {
		return factory.<Person>create(PersonValidator.class);
	}

	@Override
	public Validator<? super Person> typeFormatValidator(ValidatorFactory factory) {
		return factory.<Person>create(PersonTypeFormatValidator.class);
	}

	@Deprecated
	@Override
	public Validator<? super Person> validator() {
		return new PersonValidator();
	}

	@Deprecated
	@Override
	public Validator<? super Person> typeFormatValidator() {
		return new PersonTypeFormatValidator();
	}
	
	@Override
	public ValidatorWithArg<? super Person, Set<String>> onlyExistsValidator() {
		return new PersonOnlyExistsValidator();
	}
}
