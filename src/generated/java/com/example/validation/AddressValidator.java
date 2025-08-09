package com.example.validation;

import com.example.Address;
import com.google.common.collect.Lists;
import com.rosetta.model.lib.expression.ComparisonResult;
import com.rosetta.model.lib.path.RosettaPath;
import com.rosetta.model.lib.validation.ValidationResult;
import com.rosetta.model.lib.validation.ValidationResult.ValidationType;
import com.rosetta.model.lib.validation.Validator;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.rosetta.model.lib.expression.ExpressionOperators.checkCardinality;
import static com.rosetta.model.lib.validation.ValidationResult.failure;
import static com.rosetta.model.lib.validation.ValidationResult.success;
import static java.util.stream.Collectors.toList;

public class AddressValidator implements Validator<Address> {

	private List<ComparisonResult> getComparisonResults(Address o) {
		return Lists.<ComparisonResult>newArrayList(
				checkCardinality("street", (String) o.getStreet() != null ? 1 : 0, 1, 1), 
				checkCardinality("city", (String) o.getCity() != null ? 1 : 0, 1, 1), 
				checkCardinality("zipCode", (String) o.getZipCode() != null ? 1 : 0, 1, 1)
			);
	}

	@Override
	public List<ValidationResult<?>> getValidationResults(RosettaPath path, Address o) {
		return getComparisonResults(o)
			.stream()
			.map(res -> {
				if (!isNullOrEmpty(res.getError())) {
					return failure("Address", ValidationType.CARDINALITY, "Address", path, "", res.getError());
				}
				return success("Address", ValidationType.CARDINALITY, "Address", path, "");
			})
			.collect(toList());
	}

}
