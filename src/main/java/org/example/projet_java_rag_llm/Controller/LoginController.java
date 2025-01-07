package org.example.projet_java_rag_llm.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.projet_java_rag_llm.DBase.MysqlConnection;

import java.io.IOException;

public class LoginController {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button goToSignUpButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    public void initialize() {
        // Applique l'image d'arrière-plan
        rootPane.setStyle("-fx-background-image: url('/images/img.jpg'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center;");
    }

    @FXML
    private void handleGoToSignUp() throws IOException {
        // Charger le fichier FXML pour SignUp
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projet_java_rag_llm/sign_up.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène et l'afficher
        Stage stage = (Stage) goToSignUpButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Créer un compte");
    }


    // Méthode pour afficher une alerte générique
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleLogin() throws IOException {
        // Vérifier si l'utilisateur existe avec les identifiants fournis
        if (MysqlConnection.checkUserCredentials(emailField.getText(), passwordField.getText())) {
            // Si les identifiants sont corrects, charger la page principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projet_java_rag_llm/hello-view.fxml"));
            Parent root = loader.load();

            // Charger le fichier CSS
            String css = getClass().getResource("/css/styles.css").toExternalForm();
            root.getStylesheets().add(css);

            // Créer une nouvelle scène et l'afficher
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Page d'accueil");
            stage.centerOnScreen();

            // Fixer les dimensions minimales
            stage.setMinWidth(1000);
            stage.setMinHeight(900);

        } else {
            // Si l'utilisateur n'existe pas, afficher une alerte d'erreur
            showAlert("Alerte", "Pas d'utilisateur avec ces données.", Alert.AlertType.ERROR);

            // Effacer les champs du formulaire
            clearFields();
        }
    }


    // Méthode pour effacer les champs du formulaire
    private void clearFields() {
        emailField.clear();
        passwordField.clear();
    }
}
