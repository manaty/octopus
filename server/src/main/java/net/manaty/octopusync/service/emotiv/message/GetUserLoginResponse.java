package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

import java.util.List;

public class GetUserLoginResponse extends BaseResponse<List<String>> {

    private List<String> result;

    @Override
    public List<String> result() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("result", result)
                .add("error", error)
                .toString();
    }
}
