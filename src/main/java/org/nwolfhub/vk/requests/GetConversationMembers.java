package org.nwolfhub.vk.requests;

public class GetConversationMembers extends Request {

    public GetConversationMembers(Integer peer_id) {
        super("messages.getConversationMembers", "peer_id=" + peer_id);
    }
}
