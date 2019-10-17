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
) ELSE (
    IF %DB_TYPE%==2 (
        SET DB_CONFIG_PATH=%BQ_BASEDIR_PATH%/config/db-h2.yml
    ) ELSE (
        ECHO Incorrect database type: %DB_TYPE%
        EXIT /B 1
    )
)

SET /p HEADSET_ID="Input headset ID: "
SET /p FROM="Input the beginning of report time interval in format [yyyy-MM-dd HH:mm:ss]: "
SET /p TO="Input the end of report time interval in format [yyyy-MM-dd HH:mm:ss]: "

SET REPORT_ROOT=%BASEDIR%\reports

SET JVMARGS=-Djava.net.preferIPv4Stack ^
-Dh2.baseDir=%H2_BASEDIR% ^
-Dbq.server.reportRoot=%REPORT_ROOT%

java %JVMARGS% -jar %BASEDIR%\lib\server-1.0-SNAPSHOT.jar ^
    --config=%BQ_BASEDIR_PATH%/config/server.yml ^
    --config=%DB_CONFIG_PATH% ^
    --generate-report ^
    --headset-id=%HEADSET_ID% ^
    --from="%FROM%" ^
    --to="%TO%"