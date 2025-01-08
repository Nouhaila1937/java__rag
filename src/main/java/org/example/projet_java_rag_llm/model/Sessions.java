package org.example.projet_java_rag_llm.model;

public class Sessions {
    private int id;
    private String title;
    private int userID;

    // Constructeur modifié pour initialiser les attributs
    public Sessions(int id, String title, int userID) {
        this.id = id;
        this.title = title;
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
