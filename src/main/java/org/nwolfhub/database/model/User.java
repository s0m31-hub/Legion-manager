package org.nwolfhub.database.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users", schema = "\"users\"")
public class User {
    @Id
    public Integer id;
    public String name;
    public String link;
    public boolean banned;
    public Integer rank;

    public Integer getId() {
        return id;
    }

    public User setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getLink() {
        return link;
    }

    public User setLink(String link) {
        this.link = link;
        return this;
    }

    public boolean isBanned() {
        return banned;
    }

    public User setBanned(boolean banned) {
        this.banned = banned;
        return this;
    }

    public Integer getRank() {
        return rank;
    }

    public User setRank(Integer rank) {
        this.rank = rank;
        return this;
    }

    public User() {}
}
