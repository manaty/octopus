package net.manaty.octopusync.service.emotiv;

import net.manaty.octopusync.service.emotiv.message.Response;

/**
 * UNKNOWN_ERROR(-1) is not present in the official API.
 * It is added to be able to process codes not present in the above list.
 *
 * @link https://emotiv.github.io/cortex-docs/#errors
 */
public enum ResponseErrors {

    INVALID_JSON(-32700, "Invalid JSON was received by the server"),
    INVALID_REQUEST(-32600,	"The JSON sent is not a valid Request object"),
    UNKNOWN_METHOD(-32601,	"The method does not exist / is not available"),
    INVALID_METHOD_PARAMETERS(-32602, "Invalid method parameter(s)"),
    INTERNAL_JSONRPC_ERROR(-32603,	"Internal JSON-RPC error"),
    INVALID_CREDENTIALS(-32001, "Incorrect user name or password"),
    INVALID_LICENSE(-32002,"Invalid license key"),
    NO_INTERNET_CONNECTION(-32003,	"No Internet connection"),
    NO_HEADSET_CONNECTED(-32004, "No headset connected"),
    DUPLICATE_ACTIVE_SESSION(-32005, "There is an active session already with this headset"),
    NO_VALID_LICENSE_TO_CREATE_SESSION(-32006,	"No valid license to activate a new session"),
    SESSION_DOES_NOT_EXIST(-32007,	"Session does not exist"),
    SESSION_CLOSED(-32008,	"The session was closed"),
    CURRENT_SESSION_UPDATED(-32009,"Current session has been updated"),
    HEADSET_DISCONNECTED(-32010, "The headset of current session has been disconnected"),
    RECORDING_STOPPED(-32011, "The recording of current session has been stopped"),
    SESSION_IS_INACTIVE(-32012,"The current session must be activated first"),
    INVALID_STATUS(-32013, "Invalid status"),
    INVALID_AUTH_TOKEN(-32014, "Invalid auth token"),
    AUTH_TOKEN_EXPIRED(-32015, "Auth token expired"),
    STREAM_UNAVAILABLE_OR_ALREADY_SUBSCRIBED(-32016, "Stream unavailable or already been subscribed"),
    RECORDING_STARTED(-32017, "The recording of current session has been started"),
    MISSING_REQUIRED_PARAMETERS(-32018, "Missing required parameters"),
    SESSION_LIMIT_REACHED_FOR_DEVICE(-32019, "Session limit has been reached for current device"),
    USB_CONNECTION_REQUIRED(-32020, "The headset has to be connected by USB cable"),
    INVALID_CLIENT_ID_OR_SECRET(-32021, "Invalid client ID or client secret key"),
    DEVICE_NUMBER_LIMIT_EXCEEDED(-32022, "You have exceeded the limit on the number of devices"),
    MISSING_CLIENT_ID(-32023, "Missing client ID"),
    LICENSE_EXPIRED(-32024, "The license is expired"),
    LICENSE_DEBIT_INSUFFICIENT(-32025, "Request ‘debit’ is greater than ‘maxDebit’ on the license"),
    DAILY_DEBIT_INSUFFICIENT(-32026, "Daily debit limit exceeded"),
    LICENSE_USAGE_FORBIDDEN(-32027, "Application does not have permission to use the license"),
    SESSION_SOFT_LIMIT_EXCEEDED(-32028, "Session soft limit exceeded"),
    SESSION_HARD_LIMIT_EXCEEDED(-32029, "Session hard limit exceeded"),
    SESSION_DATA_ALREADY_EXISTS(-32030, "Session data already existed"),
    INVALID_PROFILE_NAME(-32031, "Invalid profile name"),
    LOGOUT_REQUIRED_BEFORE_LOGIN(-32032, "Logout before login with new account"),
    LOGIN_REQUIRED_TO_AUTHORIZE(-32033, "You must be logged in before calling authorize"),
    TOKEN_DOES_NOT_MATCH_USER(-32034, "Token does not match with current user"),
    ACCOUNT_DOES_NOT_MATCH_USER(-32035, "Account does not match with current user"),
    PROFILE_PROCESSING_FAILED(-32036, "Error processing profile"),
    TRAINING_NOT_SUPPORTED_FOR_ACTION(-32037, "Training for this action is not supported"),
    EXTENDER_ID_NOT_FOUND(-32038, "Extender ID not found"),
    REQUEST_TIMEOUT(-32039, "Request timed out"),
    DUPLICATE_SESSION_FOR_DEVICE(-32040, "Multiple sessions cannot be activated on the same headset"),
    EULA_NOT_ACCEPTED(-32041, "EULA has not been accepted by the user"),
    LICENSE_ACCEPTED(-32042, "You have accepted the license"), // WTF?
    LICENSE_MAX_MONTHLY_RECORD_REACHED(-32043, "Your license has been reached to max monthly record"),
    HEADSET_LINKED_WITH_PROFILE(-32044, "Current headset has linked with a profile"),
    HEADSET_NOT_LINKED_WITH_ANY_PROFILE(-32045, "Current headset does not link with any profile"),
    PROFILE_ACCESS_FORBIDDEN(-32046, "Your application does not have access to the profile of another application"),
    UNKNOWN_ERROR(-1, "Unknown error");

    private int code;
    private String description;

    ResponseErrors(int code, String description) {
        this.code = code;
        this.description = "[" + code + ": " + description + "]";
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

    public Response.ResponseError toError() {
        Response.ResponseError error = new Response.ResponseError();
        error.setCode(code);
        error.setMessage(description);
        return error;
    }

    public static ResponseErrors byCode(int code) {
        for (ResponseErrors e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return UNKNOWN_ERROR;
    }

    @Override
    public String toString() {
        return description;
    }
}
