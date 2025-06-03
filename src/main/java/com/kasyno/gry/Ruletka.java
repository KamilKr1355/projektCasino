package com.kasyno.gry;

import java.util.*;

import com.kasyno.database.UserDAO;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Ruletka extends Application {

    private static final String[] NUMBERS = {
            "0", "32", "15", "19", "4", "21", "2", "25", "17", "34", "6", "27", "13",
            "36", "11", "30", "8", "23", "10", "5", "24", "16", "33", "1", "20", "14",
            "31", "9", "22", "18", "29", "7", "28", "12", "35", "3", "26"
    };

    private static final String[] RED_NUMBERS = {
            "1", "3", "5", "7", "9", "12", "14", "16", "18",
            "19", "21", "23", "25", "27", "30", "32", "34", "36"
    };

    private static String returnColor(String number) {
        for (String red : RED_NUMBERS) {
            if (red.equals(number)) return "r";
        }
        if (number.equals("0")) return "g";
        return "b";
    }

    private double currentAngle = 0;
    private int countdown = 15;
    private Timeline countdownTimer;
    private com.kasyno.player.Player player;
    public Ruletka(com.kasyno.player.Player player) {
        this.player = player;
    }

    private String betColor = null;
    private List<String> betNumbers = new ArrayList<>();
    private Map<String, Button> numberButtons = new HashMap<>();
    private Map<String, Button> colorButtons = new HashMap<>();

    private TextField betAmountFieldColor;
    private TextField betAmountFieldNumber;
    private double betAmountColor = 0;
    private double betAmountNumber = 0;

    private void spinWheel(ImageView wheel, Text resultText, Label countdownLabel, Text bilans, Button betButton) {

        if (betColor == null && betNumbers.isEmpty()) {
            resultText.setText("Wybierz kolor lub numer do obstawienia!");
            return;
        }

        double totalBet = betAmountColor + (betAmountNumber * betNumbers.size());

        betButton.setDisable(true);
        betAmountFieldColor.setDisable(true);
        betAmountFieldNumber.setDisable(true);
        numberButtons.values().forEach(b -> b.setDisable(true));
        colorButtons.values().forEach(b -> b.setDisable(true));
        countdownTimer.stop();

        Random rand = new Random();
        int selectedIndex = rand.nextInt(NUMBERS.length);
        String landedNumber = NUMBERS[selectedIndex];
        String landedColor = returnColor(landedNumber);

        double degreesPerField = 360.0 / NUMBERS.length;
        double targetAngle = selectedIndex * degreesPerField;
        double desiredAngle = 180 - targetAngle;
        double relativeAngle = (360 - (currentAngle % 360) + desiredAngle) % 360;
        double fullSpin = 5 * 360;
        double angleToRotate = fullSpin + relativeAngle;
        currentAngle += angleToRotate;

        RotateTransition rt = new RotateTransition(Duration.seconds(4), wheel);
        rt.setInterpolator(Interpolator.EASE_OUT);
        rt.setByAngle(angleToRotate);
        rt.setCycleCount(1);

        rt.setOnFinished(event -> {
            resultText.setText("Wylosowano: " + landedNumber + " (" + colorName(landedColor) + ")");
            double winnings = 0;

            if (betNumbers.contains(landedNumber)) {
                winnings += betAmountNumber * 35;
            }

            if (betColor != null && betColor.equals(landedColor)) {
                winnings += betAmountColor * 2;
            }

            player.setBalance(player.getBalance() + winnings);
            resultText.setText(resultText.getText() + (winnings > 0 ? " - WYGRANA! +" + winnings : " - PRZEGRANA :("));
            bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));

            betColor = null;
            betNumbers.clear();
            betAmountColor = 0;
            betAmountNumber = 0;
            betAmountFieldColor.clear();
            betAmountFieldNumber.clear();
            resetButtonsStyle();

            betButton.setDisable(false);
            betAmountFieldColor.setDisable(false);
            betAmountFieldNumber.setDisable(false);
            numberButtons.values().forEach(b -> b.setDisable(false));
            colorButtons.values().forEach(b -> b.setDisable(false));

            countdown = 15;
            countdownLabel.setText("" + countdown);
            countdownTimer.play();
        });

        rt.play();
    }

    private String colorName(String c) {
        switch (c) {
            case "r": return "Czerwony";
            case "b": return "Czarny";
            case "g": return "Zielony";
            default: return "";
        }
    }

    private void resetButtonsStyle() {
        // reset przycisków numerów do ich oryginalnych kolorów
        numberButtons.forEach((num, btn) -> {
            String color = returnColor(num);
            switch (color) {
                case "r":
                    btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    break;
                case "b":
                    btn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                    break;
                case "g":
                    btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    break;
            }
        });

        // reset kolorów przycisków kolorów (z zachowaniem stałego tła)
        colorButtons.forEach((col, btn) -> {
            switch (col) {
                case "r":
                    btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    break;
                case "b":
                    btn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                    break;
                case "g":
                    btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    break;
            }
        });
    }


    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        Label countdownLabel = new Label("" + countdown);
        countdownLabel.setLayoutX(725);
        countdownLabel.setLayoutY(20);
        countdownLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ImageView wheel = new ImageView(new Image("ruletka.png"));
        wheel.setFitWidth(400);
        wheel.setFitHeight(400);
        wheel.setX(536);
        wheel.setY(50);

        ImageView arrowUp = new ImageView(new Image("arrowUp.png"));
        arrowUp.setFitWidth(50);
        arrowUp.setFitHeight(50);
        arrowUp.setX(711);
        arrowUp.setY(450);

        Button spinButton = new Button("Zakręć");
        spinButton.setLayoutX(270);
        spinButton.setLayoutY(500);

        Text resultText = new Text("");
        resultText.setLayoutX(250);
        resultText.setLayoutY(470);
        resultText.setStyle("-fx-font-size: 20px;");

        Text bilans = new Text("Bilans: " + player.getBalance());
        bilans.setLayoutX(250);
        bilans.setLayoutY(570);
        bilans.setStyle("-fx-font-size: 20px;");

        betAmountFieldColor = new TextField();
        betAmountFieldColor.setPromptText("Stawka kolor");
        betAmountFieldColor.setLayoutX(50);
        betAmountFieldColor.setLayoutY(30);
        betAmountFieldColor.setPrefWidth(100);

        betAmountFieldNumber = new TextField();
        betAmountFieldNumber.setPromptText("Stawka liczba");
        betAmountFieldNumber.setLayoutX(170);
        betAmountFieldNumber.setLayoutY(30);
        betAmountFieldNumber.setPrefWidth(100);

        String[] colors = {"r", "b", "g"};
        String[] colorNames = {"Czerwony", "Czarny", "Zielony"};
        for (int i = 0; i < colors.length; i++) {
            Button colorBtn = new Button(colorNames[i]);

            switch (colors[i]) {
                case "r":
                    colorBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    break;
                case "b":
                    colorBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                    break;
                case "g":
                    colorBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    break;
            }

            colorBtn.setLayoutX(50 + i * 110);
            colorBtn.setLayoutY(70);
            int idx = i;
            colorBtn.setOnAction(e -> {
                if (betColor != null && betColor.equals(colors[idx])) {
                    betColor = null;
                    player.setBalance(player.getBalance() + betAmountColor);
                    bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                    betAmountColor = 0;

                    colorButtons.forEach((colKey, btn) -> {
                        switch (colKey) {
                            case "r":
                                btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                                break;
                            case "b":
                                btn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                                break;
                            case "g":
                                btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                                break;
                        }
                    });
                } else {
                    try {
                        double newBetAmount = Double.parseDouble(betAmountFieldColor.getText());
                        if (newBetAmount <= 0) {
                            resultText.setText("Stawka musi być większa niż 0!");
                            return;
                        }

                        if (betColor != null) {
                            player.setBalance(player.getBalance() + betAmountColor);
                        }

                        if (player.getBalance() >= newBetAmount) {
                            player.setBalance(player.getBalance() - newBetAmount);
                            bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                            betColor = colors[idx];
                            betAmountColor = newBetAmount;

                            colorButtons.forEach((colKey, btn) -> {
                                if (colKey.equals(colors[idx])) {
                                    btn.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                                } else {
                                    switch (colKey) {
                                        case "r":
                                            btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                                            break;
                                        case "b":
                                            btn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                                            break;
                                        case "g":
                                            btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                                            break;
                                    }
                                }
                            });

                            resultText.setText("");
                        } else {
                            resultText.setText("Za mało środków na kolor!");
                        }
                    } catch (NumberFormatException ex) {
                        resultText.setText("Podaj poprawną stawkę kolor!");
                    }
                }
            });


            colorButtons.put(colors[i], colorBtn);
            root.getChildren().add(colorBtn);
        }

        int startX = 50;
        int startY = 120;
        int btnWidth = 40;
        int btnHeight = 30;
        int cols = 10;
        for (int i = 0; i < NUMBERS.length; i++) {
            String num = NUMBERS[i];
            Button numBtn = new Button(num);
            int col = i % cols;
            int row = i / cols;
            numBtn.setLayoutX(startX + col * (btnWidth + 5));
            numBtn.setLayoutY(startY + row * (btnHeight + 5));
            numBtn.setPrefSize(btnWidth, btnHeight);

            String color = returnColor(num);
            switch (color) {
                case "r":
                    numBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    break;
                case "b":
                    numBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                    break;
                case "g":
                    numBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    break;
            }

            numBtn.setOnAction(e -> {
                if (betNumbers.contains(num)) {
                    betNumbers.remove(num);
                    player.setBalance(player.getBalance() + betAmountNumber);
                    bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                    String originalColor = returnColor(num);
                    switch (originalColor) {
                        case "r":
                            numBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                            break;
                        case "b":
                            numBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                            break;
                        case "g":
                            numBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                            break;
                    }

                } else {
                    try {
                        betAmountNumber = Double.parseDouble(betAmountFieldNumber.getText());
                        if (player.getBalance() >= betAmountNumber) {
                            player.setBalance(player.getBalance() - betAmountNumber);
                            bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                            betNumbers.add(num);
                            numBtn.setStyle("-fx-background-color: yellow");
                        } else {
                            resultText.setText("Za mało środków na liczbę!");
                        }
                    } catch (NumberFormatException ex) {
                        resultText.setText("Podaj poprawną stawkę liczba!");
                    }
                }
            });
            numberButtons.put(num, numBtn);
            root.getChildren().add(numBtn);
        }

        Button betButton = new Button("Postaw i zakręć");
        betButton.setLayoutX(50);
        betButton.setLayoutY(450);
        betButton.setOnAction(e -> spinWheel(wheel, resultText, countdownLabel, bilans, betButton));

        root.getChildren().addAll(wheel, arrowUp, spinButton, resultText, countdownLabel, bilans,
                betAmountFieldColor, betAmountFieldNumber, betButton);

        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            countdown--;
            if (countdown <= 0) {
                countdown = 15;
                spinWheel(wheel, resultText, countdownLabel, bilans, betButton);
            }
            countdownLabel.setText("" + countdown);
        }));
        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();

        Scene scene = new Scene(root, 1472, 832);
        stage.setTitle("Ruletka - Kasyno");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
