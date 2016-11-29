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

import java.util.Set;

import org.azyva.dragom.model.config.OptimisticLockException;
import org.azyva.dragom.model.config.OptimisticLockHandle;
import org.azyva.dragom.model.plugin.ClassificationNodePlugin;
import org.azyva.dragom.model.plugin.ModulePlugin;
import org.azyva.dragom.model.plugin.NodePlugin;

public interface TestPlugin extends NodePlugin, ClassificationNodePlugin, ModulePlugin {
  MultiValuedAttributesTransferObject getMultiValuedAttributesTransferObject(OptimisticLockHandle optimisticLockHandle) throws OptimisticLockException;

  void setMultiValuedAttributesTransferObject(MultiValuedAttributesTransferObject multiValuedAttributesTransferObject, OptimisticLockHandle optimisticLockHandle) throws OptimisticLockException;

  Set<String> getCumulativeMultiValuedAttribute(String attributeName);
}
