package org.nwolfhub.vk.longpoll;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import org.nwolfhub.vk.VkGroup;
import org.nwolfhub.vk.requests.Request;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class LongPoll implements Serializable {
    private VkGroup vk;
    private OkHttpClient client;
    public Integer groupId;
    private String key;
    public String server;
    public Integer ts;

    public LongPoll(VkGroup vk, Integer groupId) throws IOException {
        this.vk = vk; this.groupId = groupId; this.client = vk.client;
        String lpGet = vk.makeRequest(new Request("groups.getLongPollServer", "group_id=" + groupId));
        JsonObject response = JsonParser.parseString(lpGet).getAsJsonObject().get("response").getAsJsonObject();
        this.ts = response.get("ts").getAsInt();
        this.server = response.get("server").getAsString();
        this.key = response.get("key").getAsString();
    }

    private void reInit() throws IOException {
        String lpGet = vk.makeRequest(new Request("groups.getLongPollServer", "group_id=" + groupId));
        JsonObject response = JsonParser.parseString(lpGet).getAsJsonObject().get("response").getAsJsonObject();
        this.ts = response.get("ts").getAsInt();
        this.server = response.get("server").getAsString();
        this.key = response.get("key").getAsString();
    }

    public List<Update> getUpdates() throws IOException {
        String rawResponse = "";
        try {
            rawResponse = vk.client.newCall(new okhttp3.Request.Builder().url(this.server + "?act=a_check&key=" + this.key + "&ts=" + this.ts + "&wait=25").build()).execute().body().string();
        } catch (SocketTimeoutException timeoutException) {
            rawResponse = null;
        }
        if(rawResponse!=null) {
            JsonObject response = JsonParser.parseString(rawResponse).getAsJsonObject();
            if (response.get("failed") == null) {
                this.ts = response.get("ts").getAsInt();
                List<Update> updates = new ArrayList<>();
                JsonArray updArray = response.get("updates").getAsJsonArray();
                for (JsonElement updateElement : updArray) {
                    JsonObject update = updateElement.getAsJsonObject();
                    updates.add(new Update(update.toString(), update));
                }
                return updates;
            } else {
                Integer failed = response.get("failed").getAsInt();
                switch (failed) {
                    case 1:
                        this.ts = response.get("ts").getAsInt();
                        break;
                    case 2:
                    case 3:
                        reInit();
                        break;
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Excludes all updates from list that doesn't match their type
     * @param type - type of updates you want to get
     * @param updates - original list
     * @return List containing updates of your type
     */
    public static List<Update> convertUpdateByType(Update.Type type, List<Update> updates) {
        List<Update> toReturn = new ArrayList<>();
        for(Update u:updates) {
            if(u.type.equals(type)) {
                toReturn.add(u);
            }
        }
        return toReturn;
    }

    /**
     * Excludes all updates from list that doesn't match their type
     * @param updates - original list
     * @param type - type of updates you want to get
     * @return List containing updates of your type
     */
    public static List<Update> convertUpdateByType(List<Update> updates, Update.Type type) {
        List<Update> toReturn = new ArrayList<>();
        for(Update u:updates) {
            if(u.type.equals(type)) {
                toReturn.add(u);
            }
        }
        return toReturn;
    }

}
