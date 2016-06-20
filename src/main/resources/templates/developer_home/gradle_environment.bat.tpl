@echo off

REM Layout of development environment. Adapt it to fit your needs.
REM The default layout assumes, to have a single top level directory
REM DEVELOPER_BASE, with all other required directories nested into it.

REM DEVELOPER_BASE is only needed for default layout
SET DEVELOPER_BASE=%~dp0

SET GRADLE_USER_HOME=%DEVELOPER_BASE%\\..\\gradle_user_home

SET COMPONENTSET=%DEVELOPER_BASE%
SET ASSEMBLY=%COMPONENTSET%\\assembly
SET SERVER=%DEVELOPER_BASE%\\build\\server
SET JAVADOC=%DEVELOPER_BASE%\\build\\javadoc
SET WORKSPACE=%DEVELOPER_BASE%\\build\\workspace
SET REPO=%DEVELOPER_BASE%\\build\\repo

REM Set Gradle project properties for all directories that Gradle needs to know
SET ORG_GRADLE_PROJECT_serverDirectory=%SERVER%
SET ORG_GRADLE_PROJECT_sourceDirectories=%COMPONENTSET%
SET ORG_GRADLE_PROJECT_buildEnvironmentPropertiesSample=%ASSEMBLY%\\environment.properties.sample
SET LOCAL_REPO_PATH=%REPO%

REM Set aliases
doskey cdRepo=cd/d %REPO%
doskey cdSet= cd/d %COMPONENTSET%
doskey cdAssembly=cd/d %ASSEMBLY%
doskey cdServer=cd/d %SERVER%
doskey cdDoc=cd/d %JAVADOC%
doskey openStudio=c:\\intershop\\IntershopStudio\\IntershopStudio.exe

echo #
echo Gradle environment is set up.
echo The following aliases are available
echo -------------------------------------------------------
echo cdRepo     - change directory to local repository
echo cdSet      - change directory to project component set
echo cdAssembly - change directory to project assembly
echo cdServer   - change directory to server directory
echo cdDoc      - change directory to java doc directory
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