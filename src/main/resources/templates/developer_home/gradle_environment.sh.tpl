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

SERVER=\${DEVELOPER_BASE}/build/server
WORKSPACE=\${HOME}/${ProjectName}/workspace

# Set Gradle project properties for all directories that Gradle needs to know
ORG_GRADLE_PROJECT_serverDirectory=\${SERVER}
ORG_GRADLE_PROJECT_sourceDirectories=\${DEVELOPER_BASE}
ORG_GRADLE_PROJECT_buildEnvironmentPropertiesSample=\${HOME}/${ProjectName}/environment.properties.sample

export DEVELOPER_BASE SERVER WORKSPACE REPO ORG_GRADLE_PROJECT_serverDirectory ORG_GRADLE_PROJECT_sourceDirectories ORG_GRADLE_PROJECT_buildEnvironmentPropertiesSample

# set aliases 
alias cdServer="cd \$SERVER"
alias openStudio="/opt/intershop/IntershopStudio/IntershopStudio"

echo " "
echo "Gradle environment is set up."
echo "The following aliases are available"
echo "-------------------------------------------------------"
echo "cdServer   - change directory to server directory"
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
