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
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with Dragom.  If not, see <http://www.gnu.org/licenses/>.

# Sets the Dragom execution environment. Provide such a file to suit your
# Dragom installation.

# This file can also be named dragom-set-env.sh. dragom-set-env.sh is attempted
# first and set-env.sh is used if the former does not exist. This can be useful if
# Dragom is bundled with other tools.

# When called the following variables can be used:
# - DRAGOM_HOME_DIR: Home directory of the Dragom CLI tools. Useful for setting
#   paths relative to this directory, such as the path to the model file.
# - DRAGOM_TOOL: Name of the Dragom tool invoked. Useful for having tool-specific
#   configurations.

# Must be set to the home of the JVM to use (JRE or JDK).
# JAVA_HOME=

# Passed as options to the JVM (before the main class).
# JVM_OPTIONS=

# Passed as system properties as is.
# declare -A SYSTEM_PROPERTIES

# Passed as system properties prefixed with "org.azyva.dragom.".
declare -A DRAGOM_SYSTEM_PROPERTIES
DRAGOM_SYSTEM_PROPERTIES[JavaUtilLoggingConfigFile]=$DRAGOM_HOME_DIR/conf/logging.properties
DRAGOM_SYSTEM_PROPERTIES[JavaUtilLoggingFile]=$DRAGOM_HOME_DIR/log/dragom.log

# Passed as initialization properties (prefixed with
# "org.azyva.dragom.init-property.").
# declare -A INIT_PROPERTIES
# INIT_PROPERTIES[URL_MODEL]=$DRAGOM_HOME_DIR/conf/model.xml
# INIT_PROPERTIES[GIT_REPOS_BASE_URL]=https://acme.com/bitbucket/scm
# INIT_PROPERTIES[GIT_PATH_EXECUTABLE]=/path/to/git/executable/git
