package org.example.projet_java_rag_llm.model;

import java.security.Timestamp;
import java.sql.CallableStatement;

public class Messages {
    private int id;
    private int sessionId;
    private String sender;
    private String content;

    public Messages(int id, int sessionId, String sender, String content) {
        this.id = id;
        this.sessionId = sessionId;
        this.sender = sender;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
