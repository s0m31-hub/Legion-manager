package org.nwolfhub.vk.requests;

public class UsersGet extends Request {
    public UsersGet(Integer id) {
        super("users.get", "user_ids=" + id);
    }
    public UsersGet(Integer id, String name_case) {
        super("users.get", "user_ids=" + id, "name_case=" + name_case);
    }
    public UsersGet(Integer id, String name_case, String fields) {
        super("users.get", "user_ids=" + id, "name_case=" + name_case, "fields=" + fields);
    }
    public UsersGet(String id) {
        super("users.get", "user_ids=" + id);
    }
}
