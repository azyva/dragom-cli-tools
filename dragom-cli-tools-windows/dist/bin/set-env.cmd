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

rem When called, the DRAGOM_HOME_DIR variable is set to the home directory of
rem the Dragom CLI tools.

rem Must be set to home of the JVM to use (JRE or JDK).
rem set JAVA_HOME=
set JAVA_HOME=java-home

rem Passed as options to the JVM (before the main class).
rem set JVM_OPTIONS=
set JVM_OPTIONS=jvm-option

rem Passed as system properties as is.
rem set SYSTEM_PROPERTY_<system property>=

rem Passed as system properties prefixed with "org.azyva.dragom".
set DRAGOM_SYSTEM_PROPERTY_JavaUtilLoggingConfigFile=%DRAGOM_HOME_DIR%\conf\logging.properties
set DRAGOM_SYSTEM_PROPERTY_JavaUtilLoggingFile=%DRAGOM_HOME_DIR%\log\dragom.log
rem set DRAGOM_SYSTEM_PROPERTY_UrlModel=%DRAGOM_HOME_DIR%\conf\model.xml

rem Passed as model properties (prefixed with "org.azyva.dragom.model-property").
rem set MODEL_PROPERTY_GIT_REPOS_BASE_URL=https://acme.com/bitbucket/scm

rem Passed as runtime properties (prefixed with "org.azyva.dragom.runtime-property").
set RUNTIME_PROPERTY_GIT_PATH_EXECUTABLE=git.exe
