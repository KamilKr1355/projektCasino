package com.kasyno.gry;

import com.kasyno.player.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BlackJackApp extends Application {

    private Player player;

    public BlackJackApp(Player player) {
        this.player = player;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/black_jack_view.fxml"));
        Parent root = loader.load();

        BlackJackController controller = loader.getController();
        controller.setPlayer(player);  // 👈 przekazanie gracza
        controller.initGame();

        primaryStage.setTitle("BlackJack – JavaFX");
        primaryStage.setScene(new Scene(root, 1536, 1000));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
