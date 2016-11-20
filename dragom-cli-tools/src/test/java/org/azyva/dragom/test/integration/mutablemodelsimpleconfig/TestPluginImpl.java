/*
 * Copyright 2015 - 2017 AZYVA INC. INC.
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

package org.azyva.dragom.test.integration.mutablemodelsimpleconfig;

import java.util.LinkedHashSet;
import java.util.Set;

import org.azyva.dragom.model.ClassificationNode;
import org.azyva.dragom.model.Module;
import org.azyva.dragom.model.MutableNode;
import org.azyva.dragom.model.Node;
import org.azyva.dragom.model.config.NodeConfigTransferObject;
import org.azyva.dragom.model.config.OptimisticLockException;
import org.azyva.dragom.model.config.OptimisticLockHandle;
import org.azyva.dragom.model.plugin.impl.NodePluginAbstractImpl;

// TODO: Should bi-personnality plugins do this, or should Dragom support bare NodePlugin? Probably this, but with a bi-base impl.
public class TestPluginImpl extends NodePluginAbstractImpl implements TestPlugin {
	public TestPluginImpl(ClassificationNode classificationNode) {
		super(classificationNode);
	}

	public TestPluginImpl(Module module) {
		super(module);
	}

	@Override
	public ClassificationNode getClassificationNode() {
		return (ClassificationNode)this.getNode();
	}

	@Override
	public Module getModule() {
		return (Module)this.getNode();
	}

	@Override
	public MultiValuedAttributesTransferObject getMultiValuedAttributesTransferObject(OptimisticLockHandle optimisticLockHandle)
			throws OptimisticLockException {

		return new MultiValuedAttributesTransferObject(((MutableNode)this.getNode()).getNodeConfigTransferObject(optimisticLockHandle));
	}

	@Override
	public void setMultiValuedAttributesTransferObject(MultiValuedAttributesTransferObject multiValuedAttributesTransferObject, OptimisticLockHandle optimisticLockHandle)
			throws OptimisticLockException {
		MutableNode mutableNode;
		NodeConfigTransferObject nodeConfigTransferObject;

		mutableNode = (MutableNode)this.getNode();
		nodeConfigTransferObject = mutableNode.getNodeConfigTransferObject(optimisticLockHandle);
		multiValuedAttributesTransferObject.fillNodeConfigTransferObject(nodeConfigTransferObject);
		mutableNode.setNodeConfigTransferObject(nodeConfigTransferObject, optimisticLockHandle);
	}

	@Override
	public Set<String> getCumulativeMultiValuedAttribute(String attributeName) {
		Set<String> setAttributeNameBeingBuilt;
		Node nodeCurrent;
		TestPlugin testPlugin;
		MultiValuedAttributesTransferObject multiValuedAttributesTransferObject;
		Set<String> setAttributeName;

		setAttributeNameBeingBuilt = new LinkedHashSet<String>();

		nodeCurrent = this.getNode();
		testPlugin = this;

		do {
			if (testPlugin != null) {
				multiValuedAttributesTransferObject = testPlugin.getMultiValuedAttributesTransferObject(null);
				setAttributeName = multiValuedAttributesTransferObject.getMultiValuedAttribute(attributeName);

				if (setAttributeName != null) {
					setAttributeNameBeingBuilt.addAll(setAttributeName);
				}
			}

			nodeCurrent = nodeCurrent.getClassificationNodeParent();

			if (nodeCurrent != null) {
				if (nodeCurrent.isNodePluginExists(TestPlugin.class, null)) {
					testPlugin = nodeCurrent.getNodePlugin(TestPlugin.class,  null);
				} else {
					testPlugin = null;
				}
			}
		} while (nodeCurrent != null);

		return setAttributeNameBeingBuilt;
	}
}
