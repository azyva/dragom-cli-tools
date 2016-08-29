package org.azyva.dragom.test.integration.mutablemodelsimpleconfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MultiValuedAttributesTransferObject {
	Map<String, Set<String>> mapAttributeValues;

	public MultiValuedAttributesTransferObject() {
		this.mapAttributeValues = new HashMap<String, Set<String>>();
	}

	public Set<String> getSetAttributeName() {
		return Collections.unmodifiableSet(this.mapAttributeValues.keySet());
	}

	public Set<String> getMultiValuedAttribute(String attributeName) {
		Set<String> setValue;

		setValue = this.mapAttributeValues.get(attributeName);

		if (setValue == null) {
			return null;
		} else {
			return Collections.unmodifiableSet(setValue);
		}
	}

	public boolean setMultiValuedAttribute(String attributeName, Set<String> setValue) {
		return this.mapAttributeValues.put(attributeName, new LinkedHashSet<String>(setValue)) == null;
	}

	public boolean removeMultiValuedAttribute(String attributeName) {
		return this.mapAttributeValues.remove(attributeName) != null;
	}

	public boolean addValue(String attributeName, String value) {
		Set<String> setValue;

		setValue = this.mapAttributeValues.get(attributeName);

		if (setValue == null) {
			setValue = new LinkedHashSet<String>();
			this.mapAttributeValues.put(attributeName,  setValue);
		}

		return setValue.add(value);
	}

	public boolean removeValue(String attributeName, String value) {
		Set<String> setValue;

		setValue = this.mapAttributeValues.get(attributeName);

		if (setValue != null) {
			return setValue.remove(value);
		}

		return false;
	}
}
