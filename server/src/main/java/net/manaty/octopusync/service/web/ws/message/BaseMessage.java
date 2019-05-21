package net.manaty.octopusync.service.web.ws.message;

public abstract class BaseMessage {

    protected final long id;
    protected final String type;

    public BaseMessage(long id, String type) {
        this.id = id;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
