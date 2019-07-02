@ECHO OFF

setlocal enabledelayedexpansion

FOR %%I in (%~dp0..) do set BASEDIR=%%~fI
SET H2_BASEDIR=%BASEDIR%\bin\h2
SET BQ_BASEDIR_PATH=file:/%BASEDIR:\=/%

ECHO Select the database type
ECHO 1: PostgreSQL
ECHO 2: H2
SET /P DB_TYPE=""

IF %DB_TYPE%==1 (
    SET DB_CONFIG_PATH=%BQ_BASEDIR_PATH%/config/db-postgres.yml

    java -jar %BASEDIR%\lib\server-1.0-SNAPSHOT.jar ^
        --config=!DB_CONFIG_PATH! ^
        --lb-default-schema=octopus ^
        --lb-update
) ELSE (
    IF %DB_TYPE%==2 (
        SET DB_CONFIG_PATH=%BQ_BASEDIR_PATH%/config/db-h2.yml

        java -Dh2.baseDir=%H2_BASEDIR% -jar %BASEDIR%\lib\server-1.0-SNAPSHOT.jar ^
        --config=!DB_CONFIG_PATH! ^
        --lb-update
    ) ELSE (
        ECHO Incorrect database type: %DB_TYPE%
        EXIT /B 1
    )
)

if %ERRORLEVEL% NEQ 0 (
    ECHO Liquibase command failed, will not run the server...
    EXIT /B 1
)

SET /p EMOTIV_SECRET="Input Emotiv client secret for app 'com.ea481neuro.octopusync' (hit Enter to read the secret from the configuration): "

ECHO
SET /p MASTER_ADDRESS="Input master server address in format [<host>:<port>] or hit Enter to skip: "

SET REPORT_ROOT=%BASEDIR%\reports
SET JETTY_STATIC_ROOT=%BQ_BASEDIR_PATH%/site

SET JVMARGS=-Djava.net.preferIPv4Stack ^
-Dh2.baseDir=%H2_BASEDIR% ^
-Dbq.server.reportRoot=%REPORT_ROOT% ^
-Dbq.jetty.staticResourceBase=%JETTY_STATIC_ROOT%

IF NOT "%EMOTIV_SECRET%"=="" (
    SET JVMARGS=%JVMARGS% -Dbq.cortex.emotiv.clientSecret=%EMOTIV_SECRET%
)

IF NOT "%MASTER_ADDRESS%"=="" (
    SET JVMARGS=%JVMARGS% -Dbq.grpc.master.address=%MASTER_ADDRESS%
)

java %JVMARGS% -jar %BASEDIR%\lib\server-1.0-SNAPSHOT.jar ^
    --config=%BQ_BASEDIR_PATH%/config/server.yml ^
    --config=%DB_CONFIG_PATH% ^
    --octopus-server