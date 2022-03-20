package org.nwolfhub;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.nwolfhub.database.GroupDao;
import org.nwolfhub.database.model.Chat;
import org.nwolfhub.database.model.User;
import org.nwolfhub.vk.VkGroup;

import com.google.gson.JsonParser;
import org.nwolfhub.vk.requests.GetConversationMembers;
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
        new Thread(ChatKeeper::banSearch).start();
        System.out.println("Imported chats. Total amount: " + chats.size());
    }

    private static void recalculateChats() throws IOException {
        for(Chat chat:chats) {
            String gotMembersBody = vk.makeRequest(new GetConversationMembers(chat.getId()));
            System.out.println(gotMembersBody);
            JsonArray gotMembers = JsonParser.parseString(gotMembersBody).getAsJsonObject().get("response").getAsJsonObject().get("items").getAsJsonArray();
            StringBuilder banres = new StringBuilder();
            for(JsonElement strId:gotMembers) {
                Integer id = strId.getAsJsonObject().get("member_id").getAsInt();
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
    public static void SilentRecalculateChats() throws IOException {
        for(Chat chat:chats) {
            String gotMembersBody = vk.makeRequest(new GetConversationMembers(chat.getId()));
            System.out.println(gotMembersBody);
            JsonArray gotMembers = JsonParser.parseString(gotMembersBody).getAsJsonObject().get("response").getAsJsonObject().get("items").getAsJsonArray();
            StringBuilder banres = new StringBuilder();
            for(JsonElement strId:gotMembers) {
                Integer id = strId.getAsJsonObject().get("member_id").getAsInt();
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
            System.out.println(banres.toString().replace("\n", " \\n"));
        }
    }

    private static void updateChatsInfo() throws IOException {
        for(Chat chat:chats) {
            String chatInfoResponse = vk.makeRequest(new GetConversationsById(chat.getId()));
            String chatName = JsonParser.parseString(chatInfoResponse).getAsJsonObject().get("response").getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("chat_settings").getAsJsonObject().get("title").getAsString();
            String gotMembersBody = vk.makeRequest(new GetConversationMembers(chat.getId()));
            JsonArray gotMembers = JsonParser.parseString(gotMembersBody).getAsJsonObject().get("response").getAsJsonObject().get("items").getAsJsonArray();
            StringBuilder members = new StringBuilder();
            for(JsonElement member:gotMembers) {
                if(!members.toString().equals("")) {
                    members.append(", ");
                }
                members.append(member.getAsJsonObject().get("member_id").getAsString());
            }
            dao.update(chat.setName(chatName).setMembers(members.toString()));
        }
    }

    public static void updateChats() throws IOException {
        chats = dao.getAll("Chat");
        updateChatsInfo();
        recalculateChats();
        System.out.println("Imported chats. Total amount: " + chats.size());
    }

    public static boolean fromRegistered(Integer peer_id) {
        return chats.stream().map(Chat::getId).anyMatch(peer_id::equals);
    }

    private static void banSearch() {
        while (true) {
            try {
                SilentRecalculateChats();
            } catch (IOException e) {
                System.out.println("Не смог перепроверить пользователей!");
                e.printStackTrace();
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
