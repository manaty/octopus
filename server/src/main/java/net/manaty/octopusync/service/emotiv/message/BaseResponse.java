package net.manaty.octopusync.service.emotiv.message;

public abstract class BaseResponse<T> implements Response<T> {

    public static BaseResponse<?> buildErrorResponse(int id, String jsonrpc, ResponseError error) {
        BaseResponse<?> response = new BaseResponse<Object>() {
            @Override
            public Object result() {
                return null;
            }
        };
        response.setId(id);
        response.setJsonrpc(jsonrpc);
        response.setError(error);
        return response;
    }

    protected String jsonrpc;
    protected long id;
    protected ResponseError error;

    @Override
    public String jsonrpc() {
        return jsonrpc;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public ResponseError error() {
        return error;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setError(ResponseError error) {
        this.error = error;
    }
}
