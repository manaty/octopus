package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

import java.util.List;

public class QuerySessionsResponse extends BaseResponse<List<Session>> {

    private List<Session> result;

    @Override
    public List<Session> result() {
        return result;
    }

    public void setResult(List<Session> result) {
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
