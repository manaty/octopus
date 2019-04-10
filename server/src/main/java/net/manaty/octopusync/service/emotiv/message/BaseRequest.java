package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class BaseRequest implements Request {

    private long id;
    private Map<String, Object> params;
    private String jsonrpc;

    @SuppressWarnings("unused")
    public BaseRequest() {
        // for Jackson
    }

    public BaseRequest(long id, Map<String, Object> params) {
        this.id = id;
        this.params = params;
        this.jsonrpc = JSONRPC.PROTOCOL_VERSION;
    }

    @Override
    public long id() {
        return id;
    }

    @Nullable
    @Override
    public Map<String, Object> params() {
        return params;
    }

    @Override
    public String jsonrpc() {
        return jsonrpc;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("params", params)
                .toString();
    }
}
