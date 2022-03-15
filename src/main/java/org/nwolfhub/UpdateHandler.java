package org.nwolfhub;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nwolfhub.vk.VkGroup;
import org.nwolfhub.vk.longpoll.NewMessageUpdate;
import org.nwolfhub.vk.requests.MessagesSend;

import java.io.IOException;
import java.util.Locale;

public class UpdateHandler {
    private static VkGroup vk;
    private static ManualDatabase db;
    private static Logger logger;

    public static void initialize(VkGroup vk, ManualDatabase db) {
        UpdateHandler.vk = vk; UpdateHandler.db = db;
        logger = LogManager.getLogger("Update handler");
        logger.info("Ready to serve!");
    }

    public static void handleUpdate(NewMessageUpdate update) throws IOException {
        Integer from_id = update.message.from_id;
        Integer peer_id = update.message.peer_id;
        String text = update.message.text;
        String payload = update.message.payload;
        String command = text.toLowerCase(Locale.ROOT);
        if(command.equals("ping")) {
            vk.makeRequest(new MessagesSend(peer_id, "pong"));
        }
    }
}
