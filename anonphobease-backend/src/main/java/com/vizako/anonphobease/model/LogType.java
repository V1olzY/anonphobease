package com.vizako.anonphobease.model;


import lombok.Getter;

@Getter
public enum LogType {
    USER_LOGIN("User logged in"),
    USER_LOGOUT("User logged out"),
    REPORT_SENT("Report sent"),
    CONNECTION_ESTABLISHED("Connection established"),
    CONNECTION_CLOSED("Connection closed"),
    PHOBIA_SAVE("Phobia saved"),
    PHOBIA_DELETE("Phobia deleted"),
    PHOBIA_UPDATE("Phobia updated"),
    LANGUAGE_SAVE("Language saved"),
    LANGUAGE_UPDATE("Language updated"),
    LANGUAGE_DELETE("Language deleted"),
    CHAT_SAVE("Chat saved"),
    CHAT_UPDATE("Chat updated"),
    CHAT_DELETE("Chat deleted"),
    ROLE_SAVE("Role saved"),
    ROLE_UPDATE("Role updated"),
    ROLE_DELETE("Role deleted"),;


    private final String template;

    LogType(String template) {
        this.template = template;
    }

}


