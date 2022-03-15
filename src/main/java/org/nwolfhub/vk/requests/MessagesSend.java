package org.nwolfhub.vk.requests;

import org.nwolfhub.vk.keyboard.Keyboard;

import java.util.Random;

public class MessagesSend extends Request {
    public MessagesSend(Integer peer_id, String message) {
        super("messages.send", "peer_id=" + peer_id, "message=" + message, "random_id=" + new Random().nextInt());
    }

    public MessagesSend(Integer peer_id, String message, String keyboard) {
        super("messages.send", "peer_id=" + peer_id, "message=" + message, "random_id=" + new Random().nextInt(), "keyboard=" + keyboard);
    }

    public MessagesSend(Integer peer_id, String message, Keyboard keyboard) {
        super("messages.send", "peer_id=" + peer_id, "message=" + message, "random_id=" + new Random().nextInt(), "keyboard=" + keyboard.toString());
    }
}
