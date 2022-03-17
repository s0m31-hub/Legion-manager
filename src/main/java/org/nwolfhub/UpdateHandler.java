package org.nwolfhub;

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
import org.nwolfhub.vk.requests.MessagesSend;
import org.nwolfhub.vk.requests.UsersGet;

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
        String command = text.toLowerCase(Locale.ROOT);
        if(command.equals("ping")) {
            vk.makeRequest(new MessagesSend(peer_id, "pong"));
        } else if(command.equals("!regme")) {
            vk.makeRequest(new MessagesSend(peer_id, "Попытка регистрации через hibernate..."));
            if(dao.get(User.class, from_id)==null) {
                try {
                    JsonObject request = JsonParser.parseString(vk.makeRequest(new UsersGet(from_id))).getAsJsonObject().get("response").getAsJsonArray().get(0).getAsJsonObject();
                    String name = request.get("first_name").getAsString() + " " + request.get("last_name").getAsString();
                    dao.save(new User().setId(from_id).setBanned(false).setLink("https://vk.com/id" + from_id).setName(name).setRank(0));
                    vk.makeRequest(new MessagesSend(peer_id, "Успешно. Проверьте базу данных!"));
                } catch (Exception e) {
                    vk.makeRequest(new MessagesSend(peer_id, "Произошла ошибка, логи в файле"));
                    e.printStackTrace();
                }
            } else {
                vk.makeRequest(new MessagesSend(peer_id, "Уже зарегестрирован"));
            }
        }
        if(command.equals("!я") || command.equals("!me")) {
            try {
                User u;
                if((u = (User) dao.get(User.class, from_id))!=null) {
                    vk.makeRequest(new MessagesSend(peer_id, "Инфо о пользователе " + u.getName() + "\nId: " + u.getId() + "\nСсылка: " + u.getLink() + "\nРанг администратора: " + u.getRank()));
                } else {
                    vk.makeRequest(new MessagesSend(peer_id, "Не зарегестрирован в системе. Обычно это означает, что регистрации работают в пассивном режиме. Короче: !regme в чат"));
                }
            } catch (Exception e) {
                vk.makeRequest(new MessagesSend(peer_id, "Произошла ошибка во время транзакции! (" + e + ")"));
                e.printStackTrace();
            }
        }
        if(command.equals("!regchat")) {
            User u;
            if((u = (User) dao.get(User.class, from_id))!=null) {
                if (!peer_id.equals(from_id)) {
                    if (u.getRank() < 3) {
                        vk.makeRequest(new MessagesSend(peer_id, "Слишком маленький ранг в системе!\nТребуется: 3\nТекущий: " + u.getRank()));
                    } else {
                        Chat cChat;
                        if ((cChat = (Chat) dao.get(Chat.class, peer_id))==null) {
                            cChat = new Chat(); cChat.setId(peer_id);
                            
                        }
                    }
                }
            } else {
                vk.makeRequest(new MessagesSend(peer_id, "Не зарегестрирован в системе. Обычно это означает, что регистрации работают в пассивном режиме. Короче: !regme в чат"));
            }
        }
        else if (command.contains("!чек")) {
            try {
                String[] holder = command.split(" ");
                if (holder.length == 2) {
                    User u;
                    if ((u = (User) dao.get(User.class, from_id)) != null) {
                        if (u.getRank() < 3) {
                            vk.makeRequest(new MessagesSend(peer_id, "Слишком маленький ранг в системе!\nТребуется: 3\nТекущий: " + u.getRank()));
                        } else {
                            String id = holder[1].replace("[id", "").split("\\|")[0];
                            System.out.println("Получаем инфо о пользователе " + id);
                            Response r = vk.client.newCall(new Request.Builder().url("https://vk.com/foaf.php?id=" + id).build()).execute();
                            String body = r.body().string();
                            r.close();
                            String regdate = body.split("<ya:created dc:date=\"")[1].split("T")[0];
                            String usergetRes = vk.makeRequest(new UsersGet(Integer.valueOf(id), "nom"));
                            JsonObject userget = JsonParser.parseString(usergetRes).getAsJsonObject().get("response").getAsJsonArray().get(0).getAsJsonObject();
                            String username = userget.get("first_name").getAsString() + " " + userget.get("last_name").getAsString();
                            User requested;
                            boolean registered = false;
                            String extra = "";
                            if ((requested = (User) dao.get(User.class, Integer.valueOf(id))) != null) {
                                registered = true;
                                extra = "\nИнформация из базы:\nИмя и фамилия на момент регистрации: " + requested.getName() + "\nРанг в системе: " + requested.getRank() + "\nЗабанен: " + requested.isBanned();
                            }
                            vk.makeRequest(new MessagesSend(peer_id, "Запрос выполнен. Информация о пользователе " + username + ":\nДата регистрации: " + regdate + (extra.equals("") ? "\nНе состоит в базе легиона" : extra)));
                        }
                    } else {
                        vk.makeRequest(new MessagesSend(peer_id, "Не зарегестрирован в системе. Обычно это означает, что регистрации работают в пассивном режиме. Короче: !regme в чат"));
                    }
                }
            } catch (Exception e) {
                vk.makeRequest(new MessagesSend(peer_id, e.toString()));
                e.printStackTrace();
            }
        }
    }
}
