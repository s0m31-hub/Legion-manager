package org.nwolfhub.vk;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.http.client.utils.URIBuilder;
import org.nwolfhub.vk.longpoll.LongPoll;
import org.nwolfhub.vk.longpoll.Update;
import org.nwolfhub.vk.requests.Request;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

public class VkGroup implements Serializable {
    private String token;
    public String v;
    public Integer groupId;
    public OkHttpClient client;

    public String makeRequest(Request r) throws IOException {
        try {
            Response res = client.newCall(new okhttp3.Request.Builder().url(new URIBuilder(r.toString()).addParameter("access_token", token).addParameter("v", v).toString()).build()).execute();
            String toReturn = res.body().string();
            res.close();
            return toReturn;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }

    public LongPoll initLongPoll() throws IOException {
        return new LongPoll(this, this.groupId);
    }

    public VkGroup(String token, Integer groupId) throws IOException {
        this.token = token;
        this.v = "5.131";
        this.client = new OkHttpClient();
        this.groupId = groupId;
        String res = makeRequest(new Request("groups.getById"));
        if (!res.contains("response") && res.contains("error")) {
            throw new IOException("Invalid token"); //yup, 2lazy2parse
        }
    }

    public VkGroup(String token, Integer groupId, OkHttpClient client) {
        this.token=token;
        this.v="5.131";
        this.client = client;
        this.groupId = groupId;
    }
}
