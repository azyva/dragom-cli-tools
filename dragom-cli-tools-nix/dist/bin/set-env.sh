# Copyright 2015 - 2017 AZYVA INC. INC.
#
# This file is part of Dragom.
#
# Dragom is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Dragom is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Afferp General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with Dragom.  If not, see <http://www.gnu.org/licenses/>.

# Sets the Dragom execution environment. Edit this file to suit your
# Dragom installation.

# When called, the DRAGOM_HOME_DIR variable is set to the home directory of
# the Dragom CLI tools.

# Must be set to home of the JVM to use (JRE or JDK).
# JAVA_HOME=

# Passed as options to the JVM (before the main class).
# JVM_OPTIONS=

# Passed as system properties as is.
# declare -A SYSTEM_PROPERTIES

# Passed as system properties prefixed with "org.azyva.dragom".
declare -A DRAGOM_SYSTEM_PROPERTIES
SYSTEM_PROPERTIES[JavaUtilLoggingConfigFile]=$DRAGOM_HOME_DIR/conf/logging.properties
SYSTEM_PROPERTIES[JavaUtilLoggingFile]=$DRAGOM_HOME_DIR/log/dragom.log
# DRAGOM_SYSTEM_PROPERTIES[UrlModel]=/path/to/dragom/model.xml

# Passed as model properties (prefixed with "org.azyva.dragom.model-property").
# declare -A MODEL_PROPERTIES
# MODEL_PROPERTIES[GIT_REPOS_BASE_URL]=https://acme.com/bitbucket/scm

# Passed as runtime properties (prefixed with "org.azyva.dragom.runtime-property").
# declare -A RUNTIME_PROPERTIES
# RUNTIME_PROPERTIES[GIT_PATH_EXECUTABLE]=git
