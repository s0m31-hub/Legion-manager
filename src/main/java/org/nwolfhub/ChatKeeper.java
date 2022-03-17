package org.nwolfhub;

import org.nwolfhub.database.GroupDao;
import org.nwolfhub.database.model.Chat;
import org.nwolfhub.vk.VkGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class ChatKeeper {
    private static GroupDao dao;
    private static VkGroup vk;
    public static List<Chat> chats;

    public static void initialize(VkGroup vk) {
        dao = new GroupDao();
        ChatKeeper.vk = vk;
        chats = dao.getAll("Chat");
        System.out.println("Imported chats. Total amount: " + chats.size());
    }

    private static void recalculateChats() {

    }

    private static void ban() {

    }


}
