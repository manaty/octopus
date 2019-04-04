package net.manaty.octopusync.service.emotiv.message;

public interface JSONRPC {

    String PROTOCOL_VERSION = "2.0";

    String METHOD_GETUSERLOGIN = "getUserLogin";
    String METHOD_LOGIN = "login";
    String METHOD_LOGOUT = "logout";
    String METHOD_AUTHORIZE = "authorize";
    String METHOD_QUERYSESSIONS = "querySessions";
    String METHOD_CREATESESSION = "createSession";
    String METHOD_SUBSCRIBE = "subscribe";
}
