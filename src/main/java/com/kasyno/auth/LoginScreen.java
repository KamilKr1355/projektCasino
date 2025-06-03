package com.kasyno.auth;

import com.kasyno.database.UserDAO;
import com.kasyno.menu.MainMenu;
import com.kasyno.player.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScreen extends Application {

    @Override
    public void start(Stage stage) {
        Label userLabel = new Label("Nazwa użytkownika:");
        TextField username = new TextField();
        Label passLabel = new Label("Hasło:");
        PasswordField password = new PasswordField();
        Button login = new Button("Zaloguj");
        Button register = new Button("Zarejestruj");

        login.setOnAction(e -> {
            Player player = UserDAO.login(username.getText(), password.getText());
            if (player != null) {
                MainMenu.setPlayer(player);
                new MainMenu().start(new Stage());
                stage.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "Niepoprawne dane logowania").showAndWait();
            }
        });

        register.setOnAction(e -> {
            new RegisterScreen().start(stage);
        });

        VBox layout = new VBox(10, userLabel, username, passLabel, password, login, register);
        layout.setStyle("-fx-padding: 20;");
        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Logowanie");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
