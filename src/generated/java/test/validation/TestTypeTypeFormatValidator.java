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
import static com.rosetta.model.lib.expression.ExpressionOperators.checkNumber;
import static com.rosetta.model.lib.validation.ValidationResult.failure;
import static com.rosetta.model.lib.validation.ValidationResult.success;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class TestTypeTypeFormatValidator implements Validator<TestType> {

	private List<ComparisonResult> getComparisonResults(TestType o) {
		return Lists.<ComparisonResult>newArrayList(
				checkNumber("intProperty", o.getIntProperty(), empty(), of(0), empty(), empty())
			);
	}

	@Override
	public List<ValidationResult<?>> getValidationResults(RosettaPath path, TestType o) {
		return getComparisonResults(o)
			.stream()
			.map(res -> {
				if (!isNullOrEmpty(res.getError())) {
					return failure("TestType", ValidationType.TYPE_FORMAT, "TestType", path, "", res.getError());
				}
				return success("TestType", ValidationType.TYPE_FORMAT, "TestType", path, "");
			})
			.collect(toList());
	}

}
