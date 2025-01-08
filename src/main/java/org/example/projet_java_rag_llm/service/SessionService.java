package org.example.projet_java_rag_llm.service;
import org.example.projet_java_rag_llm.Dao.SessionsDao;
import org.example.projet_java_rag_llm.model.Sessions;

import java.sql.SQLException;
import java.util.List;

public class SessionService {
    private final SessionsDao sessionsDao;

    // Constructeur pour initialiser le SessionsDao
    public SessionService() {
        this.sessionsDao = new SessionsDao();
    }

    // Méthode pour récupérer toutes les sessions d'un utilisateur
    public List<Sessions> getAllSessions(int userID) throws SQLException {
        return this.sessionsDao.getAllSessions(userID);
    }

    // Méthode pour récupérer tous les titres des sessions d'un utilisateur
    public List<String> getAllSessionTitles(int userID) {
        return this.sessionsDao.getAllSessionTitles(userID);
    }


}
