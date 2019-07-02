#!/bin/bash

BASEDIR=$(dirname $0)/..
H2_BASEDIR=${BASEDIR}/bin/h2

echo "Select the database type"
echo "1: PostgreSQL"
echo "2: H2"
read -p "" DB_TYPE

if [[ "$DB_TYPE" = "1" ]]
then
    DB_CONFIG_PATH=${BASEDIR}/config/db-postgres.yml

    java -jar ${BASEDIR}/lib/server-1.0-SNAPSHOT.jar \
        --config=${DB_CONFIG_PATH} \
        --lb-default-schema=octopus \
        --lb-update

elif [[ "$DB_TYPE" = "2" ]]
then
    DB_CONFIG_PATH=${BASEDIR}/config/db-h2.yml

    java -Dh2.baseDir=${H2_BASEDIR} -jar ${BASEDIR}/lib/server-1.0-SNAPSHOT.jar \
    --config=${DB_CONFIG_PATH} \
    --lb-update

else
    echo "Incorrect database type: $DB_TYPE"
    exit 1
fi

if [[ $? -ne 0 ]]
then
    echo "Liquibase command failed, will not run the server..."
    exit 1
fi

read -s -p "Input Emotiv client secret for app 'com.ea481neuro.octopusync' (hit Enter to read the secret from the configuration): " EMOTIV_SECRET

echo
read -p "Input master server address in format [<host>:<port>] or hit Enter to skip: " MASTER_ADDRESS

REPORT_ROOT=${BASEDIR}/reports
JETTY_STATIC_ROOT=${BASEDIR}/site

JVMARGS="-Djava.net.preferIPv4Stack \
-Dh2.baseDir=${H2_BASEDIR} \
-Dbq.server.reportRoot=$REPORT_ROOT \
-Dbq.jetty.staticResourceBase=$JETTY_STATIC_ROOT
"

if [[ ! -z "$EMOTIV_SECRET" ]]
then
    JVMARGS="$JVMARGS -Dbq.cortex.emotiv.clientSecret=$EMOTIV_SECRET"
fi

if [[ ! -z "$MASTER_ADDRESS" ]]
then
    JVMARGS="$JVMARGS -Dbq.grpc.master.address=$MASTER_ADDRESS"
fi

java ${JVMARGS} -jar ${BASEDIR}/lib/server-1.0-SNAPSHOT.jar \
    --config=${BASEDIR}/config/server.yml \
    --config=${DB_CONFIG_PATH} \
    --octopus-server