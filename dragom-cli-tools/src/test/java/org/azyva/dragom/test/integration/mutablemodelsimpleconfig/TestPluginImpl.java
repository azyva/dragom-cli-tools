package org.azyva.dragom.test.integration.mutablemodelsimpleconfig;

import java.util.Set;

import org.azyva.dragom.model.MutableNode;
import org.azyva.dragom.model.Node;
import org.azyva.dragom.model.config.NodeConfigTransferObject;
import org.azyva.dragom.model.config.PropertyDefConfig;
import org.azyva.dragom.model.config.SimplePropertyDefConfig;
import org.azyva.dragom.model.plugin.impl.NodePluginAbstractImpl;

public class TestPluginImpl extends NodePluginAbstractImpl implements TestPlugin {
	public TestPluginImpl(Node node) {
		super(node);
	}

	@Override
	public MultiValuedAttributesTransferObject getMultiValuedAttributesTransferObject() {
		MultiValuedAttributesTransferObject multiValuedAttributesTransferObject;
		MutableNode mutableNode;
		NodeConfigTransferObject nodeConfigTransferObject;

		multiValuedAttributesTransferObject = new MultiValuedAttributesTransferObject();
		mutableNode = (MutableNode)this.getNode();
		nodeConfigTransferObject = mutableNode.getNodeConfigTransferObject();

		for(PropertyDefConfig propertyDefConfig: nodeConfigTransferObject.getListPropertyDefConfig()) {
			if (propertyDefConfig.getName().startsWith("MULTI_VALUED_ATTRIBUTE.")) {
				String attributeName;
				String[] arrayValue;

				attributeName = propertyDefConfig.getName().substring("MULTI_VALUED_ATTRIBUTE.".length());
				arrayValue = propertyDefConfig.getValue().split(",");

				for (String value: arrayValue) {
					multiValuedAttributesTransferObject.addValue(attributeName, value);
				}
			}
		}

		return multiValuedAttributesTransferObject;
	}

	@Override
	public void setMultiValuedAttributesTransferObject(MultiValuedAttributesTransferObject multiValuedAttributesTransferObject) {
		MutableNode mutableNode;
		NodeConfigTransferObject nodeConfigTransferObject;

		mutableNode = (MutableNode)this.getNode();
		nodeConfigTransferObject = mutableNode.getNodeConfigTransferObject();

		for(PropertyDefConfig propertyDefConfig: nodeConfigTransferObject.getListPropertyDefConfig()) {
			if (propertyDefConfig.getName().startsWith("MULTI_VALUED_ATTRIBUTE.")) {
				nodeConfigTransferObject.removePropertyDefConfig(propertyDefConfig.getName());
			}
		}

		for(String attributeName: multiValuedAttributesTransferObject.getSetAttributeName()) {
			Set<String> setValue;
			StringBuilder stringBuilder;

			setValue = multiValuedAttributesTransferObject.getMultiValuedAttribute(attributeName);
			stringBuilder = new StringBuilder();

			for (String value: setValue) {
				stringBuilder.append(value).append(',');
			}

			if (!setValue.isEmpty()) {
				// Remove the trailing ",".
				stringBuilder.setLength(stringBuilder.length() - 1);
			}

			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("MULTI_VALUED_ATTRIBUTE." + attributeName, stringBuilder.toString(), true));
		}

		mutableNode.setNodeConfigTransferObject(nodeConfigTransferObject);
	}

	@Override
	public Set<String> getCumulativeMultiValuedAttribute(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

}
