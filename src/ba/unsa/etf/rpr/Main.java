package ba.unsa.etf.rpr;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
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
        System.out.println(" -- Opcije -----------------");
        System.out.println("01. Ispiši sve gradove");
        System.out.println("02. Ispiši glavne gradove");
        System.out.println();

        Scanner input = new Scanner(System.in);
        System.out.print("Unesite Vaš izbor: ");
        int option = input.nextInt();

        if (option == 1) {
            System.out.println(ispisiGradove());
        } else {
            glavniGrad();
        }
    }
}
