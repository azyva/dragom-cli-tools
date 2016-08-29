package org.azyva.dragom.test.integration.mutablemodelsimpleconfig;

import java.util.Set;

import org.azyva.dragom.model.plugin.NodePlugin;

public interface TestPlugin extends NodePlugin {
	MultiValuedAttributesTransferObject getMultiValuedAttributesTransferObject();

	void setMultiValuedAttributesTransferObject(MultiValuedAttributesTransferObject multiValuedAttributesTransferObject);

	Set<String> getCumulativeMultiValuedAttribute(String attributeName);
}
