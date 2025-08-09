package com.example.meta;

import com.example.Address;
import com.example.validation.AddressTypeFormatValidator;
import com.example.validation.AddressValidator;
import com.example.validation.exists.AddressOnlyExistsValidator;
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
@RosettaMeta(model=Address.class)
public class AddressMeta implements RosettaMetaData<Address> {

	@Override
	public List<Validator<? super Address>> dataRules(ValidatorFactory factory) {
		return Arrays.asList(
		);
	}
	
	@Override
	public List<Function<? super Address, QualifyResult>> getQualifyFunctions(QualifyFunctionFactory factory) {
		return Collections.emptyList();
	}
	
	@Override
	public Validator<? super Address> validator(ValidatorFactory factory) {
		return factory.<Address>create(AddressValidator.class);
	}

	@Override
	public Validator<? super Address> typeFormatValidator(ValidatorFactory factory) {
		return factory.<Address>create(AddressTypeFormatValidator.class);
	}

	@Deprecated
	@Override
	public Validator<? super Address> validator() {
		return new AddressValidator();
	}

	@Deprecated
	@Override
	public Validator<? super Address> typeFormatValidator() {
		return new AddressTypeFormatValidator();
	}
	
	@Override
	public ValidatorWithArg<? super Address, Set<String>> onlyExistsValidator() {
		return new AddressOnlyExistsValidator();
	}
}
