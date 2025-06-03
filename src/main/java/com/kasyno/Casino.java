package com.kasyno;

import com.kasyno.auth.LoginScreen;
import com.kasyno.menu.MainMenu;

import javafx.application.Application;

public class Casino {

    public static void main(String[] args) {
        com.kasyno.database.UserDAO.initializeDatabase();
        Application.launch(LoginScreen.class, args);  // Startujemy od logowania
    }
}
