package org.example.projet_java_rag_llm.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import org.bson.Document;
import org.example.projet_java_rag_llm.DBase.MongoDBConnection;

    public class test {

        @FXML
        private VBox rootPane;

        @FXML
        private VBox messageContainer;

        @FXML
        private TextField messageInput;

        @FXML
        private Button sendButton;

        @FXML
        private ScrollPane messageScrollPane;

        @FXML
        private Button logoutButton;

        @FXML
        private ListView<String> listView;

        private String connectedUsername;

        public void initialize() {
            System.out.println("Initialize called");
            rootPane = (VBox) rootPane;

            Platform.runLater(() -> {
                System.out.println("Inside runLater");
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.centerOnScreen();

                loadUserSessions(); // Charger les sessions de l'utilisateur

                // Ajout d'un écouteur pour la sélection d'une session
                listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // Afficher le titre de la session dans la console
                        System.out.println("Session sélectionnée: " + newValue);

                        // Charger les messages de la session sélectionnée
                        loadMessagesForSession(newValue);

                        // Optionnellement, afficher un message par défaut ou d'accueil ici si nécessaire
                        // addMessage("Bonjour", false); // Si tu veux un message d'accueil, laisse cette ligne.
                    }
                });
            });
        }

        private void loadMessagesForSession(String sessionTitle) {
            List<Document> userSessions = MongoDBConnection.getUserSessions(connectedUsername);
            String sessionId = null;

            // Trouver l'ID de la session sélectionnée
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

            System.out.println("Messages récupérés pour la session " + sessionTitle + ": " + messages);

            // Effacer les anciens messages de l'interface
            messageContainer.getChildren().clear();

            // Ajouter chaque message à l'interface
            for (Document messageDoc : messages) {
                String sender = messageDoc.getString("sender");
                String content = messageDoc.getString("content");

                // Afficher le message dans la console
                System.out.println("Message de " + sender + ": " + content);

                // Afficher le message dans l'interface à droite (pour l'utilisateur) ou à gauche (pour le chatbot)
                boolean isUser = sender.equals(connectedUsername);
                addMessage(content, !isUser); // Inversez les booléens pour afficher à droite pour l'utilisateur et à gauche pour le chatbot
            }

            // Faire défiler automatiquement vers le bas après le chargement
            Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
        }

        public void loadUserSessions() {
            connectedUsername = "update" ;

            boolean userExists = MongoDBConnection.userExists(connectedUsername);
            System.out.println("Utilisateur trouvé : " + (userExists ? "Oui" : "Non"));

            if (!userExists) {
                return;
            }

            List<Document> sessions = MongoDBConnection.getUserSessions(connectedUsername);

            if (sessions == null || sessions.isEmpty()) {
                System.out.println("Aucune session trouvée pour l'utilisateur " + connectedUsername);
                return;
            }

            List<String> sessionTitles = new ArrayList<>();

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

            System.out.println("Titres des sessions pour l'utilisateur " + connectedUsername + ":");
            for (String title : sessionTitles) {
                System.out.println("- " + title);
            }

            Platform.runLater(() -> {
                ObservableList<String> observableItems = FXCollections.observableArrayList(sessionTitles);
                listView.setItems(observableItems);
            });

            System.out.println("Sessions chargées : " + sessionTitles.size());
        }

        public void startNewSession(ActionEvent actionEvent) {
            String newSessionTitle = "New Session Title";
            MongoDBConnection.createNewSession(connectedUsername, newSessionTitle);
            loadUserSessions();
        }

        private void addMessage(String message, boolean isUser) {
            HBox messageBox = new HBox();
            messageBox.setSpacing(10);

            if (isUser) {
                messageBox.setStyle("-fx-alignment: CENTER_RIGHT;");
            } else {
                messageBox.setStyle("-fx-alignment: CENTER_LEFT;");
            }

            TextFlow textFlow = new TextFlow();
            textFlow.setPadding(new javafx.geometry.Insets(10));
            textFlow.setStyle("-fx-background-color: " + (isUser ? "#D1E8FF" : "#F1F0F0") + ";"
                    + "-fx-background-radius: 10;");
            textFlow.setMaxWidth(450);

            Text text = new Text(message);
            text.setFill(Color.BLACK);
            text.setWrappingWidth(400);

            textFlow.getChildren().add(text);

            messageBox.getChildren().add(textFlow);

            // Affichage du message dans la console pour vérifier
            System.out.println("Ajout du message: " + message);

            messageContainer.getChildren().add(messageBox);
        }    public void sendMessage() {
            String userMessage = messageInput.getText().trim();
            String botResponse = "";
            if (!userMessage.isEmpty()) {
                addMessage(userMessage, true);

                String prompt = userMessage;
                try {
                    String encodedPrompt = URLEncoder.encode(prompt, "UTF-8");

                    URL url = new URL("http://127.0.0.1:8765/api/?prompt=" + encodedPrompt);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    botResponse = response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                addMessage(botResponse, false);

                messageInput.clear();

                List<Document> userSessions = MongoDBConnection.getUserSessions(connectedUsername);

                String sessionId;

                if (userSessions.isEmpty()) {
                    sessionId = MongoDBConnection.createNewSession(connectedUsername);
                } else {
                    sessionId = userSessions.get(0).getString("sessionId");
                }

                MongoDBConnection.addMessageToSession(connectedUsername, sessionId, connectedUsername, userMessage);
            }
        }

    }
