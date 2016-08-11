@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Publish script for Windows
@rem
@rem ##########################################################################

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

if not exist "%DIRNAME%\build\oracleLibs\jars\ojdbc7.jar" goto environmentTestUser
if not exist "%DIRNAME%\build\oracleLibs\jars\ons.jar" goto environmentTestUser
if not exist "%DIRNAME%\build\oracleLibs\jars\ucp.jar" goto environmentTestUser

goto execute

:environmentTestUser
if "%OTN_USER%" == "" goto failureOTN
if "%OTN_PASSWD%" == "" goto failureOTN

:execute
%DIRNAME%\gradlew publish -s
set /A PUBERRORLEVEL=%ERRORLEVEL%
goto exit

:failureOTN
@echo --------------------------------------------------------------------------------
@echo It is necessary to provide the Oracle JDBC files in 'build\oracleLibs\jars' or
@echo set the environment for OTN user and password for the download of the files.
@echo Activate the access to the Oracle Maven Repository on this page:
@echo    https://www.oracle.com/webapps/maven/register/license.html
@echo Specify OTN_USER and OTN_PASSWD in your environment:
@echo    set OTN_USER=...
@echo    set OTN_PASSWD=...
@echo --------------------------------------------------------------------------------

:exit
exit /B %PUBERRORLEVEL%


