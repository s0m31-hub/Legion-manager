package org.nwolfhub.vk.requests;

public class GetConversationsById extends Request {
    public GetConversationsById(Integer chat_id) {
        super("messages.getConversationsById", "peer_ids=" + chat_id);
    }

    public GetConversationsById(Integer chat_id, String fields) {
        super("messages.getConversationsById", "peer_ids=" + chat_id, "fields=" + fields);
    }
}
