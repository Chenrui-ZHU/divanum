package com.bezkoder.spring.login.message;

import com.bezkoder.spring.login.models.Code;

import java.util.Set;

public class ResponseUser {

    private Long id;
    private String username;
//    private Set<Code> codes;

    public ResponseUser(Long id, String username){
        this.id = id;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
