package org.nwolfhub.vk.requests;

public class GetConversationsById extends Request {
    public GetConversationsById(Integer chat_id) {
        super("messages.getConversationsById", "peer_ids=" + chat_id);
    }

    public GetConversationsById(Integer chat_id, boolean extended) {
        super("messages.getConversationsById", "peer_ids=" + chat_id, "extended=" + (extended?1:0));
    }

    public GetConversationsById(Integer chat_id, String fields) {
        super("messages.getConversationsById", "peer_ids=" + chat_id, "fields=" + fields);
    }

    public GetConversationsById(Integer chat_id, String fields, boolean extended) {
        super("messages.getConversationsById", "peer_ids=" + chat_id, "fields=" + fields, "extended=" + (extended?1:0));
    }

}
