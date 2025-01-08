package org.example.projet_java_rag_llm.Dao;

import org.example.projet_java_rag_llm.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

        public int login(String email, String password) {
                String sql = "SELECT password FROM users WHERE email = ?";
                try (Connection conn = MysqlConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                        pstmt.setString(1, email);

                        try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                        String storedHash = rs.getString("password");
                                        if( BCrypt.checkpw(password, storedHash)){
                                                return rs.getInt("id");// Vérification du mot de passe haché
                                        }
                                }
                        }
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la tentative de connexion : " + e.getMessage(), e);
                }
                return -1; // Si aucun utilisateur correspondant n'est trouvé
        }

        public void signup(User user) {
                String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
                try (Connection conn = MysqlConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                        // Génération d'un mot de passe haché
                        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

                        pstmt.setString(1, user.getUsername());
                        pstmt.setString(2, user.getEmail());
                        pstmt.setString(3, hashedPassword);

                        pstmt.executeUpdate();
                } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de l'inscription de l'utilisateur : " + e.getMessage(), e);
                }
        }
}
