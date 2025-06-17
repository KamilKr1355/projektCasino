package com.kasyno;

import com.kasyno.auth.LoginScreen;
import com.kasyno.database.DatabaseManager;
import com.kasyno.menu.MainMenu;

import javafx.application.Application;

public class Casino {

    public static void main(String[] args) {
        DatabaseManager.initializeTables();
        Application.launch(LoginScreen.class, args);
    }
}
