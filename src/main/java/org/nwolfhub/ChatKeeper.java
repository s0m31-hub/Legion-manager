package org.nwolfhub;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.nwolfhub.database.GroupDao;
import org.nwolfhub.database.model.Chat;
import org.nwolfhub.database.model.User;
import org.nwolfhub.vk.VkGroup;

import com.google.gson.JsonParser;
import org.nwolfhub.vk.requests.GetConversationsById;
import org.nwolfhub.vk.requests.MessagesSend;
import org.nwolfhub.vk.requests.RemoveChatUser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ChatKeeper {
    private static GroupDao dao;
    private static VkGroup vk;
    public static List<Chat> chats;

    public static void initialize(VkGroup vk) throws IOException {
        dao = new GroupDao();
        ChatKeeper.vk = vk;
        chats = dao.getAll("Chat");
        System.out.println("Imported chats. Total amount: " + chats.size());
    }

    private static void recalculateChats() throws IOException {
        for(Chat chat:chats) {
            String gotMembersBody = vk.makeRequest(new GetConversationsById(chat.id));
            JsonArray gotMembers = JsonParser.parseString(gotMembersBody).getAsJsonObject().get("response").getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("chat_settings").getAsJsonObject().get("active_ids").getAsJsonArray();
            List<String> members = IntStream.range(0, gotMembers.size())
                    .mapToObj(gotMembers::get)
                    .map(JsonElement::getAsString)
                    .collect(Collectors.toList());
            String reportedMembers = String.join(", ", members);
            System.out.println(reportedMembers);
            StringBuilder banres = new StringBuilder();
            for(String strId:reportedMembers.split(", ")) {
                Integer id = Integer.valueOf(strId);
                User u;
                if((u = (User) dao.get(User.class, id))!=null) {
                    if(u.isBanned()) {
                        System.out.println(vk.makeRequest(new RemoveChatUser(chat.getId() - 2000000000, u.getId())));
                        banres.append("\nЗабанен: ").append(u.getId());
                    } else {
                        banres.append("\nЧист: ").append(u.getId());
                    }
                }
                else {
                    banres.append("\nНе найден в базе: ").append(id);
                }
            }
            vk.makeRequest(new MessagesSend(chat.getId(), "Результат проверок:" + banres));
        }
    }

    private static void ban() {

    }


}
