package org.example.projet_java_rag_llm.DBase;
import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.sql.*;

public class MysqlConnection {

    private static Connection con;
    public static Connection getConnection()  {
        return con;
    }
    static { //une seule instance sera créer de la connexion

        try {
            // Charger le driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Établir la connexion
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_rag", "root", "");
            System.out.println("Connexion à la base de données réussie");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour hasher un mot de passe
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Méthode pour ajouter un utilisateur avec mot de passe haché
    public static boolean addUser(String username, String email, String password) {
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            // Hacher le mot de passe avant de l'ajouter
            String hashedPassword = hashPassword(password);

            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, hashedPassword);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;  // Retourne true si l'utilisateur a été ajouté avec succès
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Exemple de méthode pour récupérer des données
    public void fetchData() {
        Connection con = getConnection();
        PreparedStatement pst = null;
        ResultSet rst = null;

        try {
            String query = "SELECT * FROM users";  // Modifier selon votre table
            pst = con.prepareStatement(query);
            rst = pst.executeQuery();

            while (rst.next()) {
                String username = rst.getString("username");
                String email = rst.getString("email");
                System.out.println("Username: " + username + ", Email: " + email);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rst != null) rst.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean checkUserCredentials(String email, String password) {
        boolean userExists = false;
        Connection con = getConnection();
        PreparedStatement pst = null;
        ResultSet rst = null;

        try {
            // Requête SQL pour vérifier si l'utilisateur avec cet email et mot de passe existe
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, email);  // Paramètre pour l'email
            pst.setString(2, password);  // Paramètre pour le mot de passe
            rst = pst.executeQuery();

            if (rst.next()) {
                userExists = true;  // Si un utilisateur est trouvé avec ces identifiants
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rst != null) rst.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userExists;
    }

}

