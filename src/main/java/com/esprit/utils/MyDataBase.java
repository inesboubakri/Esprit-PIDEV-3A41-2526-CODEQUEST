package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    // Attributs de connexion — adapte si tu as un mot de passe XAMPP
    private final String URL = "jdbc:mysql://localhost:3306/codyquest";
    private final String USERNAME = "root";
    private final String PASSWORD = "";  // Laisse vide si XAMPP par défaut

    private Connection connection;
    private static MyDataBase instance;

    // Constructeur privé = personne ne peut faire "new MyDataBase()" depuis l'extérieur
    private MyDataBase() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connexion à codequest database  établie !");
        } catch (SQLException e) {
            System.out.println(" Erreur connexion : " + e.getMessage());
        }
    }

    // getInstance() = l'unique point d'accès
    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}