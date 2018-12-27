package ba.unsa.etf.rpr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {
    static String ispisiGradove() {
        GeografijaDAO instance = GeografijaDAO.getInstance();
        final ArrayList<Grad> cities = instance.gradovi();

        StringBuilder result = new StringBuilder();
        for (Grad city : cities) {
            result.append(city);
            result.append("\n");
        }

        return result.toString();
    }

    static void glavniGrad() {
        GeografijaDAO instance = GeografijaDAO.getInstance();

        Scanner input = new Scanner(System.in);
        System.out.print("Unesite ime drzave: ");
        String country = input.nextLine().trim();

        Grad capital = instance.glavniGrad(country);
        if (capital != null) {
            System.out.println(String.format("Glavni grad države %s je %s", country, capital.getNaziv()));
        } else {
            System.out.println("Nepostojeća država");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Geografija");

        primaryStage.setScene(new Scene(root));

        primaryStage.show();
    }
}
