@echo off
REM Layout of development environment. Adapt it to fit your needs.
REM The default layout assumes, to have a single top level directory
REM DEVELOPER_BASE, with all other required directories nested into it.

REM DEVELOPER_BASE is only needed for default layout
SET DEVELOPER_BASE=%~dp0

SET SERVER=%DEVELOPER_BASE%\\build\\server
SET WORKSPACE=%USERPROFILE%\\${ProjectName}\\workspace

REM Set Gradle project properties for all directories that Gradle needs to know
SET ORG_GRADLE_PROJECT_serverDirectory=%SERVER%
SET ORG_GRADLE_PROJECT_sourceDirectories=%DEVELOPER_BASE%
SET ORG_GRADLE_PROJECT_buildEnvironmentPropertiesSample=%USERPROFILE%\\${ProjectName}\\environment.properties.sample

REM Set aliases
doskey cdServer=cd/d %SERVER%
doskey openStudio=c:\\intershop\\IntershopStudio\\IntershopStudio.exe

echo #
echo Gradle environment is set up.
echo The following aliases are available
echo -------------------------------------------------------
echo cdServer   - change directory to server directory
echo -------------------------------------------------------
echo openStudio - open Intershop Studio
echo -------------------------------------------------------
echo #

IF EXIST %SERVER%\\local\\bin\\environment.bat (
CALL %SERVER%\\local\\bin\\environment.bat
echo Legacy Ant environment is set up. ^(Called %SERVER%\\local\\bin\\environment.bat.^)
) ELSE (
echo Legacy Ant environment is not set up. ^(Please call %SERVER%\\local\\bin\\environment.bat after deployment.^)
)