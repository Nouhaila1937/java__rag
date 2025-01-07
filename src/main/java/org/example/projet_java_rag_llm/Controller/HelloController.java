package org.example.projet_java_rag_llm.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.ArrayList;
import java.util.List;

import javafx.stage.Stage;
import javafx.scene.Scene;
import org.bson.Document;
import org.example.projet_java_rag_llm.DBase.MongoDBConnection;

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

    @FXML
    public void initialize() {
            System.out.println("Initialize called");
            rootPane = (VBox) rootPane;
        // Utilisez Platform.runLater pour s'assurer que la scène est prête
        Platform.runLater(() -> {
            System.out.println("Inside runLater");
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.centerOnScreen();

            // Autres opérations nécessitant la scène
            loadUserSessions();
            // Ajouter un écouteur à la ListView pour charger les messages d'une session
            listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadMessagesForSession(newValue); // Charge les messages de la session sélectionnée
                }
            });
        });
    }

    /*private void loadMessagesForSession(String sessionTitle) {
        String username = "ajachi"; // Remplacez par la méthode pour obtenir l'utilisateur connecté

        // Obtenir l'ID ou les détails de la session à partir du titre
        List<Document> userSessions = MongoDBConnection.getUserSessions(username);
        String sessionId = null;

        for (Document session : userSessions) {
            List<Document> sessionsArray = (List<Document>) session.get("sessions", List.class);
            if (sessionsArray != null) {
                for (Document sessionDoc : sessionsArray) {
                    if (sessionDoc.getString("title").equals(sessionTitle)) {
                        sessionId = sessionDoc.getString("sessionId");
                        break;
                    }
                }
            }
            if (sessionId != null) break;
        }

        if (sessionId == null) {
            System.out.println("Aucune session trouvée pour le titre : " + sessionTitle);
            return;
        }

        // Récupérer les messages associés à cette session
        List<Document> messages = MongoDBConnection.getSessionMessages(sessionId);

        // Effacer les anciens messages de l'interface
        messageContainer.getChildren().clear();

        // Ajouter chaque message à l'interface
        for (Document messageDoc : messages) {
            String sender = messageDoc.getString("sender");
            String content = messageDoc.getString("content");

            // Affichez le message dans l'interface
            boolean isUser = sender.equals(username);
            addMessage(content, isUser);
        }

        // Faire défiler automatiquement vers le bas après le chargement
        Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
    }*/

    private void loadMessagesForSession(String sessionTitle) {
        String username = "ajachi"; // Remplacez par la méthode pour obtenir l'utilisateur connecté

        // Obtenir l'ID ou les détails de la session à partir du titre
        List<Document> userSessions = MongoDBConnection.getUserSessions(username);
        String sessionId = null;

        for (Document session : userSessions) {
            List<Document> sessionsArray = (List<Document>) session.get("sessions", List.class);
            if (sessionsArray != null && !sessionsArray.isEmpty()) {
                for (Document sessionDoc : sessionsArray) {
                    if (sessionDoc.getString("title").equals(sessionTitle)) {
                        sessionId = sessionDoc.getString("sessionId");
                        break;
                    }
                }
            }
            if (sessionId != null) break;
        }

        if (sessionId == null) {
            System.out.println("Aucune session trouvée pour le titre : " + sessionTitle);
            return;
        }

        // Récupérer les messages associés à cette session
        List<Document> messages = MongoDBConnection.getSessionMessages(sessionId);

        // Effacer les anciens messages de l'interface
        messageContainer.getChildren().clear();

        // Ajouter chaque message à l'interface
        for (Document messageDoc : messages) {
            String sender = messageDoc.getString("sender");
            String content = messageDoc.getString("content");

            // Afficher le message dans l'interface
            boolean isUser = sender.equals(username);
            addMessage(content, isUser);
        }

        // Faire défiler automatiquement vers le bas après le chargement
        Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
    }

    public void loadUserSessions() {
        String username = "ajachi";

        // Vérifier si l'utilisateur existe
        boolean userExists = MongoDBConnection.userExists(username);
        System.out.println("Utilisateur trouvé : " + (userExists ? "Oui" : "Non"));

        if (!userExists) {
            return;
        }

        // Récupérer les sessions de l'utilisateur
        List<Document> sessions = MongoDBConnection.getUserSessions(username);

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("Aucune session trouvée pour l'utilisateur " + username);
            return;
        }

        List<String> sessionTitles = new ArrayList<>();

        // Parcourir les sessions et ajouter le titre à la liste
        for (Document session : sessions) {
            List<Document> sessionsArray = (List<Document>) session.get("sessions", List.class);
            if (sessionsArray != null && !sessionsArray.isEmpty()) {
                Document firstSession = sessionsArray.get(0);
                String title = firstSession.getString("title");
                if (title != null && !title.isEmpty()) {
                    sessionTitles.add(title);
                }
            } else {
                System.out.println("Pas de sessions trouvées pour cet utilisateur");
            }
        }

        System.out.println("Titres des sessions pour l'utilisateur " + username + ":");
        for (String title : sessionTitles) {
            System.out.println("- " + title);
        }

        // Mettre à jour la ListView
        Platform.runLater(() -> {
            ObservableList<String> observableItems = FXCollections.observableArrayList(sessionTitles);
            listView.setItems(observableItems);
        });

        System.out.println("Sessions chargées : " + sessionTitles.size());
    }

    public void startNewSession(ActionEvent actionEvent) {
        String currentUsername = "ajachi";  // Exemple : nom d'utilisateur actuel
        String newSessionTitle = "New Session Title";  // Le titre de la nouvelle session
        // Créer une nouvelle session dans la base de données
        MongoDBConnection.createNewSession(currentUsername, newSessionTitle);
        loadUserSessions();  // Recharger les sessions après en avoir ajouté une nouvelle
    }


    /*public void sendMessage() {
        String userMessage = messageInput.getText().trim();
        String botResponse = "";
        if (!userMessage.isEmpty()) {
            // Ajouter le message utilisateur à droite
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

            // Ajouter la réponse du bot
            addMessage(botResponse, false);

            // Effacer le champ de saisie
            messageInput.clear();

            // Faire défiler vers le bas pour voir le dernier message
            messageScrollPane.setVvalue(1.0);  // Faire défiler vers le bas du ScrollPane
        }
    }*/

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
            messageScrollPane.setVvalue(1.0);  // Faire défiler vers le bas du ScrollPane

            // Enregistrer les messages dans la base de données (assurez-vous que l'utilisateur est connecté)
            String currentUsername = "ajachi";  // Exemple : remplacez ceci par votre gestionnaire de session ou variable

            // Vérifier si une session existe pour l'utilisateur
            List<Document> userSessions = MongoDBConnection.getUserSessions(currentUsername);

            String sessionId;

            if (userSessions.isEmpty()) {
                // Si aucune session n'existe, créer une nouvelle session
                sessionId = MongoDBConnection.createNewSession(currentUsername);
            } else {
                // Si une session existe déjà, récupérez l'ID de session
                sessionId = userSessions.get(0).getString("sessionId");
            }

            // Ajouter le message de l'utilisateur
            MongoDBConnection.addMessageToSession(currentUsername, sessionId, currentUsername, userMessage);

            // Ajouter la réponse du chatbot
            MongoDBConnection.addMessageToSession(currentUsername, sessionId, "Chatbot", botResponse);
        }
    }

    public void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);

            Stage stage = (Stage)((Node)root.getScene().getRoot()).getScene().getWindow();
            stage.close();

            Stage primaryStage = new Stage();
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
