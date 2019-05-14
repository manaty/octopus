package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

import java.util.List;

public class QueryHeadsetsResponse extends BaseResponse<List<Headset>> {

    private List<Headset> result;

    @Override
    public List<Headset> result() {
        return result;
    }

    public void setResult(List<Headset> result) {
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
