package net.manaty.octopusync.service.emotiv.message;

public class LoginResponse implements Response<Object> {

    private String jsonrpc;
    private long id;
    private Object result;

    @Override
    public String jsonrpc() {
        return null;
    }

    @Override
    public long id() {
        return 0;
    }

    @Override
    public Object result() {
        return null;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
