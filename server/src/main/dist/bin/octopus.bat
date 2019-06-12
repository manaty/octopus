SET BASEDIR=%~dp0

ECHO Select the database type
ECHO 1: PostgreSQL
ECHO 2: H2
SET /p DB_TYPE=""

IF %DB_TYPE% = "1" (
    SET DB_CONFIG_PATH=%BASEDIR%/config/db-postgres.yml

    java -jar %BASEDIR%\lib\server-1.0-SNAPSHOT.jar \
        --config=%DB_CONFIG_PATH% \
        --lb-default-schema=octopus \
        --lb-update
) ELSE (
    IF %DB_TYPE% = "2" (
        SET DB_CONFIG_PATH=%BASEDIR%/config/db-h2.yml

        java -jar %BASEDIR%\lib\server-1.0-SNAPSHOT.jar \
        --config=%DB_CONFIG_PATH% \
        --lb-update
    ) ELSE (
        ECHO Incorrect database type: %DB_TYPE%
        EXIT 1
    )
)

if %ERRORLEVEL% NEQ 0 (
    ECHO Liquibase command failed, will not run the server...
    EXIT 1
)

SET /p EMOTIV_SECRET="Input Emotiv client secret for app 'com.ea481neuro.octopusync': "
if [%"$EMOTIV_SECRET"%]=[] (
    ECHO Missing Emotiv client secret
    EXIT 1
)

SET REPORT_ROOT=%BASEDIR%\reports
SET JETTY_STATIC_ROOT=%BASEDIR%\site

SET JVMARGS="-Djava.net.preferIPv4Stack \
-Dbq.server.reportRoot=%REPORT_ROOT% \
-Dbq.cortex.emotiv.clientSecret=%EMOTIV_SECRET% \
-Dbq.jetty.staticResourceBase=%JETTY_STATIC_ROOT%
"

java %JVMARGS% -jar %BASEDIR%\lib\server-1.0-SNAPSHOT.jar \
    --config=%BASEDIR%/config/server.yml \
    --config=%DB_CONFIG_PATH% \
    --octopus-server