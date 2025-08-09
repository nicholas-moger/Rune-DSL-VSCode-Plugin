package test.meta;

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
import test.SimpleType;
import test.validation.SimpleTypeTypeFormatValidator;
import test.validation.SimpleTypeValidator;
import test.validation.exists.SimpleTypeOnlyExistsValidator;


/**
 * @version 0.0.0
 */
@RosettaMeta(model=SimpleType.class)
public class SimpleTypeMeta implements RosettaMetaData<SimpleType> {

	@Override
	public List<Validator<? super SimpleType>> dataRules(ValidatorFactory factory) {
		return Arrays.asList(
		);
	}
	
	@Override
	public List<Function<? super SimpleType, QualifyResult>> getQualifyFunctions(QualifyFunctionFactory factory) {
		return Collections.emptyList();
	}
	
	@Override
	public Validator<? super SimpleType> validator(ValidatorFactory factory) {
		return factory.<SimpleType>create(SimpleTypeValidator.class);
	}

	@Override
	public Validator<? super SimpleType> typeFormatValidator(ValidatorFactory factory) {
		return factory.<SimpleType>create(SimpleTypeTypeFormatValidator.class);
	}

	@Deprecated
	@Override
	public Validator<? super SimpleType> validator() {
		return new SimpleTypeValidator();
	}

	@Deprecated
	@Override
	public Validator<? super SimpleType> typeFormatValidator() {
		return new SimpleTypeTypeFormatValidator();
	}
	
	@Override
	public ValidatorWithArg<? super SimpleType, Set<String>> onlyExistsValidator() {
		return new SimpleTypeOnlyExistsValidator();
	}
}
