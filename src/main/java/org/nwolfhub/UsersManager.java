package org.nwolfhub;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Request;
import okhttp3.Response;
import org.nwolfhub.database.GroupDao;
import org.nwolfhub.database.model.User;
import org.nwolfhub.vk.VkGroup;
import org.nwolfhub.vk.longpoll.NewMessageUpdate;
import org.nwolfhub.vk.requests.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class UsersManager {
    private static VkGroup vk;
    private static GroupDao dao;

    public static void initialize(VkGroup vk) {
        UsersManager.vk=vk; dao = new GroupDao();
    }

    public static boolean regUser(Integer id) {
        try {
            JsonObject request = JsonParser.parseString(vk.makeRequest(new UsersGet(id))).getAsJsonObject().get("response").getAsJsonArray().get(0).getAsJsonObject();
            String name = request.get("first_name").getAsString() + " " + request.get("last_name").getAsString();
            dao.save(new User().setId(id).setBanned(false).setLink("https://vk.com/id" + id).setName(name).setRank(0));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void handleUpdate(NewMessageUpdate update) throws IOException {
        Integer from_id = update.message.from_id;
        Integer peer_id = update.message.peer_id;
        String text = update.message.text;
        String payload = update.message.payload;
        String command = text.toLowerCase(Locale.ROOT);
        if (command.equals("!regme")) {
            if (dao.get(User.class, from_id) == null) {
                regUser(from_id);
            } else {
                vk.makeRequest(new MessagesSend(peer_id, "Уже зарегестрирован"));
            }
        }
        if (command.equals("!я") || command.equals("!me")) {
            try {
                User u;
                if ((u = (User) dao.get(User.class, from_id)) != null) {
                    vk.makeRequest(new MessagesSend(peer_id, "Инфо о пользователе " + u.getName() + "\nId: " + u.getId() + "\nСсылка: " + u.getLink() + "\nРанг администратора: " + u.getRank()));
                } else {
                    vk.makeRequest(new MessagesSend(peer_id, "Не зарегестрирован в системе. Обычно это означает, что регистрации работают в пассивном режиме. Короче: !regme в чат"));
                }
            } catch (Exception e) {
                vk.makeRequest(new MessagesSend(peer_id, "Произошла ошибка во время транзакции! (" + e + ")"));
                e.printStackTrace();
            }
        } else if (command.contains("!чек")) {
            try {
                String[] holder = command.split(" ");
                if (holder.length == 2) {
                    User u;
                    if ((u = (User) dao.get(User.class, from_id)) != null) {
                        if (u.getRank() < 3) {
                            vk.makeRequest(new MessagesSend(peer_id, "Слишком маленький ранг в системе!\nТребуется: 3\nТекущий: " + u.getRank()));
                        } else {
                            String id;
                            if (holder[1].contains("@")) {
                                id = holder[1].replace("[id", "").split("\\|")[0];
                            } else if (holder[1].contains("vk.com")) {
                                id = JsonParser.parseString(vk.makeRequest(new UsersGet(holder[1].split("vk.com/")[1].split("/")[0]))).getAsJsonObject().get("response").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                            } else {
                                id = null;
                                vk.makeRequest(new MessagesSend(peer_id, "Неверный формат команды"));
                                return;
                            }
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
        } else if (command.contains("!форсрег")) {
            try {
                String[] holder = command.split(" ");
                if (holder.length == 2 && !holder[1].equals("беседа")) {
                    User u;
                    if ((u = (User) dao.get(User.class, from_id)) != null) {
                        if (u.getRank() >= 3) {
                            User toReg;
                            Integer id = Integer.valueOf(holder[1].replace("[id", "").split("\\|")[0]);
                            if ((toReg = (User) dao.get(User.class, id)) == null) {
                                regUser(id);
                            } else {
                                vk.makeRequest(new MessagesSend(peer_id, "Пользователь уже зарегестрирован"));
                            }
                        } else {
                            vk.makeRequest(new MessagesSend(peer_id, "Недостаточно прав"));
                        }
                    } else {
                        vk.makeRequest(new MessagesSend(peer_id, "Не зарегестрирован в системе. Обычно это означает, что регистрации работают в пассивном режиме. Короче: !regme в чат"));
                    }
                } else if (holder.length == 2) {
                    User u;
                    if ((u = (User) dao.get(User.class, from_id)) != null) {
                        if (u.getRank() >= 5) {
                            vk.makeRequest(new MessagesSend(peer_id, "Начинаю регистрацию пользователей в беседе!"));
                            String gotMembersBody = vk.makeRequest(new GetConversationMembers(peer_id));
                            System.out.println(gotMembersBody);
                            JsonArray gotMembers = JsonParser.parseString(gotMembersBody).getAsJsonObject().get("response").getAsJsonObject().get("items").getAsJsonArray();
                            for (JsonElement strId : gotMembers) {
                                Integer id = strId.getAsJsonObject().get("member_id").getAsInt();
                                if (id > 0) {
                                    User currentUser;
                                    if ((currentUser = (User) dao.get(User.class, id)) == null) {
                                        regUser(id);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                vk.makeRequest(new MessagesSend(peer_id, e.toString()));
                e.printStackTrace();
            }
        } else if (command.contains("!бан")) {
            try {
                String[] holder = command.split(" ");
                if (holder.length == 2) {
                    User cUser;
                    User requested;
                    Integer id;
                    if (holder[1].contains("@")) {
                        id = Integer.valueOf(holder[1].replace("[id", "").split("\\|")[0]);
                    } else if (holder[1].contains("vk.com")) {
                        id = Integer.valueOf(JsonParser.parseString(vk.makeRequest(new UsersGet(holder[1].split("vk.com/")[1].split("/")[0]))).getAsJsonObject().get("response").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString());
                    } else {
                        id = null;
                        vk.makeRequest(new MessagesSend(peer_id, "Неверный формат команды"));
                        return;
                    }
                    if ((cUser = (User) dao.get(User.class, from_id)) != null && cUser.getRank() >= 3 && (requested = (User) dao.get(User.class, id)).getRank() != null && cUser.getRank() > requested.getRank()) {
                        String removeres = vk.makeRequest(new RemoveChatUser(peer_id - 2000000000, requested.getId()));
                        if (JsonParser.parseString(removeres).getAsJsonObject().get("response") != null) {
                            dao.update(requested.setBanned(true));
                            ChatKeeper.silentRecalculateChats();
                            vk.makeRequest(new MessagesSend(peer_id, "Пользователь забанен!"));
                        }
                    } else {
                        vk.makeRequest(new MessagesSend(peer_id, "Одно из условий не выполнено:\nОба пользователя зарегестрированы в системе\nВы имеете 3 ранг или больше\nВаш ранг больше выгоняемого участника"));
                    }
                }
            } catch (Exception e) {
                vk.makeRequest(new MessagesSend(peer_id, e.toString()));
            }
        } else if (command.contains("!разбан")) {
            try {
                String[] holder = command.split(" ");
                if (holder.length == 2) {
                    User cUser;
                    User requested;
                    Integer id;
                    if (holder[1].contains("@")) {
                        id = Integer.valueOf(holder[1].replace("[id", "").split("\\|")[0]);
                    } else if (holder[1].contains("vk.com")) {
                        id = Integer.valueOf(JsonParser.parseString(vk.makeRequest(new UsersGet(holder[1].split("vk.com/")[1].split("/")[0]))).getAsJsonObject().get("response").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString());
                    } else {
                        id = null;
                        vk.makeRequest(new MessagesSend(peer_id, "Неверный формат команды"));
                        return;
                    }
                    if ((cUser = (User) dao.get(User.class, from_id)) != null && cUser.getRank() >= 3 && (requested = (User) dao.get(User.class, id)).getRank() != null) {
                        dao.update(requested.setBanned(false));
                        ChatKeeper.silentRecalculateChats();
                        vk.makeRequest(new MessagesSend(peer_id, "Пользователь разбанен!"));
                    } else {
                        vk.makeRequest(new MessagesSend(peer_id, "Одно из условий не выполнено:\nОба пользователя зарегестрированы в системе\nВы имеете 3 ранг или больше"));
                    }
                }
            } catch (Exception e) {
                vk.makeRequest(new MessagesSend(peer_id, e.toString()));
            }
        }
    }
}