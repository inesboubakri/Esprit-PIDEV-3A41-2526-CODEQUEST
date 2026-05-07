package com.esprit;

import com.esprit.entities.Cours;
import com.esprit.services.CoursServices;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        CoursServices cs = new CoursServices();
        try {
            // TEST AFFICHER
            //System.out.println("=== Liste des cours ===");
            //cs.afficher().forEach(System.out::println);

            // TEST AJOUTER
            Cours newCours = new Cours("Test JDBC", "Un cours de test", "Beginner", "Apprendre JDBC", 5, "", 100, 1);
             cs.ajouter(newCours);

            // TEST SUPPRIMER
             //cs.supprimer(99);

        } catch (SQLException e) {
            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }
}