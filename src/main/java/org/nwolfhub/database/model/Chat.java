package org.nwolfhub.database.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chat")
public class Chat {
    @Id
    public Integer id;
    public String name;
    public String members;

    public Chat() {}

    public Integer getId() {
        return id;
    }

    public Chat setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Chat setName(String name) {
        this.name = name;
        return this;
    }

    public String getMembers() {
        return members;
    }

    public Chat setMembers(String members) {
        this.members = members;
        return this;
    }

}
