@echo off

rem Copyright 2015 - 2017 AZYVA INC. INC.
rem
rem This file is part of Dragom.
rem
rem Dragom is free software: you can redistribute it and/or modify
rem it under the terms of the GNU Affero General Public License as published by
rem the Free Software Foundation, either version 3 of the License, or
rem (at your option) any later version.
rem
rem Dragom is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
rem GNU Afferp General Public License for more details.
rem
rem You should have received a copy of the GNU Affero General Public License
rem along with Dragom.  If not, see <http://www.gnu.org/licenses/>.

rem Sets the Dragom execution environment. Provide such a file to suit your
rem Dragom installation.

rem This file can also be named dragom-set-env.cmd. dragom-set-env.cmd is attempted
rem first and set-env.cmd is used if the former does not exist. This can be useful
rem if Dragom is bundled with other tools.

rem When called the following variables can be used:
rem - DRAGOM_HOME_DIR: Home directory of the Dragom CLI tools. Useful for setting
rem   paths relative to this directory, such as the path to the model file.
rem - DRAGOM_TOOL: Name of the Dragom tool invoked. Useful for having tool-specific
rem   configurations.

rem Must be set to the home of the JVM to use (JRE or JDK).
rem set JAVA_HOME=

rem Passed as options to the JVM (before the main class).
rem set JVM_OPTIONS=

rem Passed as system properties as is.
rem set SYSTEM_PROPERTY_<system property>=

rem Passed as system properties prefixed with "org.azyva.dragom".
set DRAGOM_SYSTEM_PROPERTY_JavaUtilLoggingConfigFile=%DRAGOM_HOME_DIR%\conf\logging.properties
set DRAGOM_SYSTEM_PROPERTY_JavaUtilLoggingFile=%DRAGOM_HOME_DIR%\log\dragom.log

rem Passed as initialization properties (prefixed with
rem "org.azyva.dragom.init-property").
rem set INIT_PROPERTY_URL_MODEL=%DRAGOM_HOME_DIR%\conf\model.xml
rem set INIT_PROPERTY_GIT_REPOS_BASE_URL=https://acme.com/bitbucket/scm
rem set INIT_PROPERTY_GIT_PATH_EXECUTABLE=\path\to\git\executable\git.exe
