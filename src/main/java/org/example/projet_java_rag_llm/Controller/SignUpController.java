package org.example.projet_java_rag_llm.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.example.projet_java_rag_llm.DBase.MongoDBConnection;
import org.example.projet_java_rag_llm.Dao.MysqlConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button signUpButton;

    @FXML
    private Button goToLoginButton;

    @FXML
    public void initialize() {
        signUpButton.setOnAction(event -> handleSignUp());
    }

    @FXML
    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Vérifier que tous les champs sont remplis
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur d'inscription", "Tous les champs doivent être remplis.", AlertType.ERROR);
            return;
        }

        // Vérifier si les mots de passe correspondent
        if (!password.equals(confirmPassword)) {
            showAlert("Erreur d'inscription", "Les mots de passe ne correspondent pas.", AlertType.ERROR);
            return;
        }

        // Vérifier le format de l'email
        if (!isValidEmail(email)) {
            showAlert("Erreur d'inscription", "L'email n'est pas valide.", AlertType.ERROR);
            return;
        }

        // Vérifier si l'email existe déjà dans la base de données
        if (MongoDBConnection.isEmailInUse(email)) {
            showAlert("Erreur d'inscription", "Cet email est déjà utilisé.", AlertType.ERROR);
            return;
        }

        // Ajouter l'utilisateur à la base de données
        MysqlConnection.addUser(username, email, password);

        // Afficher une alerte de succès
        showAlert("Inscription réussie", "Bienvenue " + username + ". Vous êtes maintenant inscrit.", AlertType.INFORMATION);

        // Effacer les champs du formulaire
        clearFields();
    }

    // Méthode pour afficher une alerte générique
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour effacer les champs du formulaire
    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    public void handleGoToLogin()  throws IOException {
        // Charger le fichier FXML pour SignUp
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projet_java_rag_llm/login.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène et l'afficher
        Stage stage = (Stage) goToLoginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Créer un compte");
    }
    // Méthode pour vérifier le format de l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Vérifier si l'email existe déjà dans la base de données (utilise la méthode checkUserCredentials de MysqlConnection)
    private boolean isEmailInUse(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection con = MysqlConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, email);
            try (ResultSet rst = pst.executeQuery()) {
                return rst.next();  // Si un résultat est trouvé, l'email existe déjà
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
