package org.nwolfhub;

import org.nwolfhub.database.GroupDao;
import org.nwolfhub.database.model.Chat;
import org.nwolfhub.vk.VkGroup;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
        for(Chat chat:chats) {
            String currentMembers = chat.getMembers();
            String gotMembersBody = vk.makeRequest(new GetChat(chat.id)); //remove this line
            JsonArray gotMembers = JsonParser.parseString(gotMembersBody).getAsJsonObject().get("response").getAsJsonObject().get("users").getAsJsonArray();
            List<String> members = IntStream.range(0, gotMembers.length())
            .mapToObj(gotMembers::get)
            .map(JsonObject::string)
            .collect(Collectors.toList());
            String reportedMembers = String.join(", ", members);
        }
    }

    private static void recalculateChats() {

    }

    private static void ban() {

    }


}
