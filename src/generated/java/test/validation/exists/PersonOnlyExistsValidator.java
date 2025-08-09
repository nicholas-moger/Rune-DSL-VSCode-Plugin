package test.validation.exists;

import com.google.common.collect.ImmutableMap;
import com.rosetta.model.lib.path.RosettaPath;
import com.rosetta.model.lib.validation.ExistenceChecker;
import com.rosetta.model.lib.validation.ValidationResult;
import com.rosetta.model.lib.validation.ValidationResult.ValidationType;
import com.rosetta.model.lib.validation.ValidatorWithArg;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import test.Person;

import static com.rosetta.model.lib.validation.ValidationResult.failure;
import static com.rosetta.model.lib.validation.ValidationResult.success;

public class PersonOnlyExistsValidator implements ValidatorWithArg<Person, Set<String>> {

	/* Casting is required to ensure types are output to ensure recompilation in Rosetta */
	@Override
	public <T2 extends Person> ValidationResult<Person> validate(RosettaPath path, T2 o, Set<String> fields) {
		Map<String, Boolean> fieldExistenceMap = ImmutableMap.<String, Boolean>builder()
				.put("firstName", ExistenceChecker.isSet((String) o.getFirstName()))
				.put("lastName", ExistenceChecker.isSet((String) o.getLastName()))
				.put("age", ExistenceChecker.isSet((BigDecimal) o.getAge()))
				.put("isActive", ExistenceChecker.isSet((Boolean) o.getIsActive()))
				.build();
		
		// Find the fields that are set
		Set<String> setFields = fieldExistenceMap.entrySet().stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
		
		if (setFields.equals(fields)) {
			return success("Person", ValidationType.ONLY_EXISTS, "Person", path, "");
		}
		return failure("Person", ValidationType.ONLY_EXISTS, "Person", path, "",
				String.format("[%s] should only be set.  Set fields: %s", fields, setFields));
	}
}
