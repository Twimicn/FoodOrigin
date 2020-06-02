package com.moe.fo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class User {
    private int uid;
    private String username;
    @JsonIgnore
    private String password;
    private String email;

    public User() {

    }

    public User(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
