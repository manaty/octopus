package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

public class UpdateSessionResponse extends BaseResponse<Session> {

    private Session result;

    @Override
    public Session result() {
        return result;
    }

    public void setResult(Session result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("result", result)
                .add("jsonrpc", jsonrpc)
                .add("id", id)
                .add("error", error)
                .toString();
    }
}
