package test.validation;

import com.google.common.collect.Lists;
import com.rosetta.model.lib.expression.ComparisonResult;
import com.rosetta.model.lib.path.RosettaPath;
import com.rosetta.model.lib.validation.ValidationResult;
import com.rosetta.model.lib.validation.ValidationResult.ValidationType;
import com.rosetta.model.lib.validation.Validator;
import java.util.List;
import test.TestType;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.rosetta.model.lib.expression.ExpressionOperators.checkCardinality;
import static com.rosetta.model.lib.validation.ValidationResult.failure;
import static com.rosetta.model.lib.validation.ValidationResult.success;
import static java.util.stream.Collectors.toList;

public class TestTypeValidator implements Validator<TestType> {

	private List<ComparisonResult> getComparisonResults(TestType o) {
		return Lists.<ComparisonResult>newArrayList(
				checkCardinality("stringProperty", (String) o.getStringProperty() != null ? 1 : 0, 1, 1), 
				checkCardinality("intProperty", (Integer) o.getIntProperty() != null ? 1 : 0, 0, 1), 
				checkCardinality("booleanProperty", (Boolean) o.getBooleanProperty() != null ? 1 : 0, 0, 1)
			);
	}

	@Override
	public List<ValidationResult<?>> getValidationResults(RosettaPath path, TestType o) {
		return getComparisonResults(o)
			.stream()
			.map(res -> {
				if (!isNullOrEmpty(res.getError())) {
					return failure("TestType", ValidationType.CARDINALITY, "TestType", path, "", res.getError());
				}
				return success("TestType", ValidationType.CARDINALITY, "TestType", path, "");
			})
			.collect(toList());
	}

}
