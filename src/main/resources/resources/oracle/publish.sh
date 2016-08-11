#!/usr/bin/env bash

##############################################################################
##
##  Publish script for UN*X
##
##############################################################################

# Attempt to set DIRNAME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
DIRNAME="`pwd -P`"
cd "$SAVED" >/dev/null


if [ ! -f $DIRNAME/build/oracleLibs/jars/ojdbc7.jar -o \
         ! -f $DIRNAME/build/oracleLibs/jars/ons.jar -o \
         ! -f $DIRNAME/build/oracleLibs/jars/ucp.jar ]; then
         if [ -z "$OTN_USER" -o -z "$OTN_PASSWD" ]; then
                echo "
                --------------------------------------------------------------------------------
                It is necessary to provide the Oracle JDBC files in 'build/oracleLibs/jars' or
                set the environment for OTN user and password for the download of the files.
                Activate the access to the Oracle Maven Repository on this page:
                   https://www.oracle.com/webapps/maven/register/license.html
                Specify OTN_USER and OTN_PASSWD in your environment:
                   export OTN_USER=...
                   export OTN_PASSWD=...
                --------------------------------------------------------------------------------"   
                exit 1
        fi
fi
sh $DIRNAME/gradlew publish -s
exit $?
