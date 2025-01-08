package org.example.projet_java_rag_llm.Dao;

import org.example.projet_java_rag_llm.model.Messages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    private String sql;

    public void saveMessage(Messages message) {
        String sql;

        // Vérifier si le contenu du message est présent
        if (message.getContent() != null) {
            // Cas : message avec contenu texte
            sql = "INSERT INTO messages (sessionId, sender, content) VALUES (?, ?, ?)";
        } else {
            // Cas : erreur si aucun contenu n'est fourni
            throw new IllegalArgumentException("Le message doit contenir du texte.");
        }

        try (Connection conn = MysqlConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Remplir les paramètres de la requête
            pstmt.setInt(1, message.getSessionId()); // ID de la session
            pstmt.setString(2, message.getSender()); // ID de l'expéditeur
            pstmt.setString(3, message.getContent()); // Contenu du message

            // Exécuter la requête
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // Gérer les exceptions SQL
            throw new RuntimeException("Erreur lors de l'enregistrement du message : " + e.getMessage(), e);
        }
    }

// Méthode pour récupérer les messages d'une session
public List<Messages> getMessagesBySession(int sessionId) {
    String sql = "SELECT * FROM messages WHERE sessionId = ?";
    List<Messages> messages = new ArrayList<>();

    try (Connection conn = MysqlConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, sessionId); // ID de la session

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Messages message = new Messages(
                        rs.getInt("id"),         // ID du message
                        rs.getInt("sessionId"), // ID de la session
                        rs.getString("sender"),  // ID de l'expéditeur
                        rs.getString("content") // Contenu du message
                );
                messages.add(message);
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("Erreur lors de la récupération des messages : " + e.getMessage(), e);
    }

    return messages;
}
}