package com.kasyno.auth;

import com.kasyno.database.UserDAO;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterScreen extends Application {

    @Override
    public void start(Stage stage) {
        Label userLabel = new Label("Nazwa użytkownika:");
        TextField username = new TextField();
        Label passLabel = new Label("Hasło:");
        PasswordField password = new PasswordField();
        Button register = new Button("Zarejestruj");
        Button back = new Button("Wróć");

        register.setOnAction(e -> {
            if (UserDAO.register(username.getText(), password.getText())) {
                new Alert(Alert.AlertType.INFORMATION, "Rejestracja zakończona sukcesem!").showAndWait();
                new LoginScreen().start(stage);
            } else {
                new Alert(Alert.AlertType.ERROR, "Użytkownik już istnieje!").showAndWait();
            }
        });

        back.setOnAction(e -> {
            new LoginScreen().start(stage);
        });

        VBox layout = new VBox(10, userLabel, username, passLabel, password, register, back);
        layout.setStyle("-fx-padding: 20;");
        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Rejestracja");
        stage.show();
    }
}
