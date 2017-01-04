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

rem Main start script for Dragom tools. It launches the JVM starting
rem org.azyva.dragom.tool.DragomToolInvoker.
rem
rem This script calls set-env.cmd which can set various variables to configure the
rem Dragom execution environment. See the comments in set-env.sh for more
rem information.
rem
rem Additional parameters can be passed to the JVM with one or more --jvm-option
rem cli option specified as the first arguments to the script, before the Dragom
rem tool name.
rem
rem Synopsis:
rem
rem dragom [ [ --jvm-option <jvm-option> ] ... ] <dragom-tool> [argument] ...

setlocal EnableDelayedExpansion

set DRAGOM_HOME_DIR=%~dp0%..

call %DRAGOM_HOME_DIR%\bin\set-env.cmd

if "%JAVA_HOME%" == "" (
  echo JAVA_HOME not set.
  exit /b 1
)

rem Nous utilisons un fichier temporaire pour obtenir les listes de variables afin
rem de pouvoir rediriger les erreurs (lorsqu'aucune variable n'existe) dans nul,
rem ce qui n'est pas possible avec la commande for /f.

:retry-temp-file
set TEMP_FILE=%TEMP%\dragom-temp-%RANDOM%.tmp
if exist %TEMP_FILE% goto retry-temp-file

set SYSTEM_PROPERTIES_JVM_OPTIONS=

set SYSTEM_PROPERTY_ 2>nul 1>%TEMP_FILE%

for /f "tokens=1* delims==" %%P in (%TEMP_FILE%) do (
  set PROPERTY=%%P
  set PROPERTY=!PROPERTY:SYSTEM_PROPERTY_=!
  set SYSTEM_PROPERTIES_JVM_OPTIONS=!SYSTEM_PROPERTIES_JVM_OPTIONS! -D!PROPERTY!=%%Q
)

del %TEMP_FILE%

set DRAGOM_SYSTEM_PROPERTIES_JVM_OPTIONS=

set DRAGOM_SYSTEM_PROPERTY_ 2>nul 1>%TEMP_FILE%

for /f "tokens=1* delims==" %%P in (%TEMP_FILE%) do (
  set PROPERTY=%%P
  set PROPERTY=!PROPERTY:DRAGOM_SYSTEM_PROPERTY_=!
  set DRAGOM_SYSTEM_PROPERTIES_JVM_OPTIONS=!DRAGOM_SYSTEM_PROPERTIES_JVM_OPTIONS! -Dorg.azyva.dragom.!PROPERTY!=%%Q
)

del %TEMP_FILE%

set MODEL_PROPERTIES_JVM_OPTIONS=

set MODEL_PROPERTY_ 2>nul 1>%TEMP_FILE%

for /f "tokens=1* delims==" %%P in (%TEMP_FILE%) do (
  set PROPERTY=%%P
  set PROPERTY=!PROPERTY:MODEL_PROPERTY_=!
  set MODEL_PROPERTIES_JVM_OPTIONS=!MODEL_PROPERTIES_JVM_OPTIONS! -Dorg.azyva.dragom.model-property.!PROPERTY!=%%Q
)

del %TEMP_FILE%

set RUNTIME_PROPERTIES_JVM_OPTIONS=

set RUNTIME_PROPERTY_ 2>nul 1>%TEMP_FILE%

for /f "tokens=1* delims==" %%P in (%TEMP_FILE%) do (
  set PROPERTY=%%P
  set PROPERTY=!PROPERTY:RUNTIME_PROPERTY_=!
  set RUNTIME_PROPERTIES_JVM_OPTIONS=!RUNTIME_PROPERTIES_JVM_OPTIONS! -Dorg.azyva.dragom.runtime-property.!PROPERTY!=%%Q
)

del %TEMP_FILE%

rem Extracting and handling some arguments, while leaving the remaining arguments
rem as is is difficult for various reasons, including:
rem - %* does not take into account shifted-out arguments
rem - Windows interprets = as an argument separator when parsing a command line
rem   into the positional variables %1, %2, etc.
rem The solution is to take the whole command line (%*) and parse it explicitly.
rem Here again, this is difficult since a for loop breaks on lines, not tokens. So
rem we replace spaces with newlines.

set STATE=
set CLI_JVM_OPTIONS=
set ARGS=

set ORG_ARGS=%*

rem The 2 empty lines after this set are important. This solution was found here:
rem http://stackovervlow.com/questions/2524928/dos-batch-iterate-through-a-delimited-string
set NEWLINE=^


rem When a variable is empty, its evaluation with replacement (see below in for)
rem does not work correctly.
if "%ORG_ARGS%" == "" goto continue1

for /f %%A in ("%ORG_ARGS: =!NEWLINE!%") do (
  if "!STATE!" == "" (
    if "%%A" == "--jvm-option" (
      set STATE=jvm-option
    ) else (
      set ARGS=!ARGS! %%A
      set STATE=arg
    )
  ) else if "!STATE!" == "jvm-option" (
    set CLI_JVM_OPTIONS=!CLI_JVM_OPTIONS! %%A
    set STATE=
  ) else if "!STATE!" == "arg" (
    set ARGS=!ARGS! %%A
  )
)

:continue1

%JAVA_HOME%/bin/java ^
  %JVM_OPTIONS% ^
  %SYSTEM_PROPERTIES_JVM_OPTIONS% ^
  %DRAGOM_SYSTEM_PROPERTIES_JVM_OPTIONS% ^
  %MODEL_PROPERTIES_JVM_OPTIONS% ^
  %RUNTIME_PROPERTIES_JVM_OPTIONS% ^
  %CLI_JVM_OPTIONS% ^
  -classpath "%DRAGOM_HOME_DIR%\classpath;%DRAGOM_HOME_DIR%\lib\*" ^
  org.azyva.dragom.tool.DragomToolInvoker %ARGS%
