package net.manaty.octopusync.service.emotiv.message;

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
}
