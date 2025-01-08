package org.example.projet_java_rag_llm.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javafx.stage.Stage;
import javafx.scene.Scene;
import org.example.projet_java_rag_llm.Dao.MysqlConnection;
import org.example.projet_java_rag_llm.service.*;
public class HelloController {

    @FXML
    private VBox rootPane;  // Ajoutez cette ligne pour référencer rootPane

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendButton;

    @FXML
    private ScrollPane messageScrollPane;  // Ajout du ScrollPane pour faire défiler

    @FXML
    private Button logoutButton;


    @FXML
    private ListView<String> listView;

    private UserService userService;
    private MessageService messageService;
    private SessionService sessionService;


    @FXML
    public void initialize() {
            System.out.println("Initialize called");
            rootPane = (VBox) rootPane;
        // Utilisez Platform.runLater pour s'assurer que la scène est prête
        Platform.runLater(() -> {
            System.out.println("Inside runLater");
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.centerOnScreen();

        });
    }

    private void loadMessagesForSession(String sessionTitle) {
        String username = "a"; // Remplace par la méthode pour obtenir l'utilisateur connecté

        // Récupérer l'ID de la session à partir du titre
        int sessionId = MysqlConnection.getSessionIdByTitle(sessionTitle); // Méthode à créer pour récupérer l'ID

        if (sessionId == -1) {
            System.out.println("Aucune session trouvée pour le titre : " + sessionTitle);
            return;
        }

        // Récupérer les messages associés à cette session
        List<String> messages = MysqlConnection.getMessagesBySessionId(sessionId);

        // Effacer les anciens messages de l'interface
        messageContainer.getChildren().clear();

        // Ajouter chaque message à l'interface
        for (String message : messages) {
            addMessage(message, false); // Vous pouvez ajuster la logique pour afficher les messages de manière appropriée
        }

        // Faire défiler automatiquement vers le bas après le chargement
        Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
    }


    public void startNewSession(ActionEvent actionEvent) {
        String currentUsername = "ajachi";  // Exemple : nom d'utilisateur actuel
        String newSessionTitle = "New Session Title";  // Le titre de la nouvelle session

        // Obtenez l'ID de l'utilisateur actuel
        int userId = getUserIdByUsername(currentUsername);

        // Créer une nouvelle session dans la base de données
        int sessionId = MysqlConnection.createSession(userId, newSessionTitle);

        if (sessionId != -1) {
            System.out.println("Session créée avec l'ID: " + sessionId);
            //loadUserSessions();  Recharger les sessions après en avoir ajouté une nouvelle
        } else {
            System.out.println("Échec de la création de la session.");
        }
    }

    private int getUserIdByUsername(String username) {
        // Cette méthode est censée récupérer l'ID de l'utilisateur à partir de son nom d'utilisateur
        // Cela dépend de la structure de votre base de données, mais voici un exemple générique :
        String query = "SELECT id FROM users WHERE username = ?";
        try (Connection con = MysqlConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");  // Retourne l'ID de l'utilisateur
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Retourne -1 si l'utilisateur n'est pas trouvé
    }

    public void sendMessage() {
        String userMessage = messageInput.getText().trim();
        String botResponse = "";

        if (!userMessage.isEmpty()) {
            // Ajouter le message utilisateur à droite dans l'interface graphique
            addMessage(userMessage, true);

            String prompt = userMessage;
            try {
                // Encoder le prompt pour gérer les espaces et les caractères spéciaux
                String encodedPrompt = URLEncoder.encode(prompt, "UTF-8");

                // Ajouter le prompt encodé à l'URL comme paramètre de requête
                URL url = new URL("http://127.0.0.1:8765/api/?prompt=" + encodedPrompt);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Définir la méthode de la requête
                connection.setRequestMethod("GET");

                // Lire la réponse
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Fermer le lecteur
                reader.close();

                // Réponse du serveur
                botResponse = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Ajouter la réponse du bot à gauche dans l'interface graphique
            addMessage(botResponse, false);

            // Effacer le champ de saisie
            messageInput.clear();

            // Faire défiler vers le bas pour voir le dernier message
            messageScrollPane.setVvalue(1.0);

            // Enregistrer les messages dans la base de données MySQL (assurez-vous que l'utilisateur est connecté)
            String currentUsername = "ajachi";  // Exemple : remplacez ceci par votre gestionnaire de session ou variable

            // Récupérer l'ID de la session de l'utilisateur
            int sessionId = MysqlConnection.getSessionIdByTitle(currentUsername);


            // Ajouter le message de l'utilisateur à la base de données
            MysqlConnection.saveMessage(sessionId, 3, userMessage); // '1' est l'ID de l'utilisateur, ajustez selon vos besoins

            // Ajouter la réponse du chatbot à la base de données
            MysqlConnection.saveMessage(sessionId, 3, botResponse); // '2' est l'ID du chatbot, ajustez selon vos besoins
        }
    }

    private void addMessage(String message, boolean isUser) {
        // Créer un conteneur pour le message
        HBox messageBox = new HBox();
        messageBox.setSpacing(10);

        // Appliquer l'alignement
        if (isUser) {
            messageBox.setStyle("-fx-alignment: CENTER_RIGHT;");
        } else {
            messageBox.setStyle("-fx-alignment: CENTER_LEFT;");
        }

        // Créer une boîte colorée pour le message avec une largeur maximale et un retour à la ligne automatique
        TextFlow textFlow = new TextFlow();
        textFlow.setPadding(new javafx.geometry.Insets(10));  // Padding interne
        textFlow.setStyle("-fx-background-color: " + (isUser ? "#D1E8FF" : "#F1F0F0") + ";"
                + "-fx-background-radius: 10;");

        // Définir une largeur maximale pour le texte
        textFlow.setMaxWidth(450); // Limiter la largeur du message (par exemple 500px)

        // Créer le texte
        Text text = new Text(message);
        text.setFill(Color.BLACK);
        text.setWrappingWidth(450);  // Cette propriété définit la largeur de retour à la ligne

        // Activer le retour à la ligne automatique
        text.setWrappingWidth(400); // Si le message est trop long, il sera automatiquement mis en ligne

        // Ajouter le texte au TextFlow
        textFlow.getChildren().add(text);

        // Ajouter le TextFlow au HBox
        messageBox.getChildren().add(textFlow);

        // Ajouter le HBox au VBox
        messageContainer.getChildren().add(messageBox);
    }


    public void handleLogout(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
