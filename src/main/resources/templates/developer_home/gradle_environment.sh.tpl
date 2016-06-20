#!/bin/bash

# ---------------------------------------------
# Calculate the correct home dir of the script

progName='gradle_environment.sh'
prog=\$0

notsourced=\$(echo \$prog|grep \$progName)

if [ -z "\$notsourced" ]
then
  prog=\$BASH_SOURCE
else
  echo "The script is not being sourced! Please call the script 'source \$prog' or '. \$prog'"
fi

realpath=\$(readlink -f "\$prog")

if test ! -f "\$realpath"; then
  echo >&2 "Cannot determine home directory. Please call this script with 'source /absolute/path/to/\$progName'"
fi

# Layout of development environment. Adapt it to fit your needs.
# The default layout assumes, to have a single top level directory
# DEVELOPER_BASE, with all other required directories nested into it.

# DEVELOPER_BASE is only needed for default layout
# ---------------------------------------------
DEVELOPER_BASE=`dirname "\$realpath"`
# ---------------------------------------------

GRADLE_USER_HOME=\${DEVELOPER_BASE}/../gradle_user_home

COMPONENTSET=\${DEVELOPER_BASE}
ASSEMBLY=\${COMPONENTSET}/assembly
SERVER=\${DEVELOPER_BASE}/build/server
JAVADOC=\${DEVELOPER_BASE}//build/javadoc
WORKSPACE=\${DEVELOPER_BASE}/build/workspace
REPO=\${DEVELOPER_BASE}/build/repo

# Set Gradle project properties for all directories that Gradle needs to know
ORG_GRADLE_PROJECT_serverDirectory=\${SERVER}
ORG_GRADLE_PROJECT_sourceDirectories=\${COMPONENTSET}
ORG_GRADLE_PROJECT_buildEnvironmentPropertiesSample=\${ASSEMBLY}/environment.properties.sample
LOCAL_REPO_PATH=\${REPO}

export GRADLE_USER_HOME DEVELOPER_BASE COMPONENTSET ASSEMBLY SERVER JAVADOC WORKSPACE REPO ORG_GRADLE_PROJECT_serverDirectory ORG_GRADLE_PROJECT_sourceDirectories ORG_GRADLE_PROJECT_buildEnvironmentPropertiesSample LOCAL_REPO_PATH

# set aliases 
alias cdRepo="cd \$REPO"
alias cdSet="cd \$COMPONENTSET"
alias cdAssembly="cd \$ASSEMBLY"
alias cdServer="cd \$SERVER"
alias cdDoc="cd \$JAVADOC"
alias openStudio="/opt/intershop/IntershopStudio/IntershopStudio"

echo " "
echo "Gradle environment is set up."
echo "The following aliases are available"
echo "-------------------------------------------------------"
echo "cdRepo     - change directory to local repository"
echo "cdSet      - change directory to project component set"
echo "cdAssembly - change directory to project assembly"
echo "cdServer   - change directory to server directory"
echo "cdDoc      - change directory to java doc directory"
echo "-------------------------------------------------------"
echo "openStudio - open Intershop Studio"
echo "-------------------------------------------------------"
echo " "

if [ -f \${SERVER}/local/bin/environment.sh ]
then
	. \${SERVER}/local/bin/environment.sh
	echo "Legacy Ant environment is set up - (\${SERVER}/local/bin/environment.sh executed)"
else
	echo "Legacy Ant environment is not set up - Please execute [. \${SERVER}/local/bin/environment.sh] after deployment."
fi
