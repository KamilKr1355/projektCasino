package main.java.com.kasyno;

import javafx.application.Application;
import javafx.stage.Stage;

public class Casino extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Kasyno");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
