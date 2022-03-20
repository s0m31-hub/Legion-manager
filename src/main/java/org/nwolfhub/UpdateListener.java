package org.nwolfhub;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nwolfhub.database.ManualDatabase;
import org.nwolfhub.vk.VkGroup;
import org.nwolfhub.vk.longpoll.LongPoll;
import org.nwolfhub.vk.longpoll.NewMessageUpdate;
import org.nwolfhub.vk.longpoll.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public abstract class UpdateListener {
    private static VkGroup vk;
    private static ManualDatabase db;
    private static LongPoll lp;
    private static Logger logger;

    private static List<NewMessageUpdate> convert(List<Update> updates) {
        List<NewMessageUpdate> converted = new ArrayList<>();
        for(Update u:updates) {
            try {
                converted.add(new NewMessageUpdate(u));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return converted;
    }


    public static void initialize(VkGroup vk, ManualDatabase db) throws IOException {
        UpdateListener.vk = vk; UpdateListener.db = db;
        Logger logger = LogManager.getLogger("Update listener");
        lp = vk.initLongPoll();
        new Thread(UpdateListener::listen).start();
        logger.info("Started listening!");
    }

    private static void listen() {
        while (true) {
            try {
                List<Update> all = lp.getUpdates();
                List<NewMessageUpdate> updates = convert(LongPoll.convertUpdateByType(all, Update.Type.message_new));
                for (NewMessageUpdate update:updates) {
                    try {
                        System.out.println("Новое сообщение: " + "Отправитель: " + update.message.peer_id + ", текст: " + update.message.text + ", полезная нагрузка: " + update.message.payload + ", дата: " + update.message.date);
                        UpdateHandler.handleUpdate(update);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("Exception while receiving updates");
                e.printStackTrace();
            }
        }
    }
}
