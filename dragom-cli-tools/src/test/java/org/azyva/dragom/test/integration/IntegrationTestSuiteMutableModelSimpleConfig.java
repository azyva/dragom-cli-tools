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

import java.util.Properties;

import org.azyva.dragom.model.ClassificationNode;
import org.azyva.dragom.model.MutableClassificationNode;
import org.azyva.dragom.model.MutableModel;
import org.azyva.dragom.model.config.MutableClassificationNodeConfig;
import org.azyva.dragom.model.config.MutableConfig;
import org.azyva.dragom.model.config.NodeConfigTransferObject;
import org.azyva.dragom.model.config.SimplePropertyDefConfig;
import org.azyva.dragom.model.config.impl.simple.SimpleConfig;
import org.azyva.dragom.model.impl.simple.SimpleModel;


public class IntegrationTestSuiteMutableModelSimpleConfig {
	/*********************************************************************************
	 * Tests MutableModel with SimpleConfig.
	 *********************************************************************************/
	public static void testMutableModelSimpleConfig() {
		SimpleConfig simpleConfig;
		MutableConfig mutableConfig;
		SimpleModel simpleModel;
		MutableModel mutableModel;
		MutableClassificationNode mutableClassificationNode;
		ClassificationNode classificationNode;
		MutableClassificationNodeConfig mutableClassificationNodeConfig;
		NodeConfigTransferObject nodeConfigTransferObject;
		String propertyValue;

		try {
			IntegrationTestSuite.printTestCategoryHeader("MutableModel with SimpleConfig");

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Create new SimpleModel base on SimpleConfig without initial root ClassificationNodeConfig.");

			simpleConfig = new SimpleConfig();
			mutableConfig = simpleConfig;
			simpleModel = new SimpleModel(simpleConfig, new Properties());
			mutableModel = simpleModel;
			mutableClassificationNode = mutableModel.createMutableClassificationNodeRoot();
			nodeConfigTransferObject = mutableClassificationNode.getNodeConfigTransferObject();
			nodeConfigTransferObject.setName(null);
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "VALUE", false));
			mutableClassificationNode.setNodeConfigTransferObject(nodeConfigTransferObject);
			classificationNode = mutableModel.getClassificationNodeRoot();
			propertyValue = classificationNode.getProperty("PROPERTY");

			IntegrationTestSuite.printTestFooter();

			// ################################################################################

			IntegrationTestSuite.printTestHeader("Create new SimpleModel base on SimpleConfig with initial root ClassificationNodeConfig.");

			simpleConfig = new SimpleConfig();
			mutableConfig = simpleConfig;
			mutableClassificationNodeConfig = mutableConfig.createMutableClassificationNodeConfigRoot();
			nodeConfigTransferObject = mutableClassificationNodeConfig.getNodeConfigTransferObject();
			nodeConfigTransferObject.setName(null);
			nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "VALUE", false));
			mutableClassificationNodeConfig.setNodeConfigTransferObject(nodeConfigTransferObject);
			simpleModel = new SimpleModel(simpleConfig, new Properties());
			mutableModel = simpleModel;
			classificationNode = mutableModel.getClassificationNodeRoot();
			propertyValue = classificationNode.getProperty("PROPERTY");

			IntegrationTestSuite.printTestFooter();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

// indOnlyThisNode
// cleaning caches
// creating child modules and classification node
// mix of mutable child and dynamically created.