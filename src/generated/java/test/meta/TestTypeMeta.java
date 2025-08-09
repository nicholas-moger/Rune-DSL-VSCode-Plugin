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
import test.TestType;
import test.validation.TestTypeTypeFormatValidator;
import test.validation.TestTypeValidator;
import test.validation.exists.TestTypeOnlyExistsValidator;


/**
 * @version 0.0.0
 */
@RosettaMeta(model=TestType.class)
public class TestTypeMeta implements RosettaMetaData<TestType> {

	@Override
	public List<Validator<? super TestType>> dataRules(ValidatorFactory factory) {
		return Arrays.asList(
		);
	}
	
	@Override
	public List<Function<? super TestType, QualifyResult>> getQualifyFunctions(QualifyFunctionFactory factory) {
		return Collections.emptyList();
	}
	
	@Override
	public Validator<? super TestType> validator(ValidatorFactory factory) {
		return factory.<TestType>create(TestTypeValidator.class);
	}

	@Override
	public Validator<? super TestType> typeFormatValidator(ValidatorFactory factory) {
		return factory.<TestType>create(TestTypeTypeFormatValidator.class);
	}

	@Deprecated
	@Override
	public Validator<? super TestType> validator() {
		return new TestTypeValidator();
	}

	@Deprecated
	@Override
	public Validator<? super TestType> typeFormatValidator() {
		return new TestTypeTypeFormatValidator();
	}
	
	@Override
	public ValidatorWithArg<? super TestType, Set<String>> onlyExistsValidator() {
		return new TestTypeOnlyExistsValidator();
	}
}
