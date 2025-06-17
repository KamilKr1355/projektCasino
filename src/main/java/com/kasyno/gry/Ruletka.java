package com.kasyno.gry;

import java.util.*;

import com.kasyno.database.UserDAO;
import com.kasyno.menu.MainMenu;
import com.kasyno.player.Player;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
    private Player player;
    public Ruletka(Player player) {
        this.player = player;
    }

    private String betColor = null;
    private List<String> betNumbers = new ArrayList<>();
    private List<String> betColumns = new ArrayList<>();
    private List<String> betDozens = new ArrayList<>();
    private List<String> betHalves = new ArrayList<>();
    private Map<String, Button> numberButtons = new HashMap<>();
    private Map<String, Button> colorButtons = new HashMap<>();
    private Map<String, Button> columnButtons = new HashMap<>();
    private Map<String, Button> dozenButtons = new HashMap<>();
    private Map<String, Button> halfButtons = new HashMap<>();

    private TextField betAmountField; // Jedno pole do wprowadzania stawek
    private Map<String, Double> betAmounts = new HashMap<>(); // Mapa przechowująca stawki dla każdego typu zakładu

    private void spinWheel(ImageView wheel, Text resultText, Label countdownLabel, Text bilans, Button betButton) {
        if (betColor == null && betNumbers.isEmpty() && betColumns.isEmpty() && betDozens.isEmpty() && betHalves.isEmpty()) {
            resultText.setText("Wybierz coś do obstawienia!");
            return;
        }

        double totalBet = (betColor != null ? betAmounts.getOrDefault("color", 0.0) : 0.0) +
                betNumbers.stream().mapToDouble(n -> betAmounts.getOrDefault("number", 0.0)).sum() +
                betColumns.stream().mapToDouble(c -> betAmounts.getOrDefault("column", 0.0)).sum() +
                betDozens.stream().mapToDouble(d -> betAmounts.getOrDefault("dozen", 0.0)).sum() +
                betHalves.stream().mapToDouble(h -> betAmounts.getOrDefault("half", 0.0)).sum();

        betButton.setDisable(true);
        betAmountField.setDisable(true);
        numberButtons.values().forEach(b -> b.setDisable(true));
        colorButtons.values().forEach(b -> b.setDisable(true));
        columnButtons.values().forEach(b -> b.setDisable(true));
        dozenButtons.values().forEach(b -> b.setDisable(true));
        halfButtons.values().forEach(b -> b.setDisable(true));
        countdownTimer.stop();

        Random rand = new Random();
        int selectedIndex = rand.nextInt(NUMBERS.length);
        String landedNumber = NUMBERS[selectedIndex];
        String landedColor = returnColor(landedNumber);

        int column;
        int dozen;
        int half;

        if (!landedNumber.equals("0")) {
            int num = Integer.parseInt(landedNumber);

            // Poprawione określanie kolumny (1-3)
            if (num%3==0)
            {
                column=1;
            }
            else if (num%3==2)
            {
                column=2;
            }
            else if (num%3==1)
            {
                column=3;
            } else {
                column = 0;
            }
            //column = (num - 2) % 3 + 1;

            // Określ tuzin (1-3)
            if (num >= 1 && num <= 12) {
                dozen = 1;
            } else if (num >= 13 && num <= 24) {
                dozen = 2;
            } else if (num >= 25 && num <= 36) {
                dozen = 3;
            } else {
                dozen = 0;
            }

            // Określ połowę (1-2)
            half = (num <= 18) ? 1 : 2;
        } else {
            half = 0;
            dozen = 0;
            column = 0;
        }


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

            // Wygrana dla numerów
            if (betNumbers.contains(landedNumber)) {
                winnings += betAmounts.getOrDefault("number", 0.0) * 35;
            }

            // Wygrana dla kolorów
            if (betColor != null && betColor.equals(landedColor)) {
                winnings += betAmounts.getOrDefault("color", 0.0) * 2;
            }

            // Wygrana dla kolumn
            if (column > 0 && betColumns.contains(String.valueOf(column))) {
                winnings += betAmounts.getOrDefault("column", 0.0) * 2;
            }

            // Wygrana dla tuzinów
            if (dozen > 0 && betDozens.contains(String.valueOf(dozen))) {
                winnings += betAmounts.getOrDefault("dozen", 0.0) * 2;
            }

            // Wygrana dla połówek
            if (half > 0 && betHalves.contains(String.valueOf(half))) {
                winnings += betAmounts.getOrDefault("half", 0.0) * 1;
            }

            player.setBalance(player.getBalance() + winnings);
            resultText.setText(resultText.getText() + (winnings > 0 ? " - WYGRANA! +" + winnings : " - PRZEGRANA :("));
            bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));

            // Reset wszystkich zakładów
            betColor = null;
            betNumbers.clear();
            betColumns.clear();
            betDozens.clear();
            betHalves.clear();
            betAmounts.clear();
            betAmountField.clear();
            resetButtonsStyle();

            // Odblokowanie przycisków
            betButton.setDisable(false);
            betAmountField.setDisable(false);
            numberButtons.values().forEach(b -> b.setDisable(false));
            colorButtons.values().forEach(b -> b.setDisable(false));
            columnButtons.values().forEach(b -> b.setDisable(false));
            dozenButtons.values().forEach(b -> b.setDisable(false));
            halfButtons.values().forEach(b -> b.setDisable(false));

            countdown = 15;
            countdownLabel.setTextFill(Color.web("#d4af37"));
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
        // reset przycisków numerów
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

        // reset przycisków kolorów
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

        // reset przycisków kolumn
        columnButtons.forEach((col, btn) -> {
            btn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");
        });

        // reset przycisków tuzinów
        dozenButtons.forEach((doz, btn) -> {
            btn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");
        });

        // reset przycisków połówek
        halfButtons.forEach((half, btn) -> {
            btn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");
        });
    }

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        Label countdownLabel = new Label("" + countdown);
        countdownLabel.setLayoutX(328);
        countdownLabel.setLayoutY(22);
        countdownLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        countdownLabel.setTextFill(Color.web("#d4af37"));

        ImageView wheel = new ImageView(new Image("ruletka.png"));
        wheel.setFitWidth(400);
        wheel.setFitHeight(400);
        wheel.setX(136);
        wheel.setY(50);

        ImageView arrowUp = new ImageView(new Image("arrowUp.png"));
        arrowUp.setFitWidth(50);
        arrowUp.setFitHeight(50);
        arrowUp.setX(311);
        arrowUp.setY(450);


        Text resultText = new Text("");
        resultText.setFill(Color.WHITE);
        resultText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        resultText.setLayoutX(220);
        resultText.setLayoutY(700);
        Text bilans = new Text("Bilans: " + player.getBalance());
        bilans.setLayoutX(1300);
        bilans.setLayoutY(100);
        bilans.setFill(Color.web("#d4af37"));
        bilans.setStyle(
                        "-fx-text-fill: #d4af37;" +
                        "-fx-font-family: 'Arial';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;"
        );

        // Jedno pole do wprowadzania stawek
        betAmountField = new TextField();
        betAmountField.setPromptText("Wprowadź stawkę");
        betAmountField.setLayoutX(803);
        betAmountField.setLayoutY(120);
        betAmountField.setPrefWidth(150);

        // Przyciski kolorów
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

            colorBtn.setLayoutX(803 + i * 110);
            colorBtn.setLayoutY(160);
            int idx = i;
            colorBtn.setOnAction(e -> {
                try {
                    double betAmount = Double.parseDouble(betAmountField.getText());
                    if (betAmount <= 0) {
                        resultText.setText("Stawka musi być większa niż 0!");
                        return;
                    }

                    if (betColor != null && betColor.equals(colors[idx])) {
                        // Anuluj zakład
                        player.setBalance(player.getBalance() + betAmounts.getOrDefault("color", 0.0));
                        bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                        betColor = null;
                        betAmounts.remove("color");
                        colorBtn.setStyle("-fx-background-color: " + (colors[idx].equals("r") ? "red" : colors[idx].equals("b") ? "black" : "green") + "; -fx-text-fill: white;");
                    } else {
                        if (player.getBalance() >= betAmount) {
                            // Anuluj poprzedni zakład na kolor jeśli istnieje
                            if (betColor != null) {
                                player.setBalance(player.getBalance() + betAmounts.getOrDefault("color", 0.0));
                                colorButtons.get(betColor).setStyle("-fx-background-color: " + (betColor.equals("r") ? "red" : betColor.equals("b") ? "black" : "green") + "; -fx-text-fill: white;");
                            }

                            // Postaw nowy zakład
                            player.setBalance(player.getBalance() - betAmount);
                            bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                            betColor = colors[idx];
                            betAmounts.put("color", betAmount);
                            colorBtn.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                            resultText.setText("");
                        } else {
                            resultText.setText("Za mało środków na zakład!");
                        }
                    }
                } catch (NumberFormatException ex) {
                    resultText.setText("Podaj poprawną stawkę!");
                }
            });

            colorButtons.put(colors[i], colorBtn);
            root.getChildren().add(colorBtn);
        }

        // Przyciski numerów w układzie ruletki
        int startX = 803;
        int startY = 210;
        int btnWidth = 40;
        int btnHeight = 30;

        // Kolumna 0 (specjalna)
        Button zeroBtn = new Button("0");
        zeroBtn.setLayoutX(startX);
        zeroBtn.setLayoutY(startY);
        zeroBtn.setPrefSize(btnWidth, btnHeight * 3 + 10);
        zeroBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        zeroBtn.setOnAction(e -> handleNumberBet(zeroBtn, "0", resultText, bilans));
        numberButtons.put("0", zeroBtn);
        root.getChildren().add(zeroBtn);

        // Rzędzy 1-3 (12x3 przycisków)
        String[][] rowNumbers = {
                {
                    "3","2","1"
                },
                {
                        "6","5","4"
                },
                {
                        "9","8","7"
                },
                {
                        "12","11","10"
                },
                {
                        "15","14","13"
                },
                {
                        "18","17","16"
                },
                {
                        "21","20","19"
                },
                {
                        "24","23","22"
                },
                {
                        "27","26","25"
                },
                {
                        "30","29","28"
                },
                {
                        "33","32","31"
                },
                {
                        "36","35","34"
                },
        };

        for (int col = 0; col < 12; col++) {
            for (int row = 0; row < 3; row++) {
                String num = rowNumbers[col][row];
                Button numBtn = new Button(num);
                numBtn.setLayoutX(startX + (col + 1) * (btnWidth + 5));
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
                }

                numBtn.setOnAction(e -> handleNumberBet(numBtn, num, resultText, bilans));
                numberButtons.put(num, numBtn);
                root.getChildren().add(numBtn);
            }
        }

        // Przyciski kolumn (1-3)
        for (int i = 1; i <= 3; i++) {
            Button rowBtn = new Button("2:1");
            rowBtn.setLayoutX(startX + 40*15-15);
            rowBtn.setLayoutY(startY + 35*(i-1));
            rowBtn.setPrefSize(40, btnHeight);
            rowBtn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");

            int column = i;
            rowBtn.setOnAction(e -> {
                try {
                    double betAmount = Double.parseDouble(betAmountField.getText());
                    if (betAmount <= 0) {
                        resultText.setText("Stawka musi być większa niż 0!");
                        return;
                    }

                    if (betColumns.contains(String.valueOf(column))) {
                        // Anuluj zakład
                        player.setBalance(player.getBalance() + betAmounts.getOrDefault("column", 0.0));
                        bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                        betColumns.remove(String.valueOf(column));
                        betAmounts.remove("column");
                        rowBtn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");
                    } else {
                        if (player.getBalance() >= betAmount) {
                            player.setBalance(player.getBalance() - betAmount);
                            bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                            betColumns.add(String.valueOf(column));
                            betAmounts.put("column", betAmount);
                            rowBtn.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                            resultText.setText("");
                        } else {
                            resultText.setText("Za mało środków na zakład!");
                        }
                    }
                } catch (NumberFormatException ex) {
                    resultText.setText("Podaj poprawną stawkę!");
                }
            });

            columnButtons.put(String.valueOf(i), rowBtn);
            root.getChildren().add(rowBtn);
        }

        // Przyciski tuzinów (1-3)
        for (int i = 1; i <= 3; i++) {
            Button dozenBtn = new Button(i + ". tuzin");
            if (i==1) {dozenBtn = new Button("1-12");}
            else if(i==2) {dozenBtn = new Button("12-23");}
            else {dozenBtn = new Button("24-36");}

            dozenBtn.setLayoutX(startX + (i-1) * 120);
            dozenBtn.setLayoutY(startY + 4 * (btnHeight ));
            dozenBtn.setPrefSize(110, btnHeight);
            dozenBtn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");

            int dozen = i;
            Button finalDozenBtn = dozenBtn;
            dozenBtn.setOnAction(e -> {
                try {
                    double betAmount = Double.parseDouble(betAmountField.getText());
                    if (betAmount <= 0) {
                        resultText.setText("Stawka musi być większa niż 0!");
                        return;
                    }

                    if (betDozens.contains(String.valueOf(dozen))) {
                        // Anuluj zakład
                        player.setBalance(player.getBalance() + betAmounts.getOrDefault("dozen", 0.0));
                        bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                        betDozens.remove(String.valueOf(dozen));
                        betAmounts.remove("dozen");
                        finalDozenBtn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");
                    } else {
                        if (player.getBalance() >= betAmount) {
                            player.setBalance(player.getBalance() - betAmount);
                            bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                            betDozens.add(String.valueOf(dozen));
                            betAmounts.put("dozen", betAmount);
                            finalDozenBtn.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                            resultText.setText("");
                        } else {
                            resultText.setText("Za mało środków na zakład!");
                        }
                    }
                } catch (NumberFormatException ex) {
                    resultText.setText("Podaj poprawną stawkę!");
                }
            });

            dozenButtons.put(String.valueOf(i), dozenBtn);
            root.getChildren().add(dozenBtn);
        }

        // Przyciski połówek (1-2)
        Button firstHalfBtn = new Button("1-18");
        firstHalfBtn.setLayoutX(startX);
        firstHalfBtn.setLayoutY(startY + 6 * (btnHeight ));
        firstHalfBtn.setPrefSize(110, btnHeight);
        firstHalfBtn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");
        firstHalfBtn.setOnAction(e -> {
            try {
                double betAmount = Double.parseDouble(betAmountField.getText());
                if (betAmount <= 0) {
                    resultText.setText("Stawka musi być większa niż 0!");
                    return;
                }

                if (betHalves.contains("1")) {
                    // Anuluj zakład
                    player.setBalance(player.getBalance() + betAmounts.getOrDefault("half", 0.0));
                    bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                    betHalves.remove("1");
                    betAmounts.remove("half");
                    firstHalfBtn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");
                } else {
                    if (player.getBalance() >= betAmount) {
                        player.setBalance(player.getBalance() - betAmount);
                        bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                        betHalves.add("1");
                        betAmounts.put("half", betAmount);
                        firstHalfBtn.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                        resultText.setText("");
                    } else {
                        resultText.setText("Za mało środków na zakład!");
                    }
                }
            } catch (NumberFormatException ex) {
                resultText.setText("Podaj poprawną stawkę!");
            }
        });

        Button secondHalfBtn = new Button("19-36");
        secondHalfBtn.setLayoutX(startX + 120);
        secondHalfBtn.setLayoutY(startY + 6* (btnHeight ));
        secondHalfBtn.setPrefSize(110, btnHeight);
        secondHalfBtn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");
        secondHalfBtn.setOnAction(e -> {
            try {
                double betAmount = Double.parseDouble(betAmountField.getText());
                if (betAmount <= 0) {
                    resultText.setText("Stawka musi być większa niż 0!");
                    return;
                }

                if (betHalves.contains("2")) {
                    // Anuluj zakład
                    player.setBalance(player.getBalance() + betAmounts.getOrDefault("half", 0.0));
                    bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                    betHalves.remove("2");
                    betAmounts.remove("half");
                    secondHalfBtn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white;");
                } else {
                    if (player.getBalance() >= betAmount) {
                        player.setBalance(player.getBalance() - betAmount);
                        bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                        betHalves.add("2");
                        betAmounts.put("half", betAmount);
                        secondHalfBtn.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                        resultText.setText("");
                    } else {
                        resultText.setText("Za mało środków na zakład!");
                    }
                }
            } catch (NumberFormatException ex) {
                resultText.setText("Podaj poprawną stawkę!");
            }
        });

        halfButtons.put("1", firstHalfBtn);
        halfButtons.put("2", secondHalfBtn);
        root.getChildren().addAll(firstHalfBtn, secondHalfBtn);

        Button betButton = new Button("Postaw i zakręć");
        betButton.setLayoutX(268);
        betButton.setLayoutY(550);
        betButton.setStyle(
                "-fx-background-color: #d4af37;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-family: 'Arial';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        betButton.setOnAction(e -> spinWheel(wheel, resultText, countdownLabel, bilans, betButton));

        root.getChildren().addAll(wheel, arrowUp, resultText, countdownLabel, bilans,
                betAmountField, betButton);

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

        Image backgroundImage = new Image("roulletebg.jpg");
        BackgroundSize backgroundSize = new BackgroundSize(1472, 832, true, true, true, false);
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        Button backButton = new Button("Powrót do menu");
        backButton.setLayoutX(273);
        backButton.setLayoutY(620);
        backButton.setStyle(
                "-fx-background-color: #d4af37;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Arial';" +
                        "-fx-font-size: 14px;"
        );
        backButton.setOnAction(e -> {
            try {
                MainMenu menu = new MainMenu();
                menu.start(new Stage());
                stage.hide();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });
        root.getChildren().add(backButton);
        root.setBackground(new Background(background));
        Scene scene = new Scene(root, 1472, 832);
        stage.setTitle("Ruletka - Kasyno");
        stage.setScene(scene);
        stage.show();
    }

    private void handleNumberBet(Button numBtn, String num, Text resultText, Text bilans) {
        try {
            double betAmount = Double.parseDouble(betAmountField.getText());
            if (betAmount <= 0) {
                resultText.setText("Stawka musi być większa niż 0!");
                return;
            }
//
            if (betNumbers.contains(num)) {
                // Anuluj zakład
                player.setBalance(player.getBalance() + betAmounts.getOrDefault("number", 0.0));
                bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                betNumbers.remove(num);
                betAmounts.remove("number");
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
                if (player.getBalance() >= betAmount) {
                    player.setBalance(player.getBalance() - betAmount);
                    bilans.setText("Bilans: " + String.format("%.2f", player.getBalance()));
                    betNumbers.add(num);
                    betAmounts.put("number", betAmount);
                    numBtn.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                    resultText.setText("");
                } else {
                    resultText.setText("Za mało środków na zakład!");
                }
            }
        } catch (NumberFormatException ex) {
            resultText.setText("Podaj poprawną stawkę!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}