package org.nwolfhub;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nwolfhub.database.ManualDatabase;
import org.nwolfhub.database.GroupDao;
import org.nwolfhub.database.model.Chat;
import org.nwolfhub.database.model.User;
import org.nwolfhub.vk.VkGroup;
import org.nwolfhub.vk.longpoll.NewMessageUpdate;
import org.nwolfhub.vk.requests.*;

import java.io.IOException;
import java.util.Locale;

public abstract class UpdateHandler {
    private static VkGroup vk;
    private static ManualDatabase db;
    private static Logger logger;
    private static GroupDao dao;

    public static void initialize(VkGroup vk, ManualDatabase db) {
        UpdateHandler.vk = vk; UpdateHandler.db = db;
        dao = new GroupDao();
        logger = LogManager.getLogger("Update handler");
        logger.info("Ready to serve!");
    }

    public static void handleUpdate(NewMessageUpdate update) throws IOException {
        Integer from_id = update.message.from_id;
        Integer peer_id = update.message.peer_id;
        String text = update.message.text;
        String payload = update.message.payload;
        NewMessageUpdate.Action action = update.message.action;
        String command = text.toLowerCase(Locale.ROOT);
        if (ChatKeeper.fromRegistered(peer_id)) {
            if(action!=null) {
                if (action.getType().equals("chat_invite_user")) {
                    User invited;
                    if((invited = (User) dao.get(User.class, action.getMember_id())) == null) {
                        UsersManager.regUser(action.getMember_id());
                        vk.makeRequest(new MessagesSend(peer_id, "Зарегестрировал нового участника"));
                    } else {
                        if(invited.isBanned()) {
                            vk.makeRequest(new RemoveChatUser(peer_id-2000000000, action.getMember_id()));
                            vk.makeRequest(new MessagesSend(peer_id, "Данный участник был забанен"));
                        }
                    }
                }
            }
            if (command.equals("!команды") || command.equals("!помощь")) {
                User u;
                if ((u = (User) dao.get(User.class, from_id)) == null) {
                    vk.makeRequest(new MessagesSend(peer_id, "Команды бота:\n!regme - зарегаться в боте"));
                } else if (u.getRank() >= 3 && u.getRank() < 5) {
                    vk.makeRequest(new MessagesSend(peer_id, "Команды бота:\n!я - информация о себе\n!чек - информация о другом пользователе\n!форсрег - зарегестрировать пользователя\n!бан - забанить пользователя\n!разбан - рабанить пользователя"));
                } else if (u.getRank() >= 5) {
                    vk.makeRequest(new MessagesSend(peer_id, "Команды бота:\n!я - информация о себе\n!чек - информация о другом пользователе\n!форсрег - зарегестрировать пользователя\n!форсрег беседа - зарегестрировать всех пользователей в беседе\n!бан - забанить пользователя\n!разбан - рабанить пользователя"));
                } else {
                    vk.makeRequest(new MessagesSend(peer_id, "Команды бота:\n!я - информация о себе"));
                }
            }
            if (command.equals("ping")) {
                vk.makeRequest(new MessagesSend(peer_id, "pong"));
            }
            if (command.equals("!info")) {
                vk.makeRequest(new MessagesSend(peer_id, "peer: " + peer_id + "\nfrom: " + from_id));
            } else if (command.equals("!regme") || command.equals("!я") || command.equals("!me") || command.contains("!чек") || command.contains("!форсрег") || command.contains("!бан") || command.contains("!анбан") || command.contains("!разбан")) {
                UsersManager.handleUpdate(update);
            }
        }
        else if (command.equals("!regchat")) {
            User u;
            if ((u = (User) dao.get(User.class, from_id)) != null) {
                if (!peer_id.equals(from_id)) {
                    if (u.getRank() < 5) {
                        vk.makeRequest(new MessagesSend(peer_id, "Слишком маленький ранг в системе!\nТребуется: 5\nТекущий: " + u.getRank()));
                    } else {
                        Chat cChat;
                        if ((cChat = (Chat) dao.get(Chat.class, peer_id)) == null) {
                            cChat = new Chat();
                            cChat.setId(peer_id);
                            String chatInfoResponse = vk.makeRequest(new GetConversationsById(cChat.getId()));
                            String chatName = JsonParser.parseString(chatInfoResponse).getAsJsonObject().get("response").getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("chat_settings").getAsJsonObject().get("title").getAsString();
                            String gotMembersBody = vk.makeRequest(new GetConversationMembers(cChat.getId()));
                            JsonArray gotMembers = JsonParser.parseString(gotMembersBody).getAsJsonObject().get("response").getAsJsonObject().get("items").getAsJsonArray();
                            StringBuilder members = new StringBuilder();
                            for(JsonElement member:gotMembers) {
                                if(!members.toString().equals("")) {
                                    members.append(", ");
                                }
                                members.append(member.getAsJsonObject().get("member_id").getAsString());
                            }
                            dao.save(cChat.setMembers(members.toString()).setName(chatName));
                            ChatKeeper.updateChats();
                            vk.makeRequest(new MessagesSend(peer_id, "Чат успешно зарегестрирован в системе\nУчастникам: напишите !regme, чтобы зарегестрироваться в системе\nАдминистраторам: напишите !форсрег беседа, чтобы зарегестрировать всех"));
                        } else {
                            vk.makeRequest(new MessagesSend(peer_id, "Чат уже был зарегестрирован в системе"));
                        }
                    }
                }
            } else {
                vk.makeRequest(new MessagesSend(peer_id, "Не зарегестрирован в системе. Обычно это означает, что регистрации работают в пассивном режиме. Короче: !regme в чат"));
            }
        }
    }
}
