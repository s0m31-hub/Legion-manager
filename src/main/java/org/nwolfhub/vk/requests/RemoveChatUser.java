package org.nwolfhub.vk.requests;

public class RemoveChatUser extends Request {
    public RemoveChatUser(Integer chat_id, Integer user_id) {
        super("messages.removeChatUser", "chat_id=" + chat_id, "user_id=" + user_id);
    }
}
