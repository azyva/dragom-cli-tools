/*
 * Copyright 2015 AZYVA INC.
 *
 * This file is part of Dragom.
 *
 * Dragom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dragom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Dragom.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.azyva.dragom.test.integration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.azyva.dragom.model.ClassificationNode;
import org.azyva.dragom.model.Module;
import org.azyva.dragom.model.MutableClassificationNode;
import org.azyva.dragom.model.MutableModel;
import org.azyva.dragom.model.MutableModule;
import org.azyva.dragom.model.NodePath;
import org.azyva.dragom.model.config.MutableClassificationNodeConfig;
import org.azyva.dragom.model.config.MutableConfig;
import org.azyva.dragom.model.config.MutableModuleConfig;
import org.azyva.dragom.model.config.NodeConfigTransferObject;
import org.azyva.dragom.model.config.SimplePluginDefConfig;
import org.azyva.dragom.model.config.SimplePropertyDefConfig;
import org.azyva.dragom.model.config.impl.simple.SimpleConfig;
import org.azyva.dragom.model.impl.simple.SimpleModel;
import org.azyva.dragom.test.integration.mutablemodelsimpleconfig.MultiValuedAttributesTransferObject;
import org.azyva.dragom.test.integration.mutablemodelsimpleconfig.TestPlugin;
import org.azyva.dragom.test.integration.mutablemodelsimpleconfig.TestPluginImpl;


public class IntegrationTestSuiteMutableModelSimpleConfig {
	/*********************************************************************************
	 * Tests MutableModel with SimpleConfig.
	 *********************************************************************************/
	public static void testMutableModelSimpleConfig() {
		SimpleConfig simpleConfig;
		MutableConfig mutableConfig;
		SimpleModel simpleModel;
		MutableModel mutableModel;
		MutableClassificationNodeConfig mutableClassificationNodeConfig;
		MutableModuleConfig mutableModuleConfig;
		MutableClassificationNode mutableClassificationNode;
		MutableModule mutableModule;
		ClassificationNode classificationNode;
		Module module;
		NodeConfigTransferObject nodeConfigTransferObject;
		String propertyValue;
		TestPlugin testPlugin;
		MultiValuedAttributesTransferObject multiValuedAttributesTransferObject;
		Set<String> setValue;

		try {
			IntegrationTestSuite.printTestCategoryHeader("MutableModel with SimpleConfig");

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Create new SimpleModel based on SimpleConfig with initial root ClassificationNodeConfig.");

			simpleConfig = new SimpleConfig();
			mutableConfig = simpleConfig;
			mutableClassificationNodeConfig = mutableConfig.createMutableClassificationNodeConfigRoot();
			nodeConfigTransferObject = mutableClassificationNodeConfig.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName(null);
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "value", false));
			mutableClassificationNodeConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);
			simpleModel = new SimpleModel(simpleConfig, new Properties());
			mutableModel = simpleModel;
			classificationNode = mutableModel.getClassificationNodeRoot();
			propertyValue = classificationNode.getProperty("PROPERTY");

			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Create new SimpleModel based on SimpleConfig without initial root ClassificationNodeConfig.");

			simpleConfig = new SimpleConfig();
			mutableConfig = simpleConfig;
			simpleModel = new SimpleModel(simpleConfig, new Properties());
			mutableModel = simpleModel;
			mutableClassificationNode = mutableModel.createMutableClassificationNodeRoot();
			nodeConfigTransferObject = mutableClassificationNode.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName(null);
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "value", false));
			mutableClassificationNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);
			classificationNode = mutableModel.getClassificationNodeRoot();
			propertyValue = classificationNode.getProperty("PROPERTY");

			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Property inheritance with SimpleModel based on complete SimpleConfig.");

			simpleConfig = new SimpleConfig();
			mutableConfig = simpleConfig;

			mutableClassificationNodeConfig = mutableConfig.createMutableClassificationNodeConfigRoot();
			nodeConfigTransferObject = mutableClassificationNodeConfig.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName(null);
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "value-root", false));
			mutableClassificationNodeConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableClassificationNodeConfig = mutableClassificationNodeConfig.createChildMutableClassificationNodeConfig();
			nodeConfigTransferObject = mutableClassificationNodeConfig.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("Level1");
			mutableClassificationNodeConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableClassificationNodeConfig = mutableClassificationNodeConfig.createChildMutableClassificationNodeConfig();
			nodeConfigTransferObject = mutableClassificationNodeConfig.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("Level2");
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "value-level-2", true));
			mutableClassificationNodeConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableClassificationNodeConfig = mutableClassificationNodeConfig.createChildMutableClassificationNodeConfig();
			nodeConfigTransferObject = mutableClassificationNodeConfig.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("Level3");
			mutableClassificationNodeConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableClassificationNodeConfig = mutableClassificationNodeConfig.createChildMutableClassificationNodeConfig();
			nodeConfigTransferObject = mutableClassificationNodeConfig.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("Level4");
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "value-level-4", false));
			mutableClassificationNodeConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableModuleConfig = mutableClassificationNodeConfig.createChildMutableModuleConfig();
			nodeConfigTransferObject = mutableModuleConfig.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("module-1");
			mutableModuleConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableModuleConfig = mutableClassificationNodeConfig.createChildMutableModuleConfig();
			nodeConfigTransferObject = mutableModuleConfig.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("module-2");
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "value-module-2", false));
			mutableModuleConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			simpleModel = new SimpleModel(simpleConfig, new Properties());
			mutableModel = simpleModel;

			classificationNode = mutableModel.getClassificationNodeRoot();
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for root: " + propertyValue);
			if (!propertyValue.equals("value-root")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-root.");
			}

			classificationNode = mutableModel.getClassificationNode(new NodePath(""));
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for root (accessed with root NodePath): " + propertyValue);
			if (!propertyValue.equals("value-root")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-root.");
			}

			classificationNode = mutableModel.getClassificationNode(new NodePath("Level1/"));
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for Level1: " + propertyValue);
			if (!propertyValue.equals("value-root")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-root.");
			}

			classificationNode = mutableModel.getClassificationNode(new NodePath("Level1/Level2/"));
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for Level2: " + propertyValue);
			if (!propertyValue.equals("value-level-2")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-level-2.");
			}

			classificationNode = mutableModel.getClassificationNode(new NodePath("Level1/Level2/Level3/"));
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for Level3: " + propertyValue);
			if (propertyValue != null) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be null.");
			}

			classificationNode = mutableModel.getClassificationNode(new NodePath("Level1/Level2/Level3/Level4/"));
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for Level4: " + propertyValue);
			if (!propertyValue.equals("value-level-4")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-level-4.");
			}

			module = mutableModel.getModule(new NodePath("Level1/Level2/Level3/Level4/module-1"));
			propertyValue = module.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for module-1: " + propertyValue);
			if (!propertyValue.equals("value-level-4")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-level-4.");
			}

			module = mutableModel.getModule(new NodePath("Level1/Level2/Level3/Level4/module-2"));
			propertyValue = module.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for module-2: " + propertyValue);
			if (!propertyValue.equals("value-module-2")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-module-2.");
			}

			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Property inheritance with constructed SimpleModel initially based on empty SimpleConfig.");

			simpleConfig = new SimpleConfig();
			mutableConfig = simpleConfig;
			simpleModel = new SimpleModel(simpleConfig, new Properties());
			mutableModel = simpleModel;

			mutableClassificationNode = mutableModel.createMutableClassificationNodeRoot();
			nodeConfigTransferObject = mutableClassificationNode.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName(null);
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "value-root", false));
			mutableClassificationNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableClassificationNode = mutableClassificationNode.createChildMutableClassificationNode();
			nodeConfigTransferObject = mutableClassificationNode.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("Level1");
			mutableClassificationNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableClassificationNode = mutableClassificationNode.createChildMutableClassificationNode();
			nodeConfigTransferObject = mutableClassificationNode.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("Level2");
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "value-level-2", true));
			mutableClassificationNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableClassificationNode = mutableClassificationNode.createChildMutableClassificationNode();
			nodeConfigTransferObject = mutableClassificationNode.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("Level3");
			mutableClassificationNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableClassificationNode = mutableClassificationNode.createChildMutableClassificationNode();
			nodeConfigTransferObject = mutableClassificationNode.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("Level4");
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "value-level-4", false));
			mutableClassificationNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableModule = mutableClassificationNode.createChildMutableModule();
			nodeConfigTransferObject = mutableModule.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("module-1");
			mutableModule.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			mutableModule = mutableClassificationNode.createChildMutableModule();
			nodeConfigTransferObject = mutableModule.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("module-2");
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "value-module-2", false));
			mutableModule.setNodeConfigTransferObject(nodeConfigTransferObject, null);

			classificationNode = mutableModel.getClassificationNodeRoot();
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for root: " + propertyValue);
			if (!propertyValue.equals("value-root")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-root.");
			}

			classificationNode = mutableModel.getClassificationNode(new NodePath(""));
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for root (accessed with root NodePath): " + propertyValue);
			if (!propertyValue.equals("value-root")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-root.");
			}

			classificationNode = mutableModel.getClassificationNode(new NodePath("Level1/"));
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for Level1: " + propertyValue);
			if (!propertyValue.equals("value-root")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-root.");
			}

			classificationNode = mutableModel.getClassificationNode(new NodePath("Level1/Level2/"));
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for Level2: " + propertyValue);
			if (!propertyValue.equals("value-level-2")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-level-2.");
			}

			classificationNode = mutableModel.getClassificationNode(new NodePath("Level1/Level2/Level3/"));
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for Level3: " + propertyValue);
			if (propertyValue != null) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be null.");
			}

			classificationNode = mutableModel.getClassificationNode(new NodePath("Level1/Level2/Level3/Level4/"));
			propertyValue = classificationNode.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for Level4: " + propertyValue);
			if (!propertyValue.equals("value-level-4")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-level-4.");
			}

			module = mutableModel.getModule(new NodePath("Level1/Level2/Level3/Level4/module-1"));
			propertyValue = module.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for module-1: " + propertyValue);
			if (!propertyValue.equals("value-level-4")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-level-4.");
			}

			module = mutableModel.getModule(new NodePath("Level1/Level2/Level3/Level4/module-2"));
			propertyValue = module.getProperty("PROPERTY");
			System.out.println("Value of PROPERTY for module-2: " + propertyValue);
			if (!propertyValue.equals("value-module-2")) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of PROPERTY " + propertyValue + " expected to be value-module-2.");
			}

			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Plugin.");

			simpleConfig = new SimpleConfig();
			mutableConfig = simpleConfig;
			simpleModel = new SimpleModel(simpleConfig, new Properties());
			mutableModel = simpleModel;

			mutableClassificationNode = mutableModel.createMutableClassificationNodeRoot();
			nodeConfigTransferObject = mutableClassificationNode.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setPluginDefConfig(new SimplePluginDefConfig(TestPlugin.class, null, TestPluginImpl.class.getName(), false));
			nodeConfigTransferObject.setName(null);
			mutableClassificationNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);
			testPlugin = mutableClassificationNode.getNodePlugin(TestPlugin.class, null);
			multiValuedAttributesTransferObject = testPlugin.getMultiValuedAttributesTransferObject(null);
			multiValuedAttributesTransferObject.addValue("ATTRIBUTE_1", "value-attribute-1-root");
			testPlugin.setMultiValuedAttributesTransferObject(multiValuedAttributesTransferObject, null);

			mutableClassificationNode = mutableClassificationNode.createChildMutableClassificationNode();
			nodeConfigTransferObject = mutableClassificationNode.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("Level1");
			mutableClassificationNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);
			testPlugin = mutableClassificationNode.getNodePlugin(TestPlugin.class, null);
			multiValuedAttributesTransferObject = testPlugin.getMultiValuedAttributesTransferObject(null);
			multiValuedAttributesTransferObject.addValue("ATTRIBUTE_2", "value-attribute-2-level-1");
			testPlugin.setMultiValuedAttributesTransferObject(multiValuedAttributesTransferObject, null);

			mutableClassificationNode = mutableClassificationNode.createChildMutableClassificationNode();
			nodeConfigTransferObject = mutableClassificationNode.getNodeConfigTransferObject(null);
			nodeConfigTransferObject.setName("Level2");
			mutableClassificationNode.setNodeConfigTransferObject(nodeConfigTransferObject, null);
			testPlugin = mutableClassificationNode.getNodePlugin(TestPlugin.class, null);
			multiValuedAttributesTransferObject = testPlugin.getMultiValuedAttributesTransferObject(null);
			multiValuedAttributesTransferObject.addValue("ATTRIBUTE_1", "value-attribute-1-level-2-1");
			multiValuedAttributesTransferObject.addValue("ATTRIBUTE_1", "value-attribute-1-level-2-2");
			testPlugin.setMultiValuedAttributesTransferObject(multiValuedAttributesTransferObject, null);

			setValue = testPlugin.getCumulativeMultiValuedAttribute("ATTRIBUTE_1");

			if (!setValue.equals(new HashSet<String>(Arrays.asList("value-attribute-1-root", "value-attribute-1-level-2-1", "value-attribute-1-level-2-2")))) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of ATTRIBUTE_1 " + setValue + " expected to be " + new HashSet<String>(Arrays.asList("value-attribute-1-root", "value-attribute-1-level-2-1", "value-attribute-1-level-2-2")));
			}

			setValue = testPlugin.getCumulativeMultiValuedAttribute("ATTRIBUTE_2");

			if (!setValue.equals(new HashSet<String>(Arrays.asList("value-attribute-2-level-1")))) {
				throw new RuntimeException(">>>>> TEST FAILURE: Value of ATTRIBUTE_2 " + setValue + " expected to be " + new HashSet<String>(Arrays.asList("value-attribute-2-level-1")));
			}

			IntegrationTestSuite.printTestFooter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

// cleaning caches
// mix of mutable child and dynamically created.
// Plugins (plugin that abstract highlevel concept to edit properties). With indOnlyThisNode also.
// With initialization properties?
// Modify existing Node or NodeConfig. Test cleaning caches.
// Dupliate nodes (same name)
// Null name on non root.

// Null property to cancel inheritance.