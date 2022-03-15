package org.nwolfhub.vk.longpoll;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Update implements Serializable {
    public String rawJson;
    public JsonObject parsedJson;
    public Type type;

    public Update(String json, JsonObject aJson) {
        this.rawJson = json; this.parsedJson = aJson;
        try {
            this.type = Type.valueOf(parsedJson.get("type").getAsString());
        } catch (NumberFormatException e) {
            type = Type.not_supported;
        }
    }

    public static class WrongTypeException extends RuntimeException {
        public WrongTypeException(String text) {super(text);}
    }

    @Override
    public String toString() {
        return rawJson;
    }

    public enum Type {
        message_new,
        message_allow,
        message_deny,
        wall_post_new,
        not_supported
    }
}
