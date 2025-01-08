package org.example.projet_java_rag_llm.service;
import org.example.projet_java_rag_llm.Dao.MessageDao;
import org.example.projet_java_rag_llm.model.Messages;

import java.sql.SQLException;
import java.util.List;
public class MessageService {
    private final MessageDao messageDAO;

    // Constructeur pour initialiser le MessageDAO
    public MessageService() {
        this.messageDAO = new MessageDao();
    }
    // Méthode pour sauvegarder un message
    public void saveMessage(Messages message) throws SQLException {
        // Appel au DAO pour sauvegarder le message
        messageDAO.saveMessage(message);
    }

    // Méthode pour récupérer les messages d'une conversation
    public List<Messages> getMessagesByConversation(int sessionID) throws SQLException {
        // Appel au DAO pour récupérer les messages
        return messageDAO.getMessagesBySession(sessionID);
    }


}
