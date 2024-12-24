package org.example.java_sem;

import lombok.Data;

@Data
public class Message {
    private String username;
    private String text;

    public Message(String username, String text) {
        this.username = username;
        this.text = text;
    }
    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }
}
