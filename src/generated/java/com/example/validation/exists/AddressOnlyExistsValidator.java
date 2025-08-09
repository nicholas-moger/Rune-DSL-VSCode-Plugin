package com.example.validation.exists;

import com.example.Address;
import com.google.common.collect.ImmutableMap;
import com.rosetta.model.lib.path.RosettaPath;
import com.rosetta.model.lib.validation.ExistenceChecker;
import com.rosetta.model.lib.validation.ValidationResult;
import com.rosetta.model.lib.validation.ValidationResult.ValidationType;
import com.rosetta.model.lib.validation.ValidatorWithArg;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.rosetta.model.lib.validation.ValidationResult.failure;
import static com.rosetta.model.lib.validation.ValidationResult.success;

public class AddressOnlyExistsValidator implements ValidatorWithArg<Address, Set<String>> {

	/* Casting is required to ensure types are output to ensure recompilation in Rosetta */
	@Override
	public <T2 extends Address> ValidationResult<Address> validate(RosettaPath path, T2 o, Set<String> fields) {
		Map<String, Boolean> fieldExistenceMap = ImmutableMap.<String, Boolean>builder()
				.put("street", ExistenceChecker.isSet((String) o.getStreet()))
				.put("city", ExistenceChecker.isSet((String) o.getCity()))
				.put("zipCode", ExistenceChecker.isSet((String) o.getZipCode()))
				.build();
		
		// Find the fields that are set
		Set<String> setFields = fieldExistenceMap.entrySet().stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
		
		if (setFields.equals(fields)) {
			return success("Address", ValidationType.ONLY_EXISTS, "Address", path, "");
		}
		return failure("Address", ValidationType.ONLY_EXISTS, "Address", path, "",
				String.format("[%s] should only be set.  Set fields: %s", fields, setFields));
	}
}
