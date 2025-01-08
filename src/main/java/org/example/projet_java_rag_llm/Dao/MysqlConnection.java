package org.example.projet_java_rag_llm.Dao;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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


    private int getUserIdByUsername(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try (Connection con = MysqlConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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

    public static int getSessionIdByTitle(String title) {
        String query = "SELECT id FROM sessions WHERE title = ?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, title);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");  // Retourne l'ID de la session
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 si la session n'est pas trouvée
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

    // Méthode pour vérifier si l'utilisateur existe avec les identifiants
    public static boolean datafound(String email, String password) {
        // Connexion à la base de données
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Connexion à la base de données
            connection = getConnection();  // Assurez-vous que cette méthode récupère une connexion à votre DB
            String query = "SELECT password FROM users WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);  // Associer l'email à la requête préparée

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Récupérer le mot de passe haché depuis la base de données
                String hashedPassword = resultSet.getString("password");

                // Comparer le mot de passe fourni avec le mot de passe haché
                return BCrypt.checkpw(password, hashedPassword);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Méthode pour vérifier les identifiants de l'utilisateur avec un mot de passe haché
    public static boolean checkUserCredentials(String email, String password) {
        boolean userExists = false;
        String query = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, email);
            try (ResultSet rst = pst.executeQuery()) {
                if (rst.next()) {
                    String hashedPassword = rst.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        userExists = true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExists;
    }

    public static int createSession(int userId, String title) {
        String query = "INSERT INTO sessions (user_id, title) VALUES (?, ?)";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, userId);
            pst.setString(2, title);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Retourne l'ID de la session créée
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 si la session n'a pas pu être créée
    }

    public static boolean saveMessage(int sessionId, int senderId, String content) {
        String query = "INSERT INTO messages (session_id, sender, content) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, sessionId);
            pst.setInt(2, senderId);
            pst.setString(3, content);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getMessagesBySessionId(int sessionId) {
        List<String> messages = new ArrayList<>();
        String query = "SELECT sender, content, timestamp FROM messages WHERE session_id = ? ORDER BY timestamp ASC";

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, sessionId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("sender");
                String content = rs.getString("content");
                String timestamp = rs.getString("timestamp");
                messages.add("Sender: " + sender + " - " + content + " (at " + timestamp + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    // Attribut pour stocker l'utilisateur connecté
    private static String loggedInUsername;

    // Méthode pour définir l'utilisateur connecté
    public static boolean loginUser(String email, String password) {
        String query = "SELECT username, password FROM users WHERE email = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, email);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        loggedInUsername = rs.getString("username");
                        System.out.println("Connexion réussie. Utilisateur connecté : " + loggedInUsername);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Échec de la connexion : email ou mot de passe incorrect.");
        return false;
    }
    // Méthode pour récupérer le nom de l'utilisateur connecté
    public static String getLoggedInUsername() {
        if (loggedInUsername != null) {
            System.out.println("Utilisateur actuellement connecté : " + loggedInUsername);
        } else {
            System.out.println("Aucun utilisateur n'est connecté.");
        }
        return loggedInUsername;
    }

}

