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
elif [[ "$DB_TYPE" = "2" ]]
then
    DB_CONFIG_PATH=${BASEDIR}/config/db-h2.yml
else
    echo "Incorrect database type: $DB_TYPE"
    exit 1
fi

read -p "Input headset ID: " HEADSET_ID
read -p "Input the beginning of report time interval in format [yyyy-MM-dd HH:mm:ss]: " FROM
read -p "Input the end of report time interval in format [yyyy-MM-dd HH:mm:ss]: " TO

REPORT_ROOT=${BASEDIR}/reports

JVMARGS="-Djava.net.preferIPv4Stack \
-Dh2.baseDir=${H2_BASEDIR} \
-Dbq.server.reportRoot=$REPORT_ROOT \
"

java ${JVMARGS} -jar ${BASEDIR}/lib/server-1.0-SNAPSHOT.jar \
    --config=${BASEDIR}/config/server.yml \
    --config=${DB_CONFIG_PATH} \
    --generate-report \
    --headset-id=${HEADSET_ID} \
    --from="$FROM" \
    --to="$TO"