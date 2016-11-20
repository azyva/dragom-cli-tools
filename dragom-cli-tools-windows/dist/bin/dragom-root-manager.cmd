REM Copyright 2015 - 2017 AZYVA INC. INC.
REM
REM This file is part of Dragom.
REM
REM Dragom is free software: you can redistribute it and/or modify
REM it under the terms of the GNU Affero General Public License as published by
REM the Free Software Foundation, either version 3 of the License, or
REM (at your option) any later version.
REM
REM Dragom is distributed in the hope that it will be useful,
REM but WITHOUT ANY WARRANTY; without even the implied warranty of
REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
REM GNU Afferp General Public License for more details.
REM
REM You should have received a copy of the GNU Affero General Public License
REM along with Dragom.  If not, see <http://www.gnu.org/licenses/>.

@echo off
set DRAGOM_HOME=%~dp0%..
%JAVA_HOME%\bin\java -classpath %DRAGOM_HOME%\lib\* -Djava.util.logging.config.file=%DRAGOM_HOME%/config/logging.properties -Dorg.azyva.dragom.UrlModel=file:///%DRAGOM_HOME%/config/model.xml org.azyva.dragom.tool.RootManagerTool %*
