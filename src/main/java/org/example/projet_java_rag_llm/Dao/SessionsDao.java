package org.example.projet_java_rag_llm.Dao;

import org.example.projet_java_rag_llm.model.Sessions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class SessionsDao {
    // Méthode pour récupérer toutes les sessions d'un utilisateur
    public List<Sessions> getAllSessions(int userID) throws SQLException {
        List<Sessions> sessions = new ArrayList<Sessions>();
        String sql = "SELECT * FROM sessions WHERE userID = " + userID;
        try (Connection conn = MysqlConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Sessions session = new Sessions(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("userID")
                );
                sessions.add(session); // Correction ici
            }
        }
        return sessions;
    }

    // Méthode pour récupérer tous les titres de sessions d'un utilisateur
    public List<String> getAllSessionTitles(int userID) {
        List<String> titles = new ArrayList<String>();
        String sql = "SELECT title FROM sessions WHERE userID = " + userID;
        try (Connection conn = MysqlConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                titles.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return titles;
    }
}
