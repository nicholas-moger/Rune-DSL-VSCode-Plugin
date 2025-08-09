package test.validation;

import com.google.common.collect.Lists;
import com.rosetta.model.lib.expression.ComparisonResult;
import com.rosetta.model.lib.path.RosettaPath;
import com.rosetta.model.lib.validation.ValidationResult;
import com.rosetta.model.lib.validation.ValidationResult.ValidationType;
import com.rosetta.model.lib.validation.Validator;
import java.util.List;
import test.SimpleType;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.rosetta.model.lib.expression.ExpressionOperators.checkNumber;
import static com.rosetta.model.lib.validation.ValidationResult.failure;
import static com.rosetta.model.lib.validation.ValidationResult.success;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class SimpleTypeTypeFormatValidator implements Validator<SimpleType> {

	private List<ComparisonResult> getComparisonResults(SimpleType o) {
		return Lists.<ComparisonResult>newArrayList(
				checkNumber("value", o.getValue(), empty(), of(0), empty(), empty())
			);
	}

	@Override
	public List<ValidationResult<?>> getValidationResults(RosettaPath path, SimpleType o) {
		return getComparisonResults(o)
			.stream()
			.map(res -> {
				if (!isNullOrEmpty(res.getError())) {
					return failure("SimpleType", ValidationType.TYPE_FORMAT, "SimpleType", path, "", res.getError());
				}
				return success("SimpleType", ValidationType.TYPE_FORMAT, "SimpleType", path, "");
			})
			.collect(toList());
	}

}
