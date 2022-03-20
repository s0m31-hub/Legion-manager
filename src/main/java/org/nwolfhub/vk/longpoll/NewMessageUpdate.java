package org.nwolfhub.vk.longpoll;

import com.google.gson.JsonObject;

import java.io.IOException;

public class NewMessageUpdate extends Update {

    public Message message;
    public ClientInfo client_info;

    public NewMessageUpdate(JsonObject update) throws IOException {
        super(update.toString(), update);
        try {
            if(update.get("type").getAsString().equals("message_new")) {
                JsonObject msg = update.get("object").getAsJsonObject().get("message").getAsJsonObject();
                JsonObject client = update.get("object").getAsJsonObject().get("client_info").getAsJsonObject();
                Action action = msg.get("action") == null?null:new Action(msg.get("action").getAsJsonObject().get("type").getAsString(), msg.get("action").getAsJsonObject().get("member_id").getAsInt());
                this.message = new Message(msg.get("date").getAsLong(), msg.get("from_id").getAsInt(), msg.get("id").getAsInt(), msg.get("out").getAsInt(), msg.get("conversation_message_id").getAsInt(), msg.get("important").getAsBoolean(), msg.get("is_hidden").getAsBoolean(), msg.get("peer_id").getAsInt(), msg.get("random_id").getAsInt(), msg.get("text").getAsString(), action).setPayload(msg.get("payload") == null?"":msg.get("payload").getAsString());
                this.client_info = new ClientInfo(client.get("button_actions").toString(), client.get("keyboard").getAsBoolean(), client.get("inline_keyboard").getAsBoolean(), client.get("carousel").getAsBoolean(), client.get("lang_id").getAsInt());
            } else {
                throw new WrongTypeException("An update you have tried to convert had " + update.get("type").getAsString() + " type instead of message_new");
            }
        } catch (NullPointerException e) {
            throw new IOException(e);
        }
    }

    public NewMessageUpdate() {
        super("", new JsonObject());
        this.message = new Message();
        this.client_info = new ClientInfo();
    }

    public NewMessageUpdate(Update originalUpdate) throws IOException {
        super(originalUpdate.rawJson, originalUpdate.parsedJson);
        JsonObject update = originalUpdate.parsedJson;
        try {
            if(update.get("type").getAsString().equals("message_new")) {
                JsonObject msg = update.get("object").getAsJsonObject().get("message").getAsJsonObject();
                JsonObject client = update.get("object").getAsJsonObject().get("client_info").getAsJsonObject();
                Action action = msg.get("action") == null?null:new Action(msg.get("action").getAsJsonObject().get("type").getAsString(), msg.get("action").getAsJsonObject().get("member_id").getAsInt());
                this.message = new Message(msg.get("date").getAsLong(), msg.get("from_id").getAsInt(), msg.get("id").getAsInt(), msg.get("out").getAsInt(), msg.get("conversation_message_id").getAsInt(), msg.get("important").getAsBoolean(), msg.get("is_hidden").getAsBoolean(), msg.get("peer_id").getAsInt(), msg.get("random_id").getAsInt(), msg.get("text").getAsString(), action).setPayload(msg.get("payload") == null?"":msg.get("payload").getAsString());;
                this.client_info = new ClientInfo(client.get("button_actions").toString(), client.get("keyboard").getAsBoolean(), client.get("inline_keyboard").getAsBoolean(), client.get("carousel").getAsBoolean(), client.get("lang_id").getAsInt());
            } else {
                throw new WrongTypeException("An update you have tried to convert had " + update.get("type").getAsString() + " type instead of message_new");
            }
        } catch (NullPointerException e) {
            throw new IOException(e);
        }
    }


    public static class Action {
        String type;
        Integer member_id;

        public String getType() {
            return type;
        }

        public Action setType(String type) {
            this.type = type;
            return this;
        }

        public Integer getMember_id() {
            return member_id;
        }

        public Action setMember_id(Integer member_id) {
            this.member_id = member_id;
            return this;
        }

        public Action(String type, Integer member_id) {
            this.type = type;
            this.member_id = member_id;
        }
        public Action() {}

    }

    public static class Message {
        public Long date;
        public Integer from_id;
        public Integer id;
        public Integer out;
        public Integer conversation_message_id;
        public boolean important;
        public boolean is_hidden;
        public Integer peer_id;
        public Integer random_id;
        public String payload;
        public String text;
        public Action action;

        public Action getAction() {
            return action;
        }

        public Message setAction(Action action) {
            this.action = action;
            return this;
        }

        public Integer getFrom_id() {
            return from_id;
        }

        public Integer getId() {
            return id;
        }

        public String getPayload() {return payload;}

        public Integer getOut() {
            return out;
        }

        public Integer getConversation_message_id() {
            return conversation_message_id;
        }

        public boolean isImportant() {
            return important;
        }

        public boolean isIs_hidden() {
            return is_hidden;
        }

        public Integer getPeer_id() {
            return peer_id;
        }

        public Integer getRandom_id() {
            return random_id;
        }

        public String getText() {
            return text;
        }

        public Message setDate(Long date) {
            this.date = date;
            return this;
        }

        public Message setFrom_id(Integer from_id) {
            this.from_id = from_id;
            return this;
        }

        public Message setId(Integer id) {
            this.id = id;
            return this;
        }

        public Message setOut(Integer out) {
            this.out = out;
            return this;
        }

        public Message setConversation_message_id(Integer conversation_message_id) {
            this.conversation_message_id = conversation_message_id;
            return this;
        }

        public Message setImportant(boolean important) {
            this.important = important;
            return this;
        }

        public Message setIs_hidden(boolean is_hidden) {
            this.is_hidden = is_hidden;
            return this;
        }

        public Message setPeer_id(Integer peer_id) {
            this.peer_id = peer_id;
            return this;
        }

        public Message setRandom_id(Integer random_id) {
            this.random_id = random_id;
            return this;
        }

        public Message setPayload(String payload) {
            this.payload = payload;
            return this;
        }

        public Message setText(String text) {
            this.text = text;
            return this;
        }

        public Message(Long date, Integer from_id, Integer id, Integer out, Integer conversation_message_id, boolean important, boolean is_hidden, Integer peer_id, Integer random_id, String text, Action action) {
            this.date = date;
            this.from_id = from_id;
            this.id = id;
            this.out = out;
            this.conversation_message_id = conversation_message_id;
            this.important = important;
            this.is_hidden = is_hidden;
            this.peer_id = peer_id;
            this.random_id = random_id;
            this.text = text;
            this.action = action;
        }

        public Message() {}
    }

    public static class ClientInfo {
        String[] button_actions;
        boolean keyboard;
        boolean inline_keyboard;
        boolean carousel;
        Integer lang_id;

        public String[] getButton_actions() {
            return button_actions;
        }

        public boolean isKeyboard() {
            return keyboard;
        }

        public boolean isInline_keyboard() {
            return inline_keyboard;
        }

        public boolean isCarousel() {
            return carousel;
        }

        public Integer getLang_id() {
            return lang_id;
        }

        public ClientInfo(String[] button_actions, boolean keyboard, boolean inline_keyboard, boolean carousel, Integer lang_id) {
            this.button_actions = button_actions;
            this.keyboard = keyboard;
            this.inline_keyboard = inline_keyboard;
            this.carousel = carousel;
            this.lang_id = lang_id;
        }

        public ClientInfo(String button_actions, boolean keyboard, boolean inline_keyboard, boolean carousel, Integer lang_id) {
            this.button_actions = button_actions.replace("[", "").replace("]", "").replace("\"", "").split(",");
            this.keyboard = keyboard;
            this.inline_keyboard = inline_keyboard;
            this.carousel = carousel;
            this.lang_id = lang_id;
        }

        public ClientInfo() {

        }
    }
}
