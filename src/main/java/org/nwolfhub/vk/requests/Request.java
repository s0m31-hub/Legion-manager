package org.nwolfhub.vk.requests;

import org.apache.http.client.utils.URIBuilder;

import java.io.Serializable;
import java.net.URISyntaxException;

public class Request implements Serializable {
    public String url;
    public final String method;
    public final String[] params;

    public Request(String method, String... params) {
        this.method = method; this.params = params;
    }

    @Override
    public String toString() {
        URIBuilder builder;
        try {
            builder = new URIBuilder("https://api.vk.com/method/" + method);
            for(String param:params) {
                String[] holder = param.split("=");
                builder.addParameter(holder[0], holder[1]);
            }
            return builder.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "0";
    }
}
