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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.azyva.dragom.model.config.NodeConfigTransferObject;
import org.azyva.dragom.model.config.PropertyDefConfig;
import org.azyva.dragom.model.config.SimplePropertyDefConfig;

public class MultiValuedAttributesTransferObject {
  private Map<String, Set<String>> mapAttributeValues;

  public MultiValuedAttributesTransferObject(NodeConfigTransferObject nodeConfigTransferObject) {
    this.mapAttributeValues = new HashMap<String, Set<String>>();

    for(PropertyDefConfig propertyDefConfig: nodeConfigTransferObject.getListPropertyDefConfig()) {
      if (propertyDefConfig.getName().startsWith("MULTI_VALUED_ATTRIBUTE.")) {
        String attributeName;
        String[] arrayValue;

        attributeName = propertyDefConfig.getName().substring("MULTI_VALUED_ATTRIBUTE.".length());
        arrayValue = propertyDefConfig.getValue().split(",");

        for (String value: arrayValue) {
          this.addValue(attributeName, value);
        }
      }
    }
  }

  public void fillNodeConfigTransferObject(NodeConfigTransferObject nodeConfigTransferObject) {
    for(PropertyDefConfig propertyDefConfig: nodeConfigTransferObject.getListPropertyDefConfig()) {
      if (propertyDefConfig.getName().startsWith("MULTI_VALUED_ATTRIBUTE.")) {
        nodeConfigTransferObject.removePropertyDefConfig(propertyDefConfig.getName());
      }
    }

    for(String attributeName: this.getSetAttributeName()) {
      Set<String> setValue;
      StringBuilder stringBuilder;

      setValue = this.getMultiValuedAttribute(attributeName);
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
