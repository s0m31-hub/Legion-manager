package org.nwolfhub.vk.requests;

public class GetChat extends Request {
    public GetChat(Integer chat_id) {
        super("messages.getChat", "chat_id=" + chat_id);
    }

    public GetChat(Integer chat_id, String fields) {
        super("messages.getChat", "chat_id=" + chat_id, "fields=" + fields);
    }
}
