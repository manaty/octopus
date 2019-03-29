package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

public class LoginResponse extends BaseResponse<Object> {

    private Object result;

    @Override
    public Object result() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("error", error)
                .add("result", (result == null) ? null : "...")
                .toString();
    }
}
