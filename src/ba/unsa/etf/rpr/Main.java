package ba.unsa.etf.rpr;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GeografijaDAO dao = GeografijaDAO.getInstance();

        Drzava ujedinjeno_kraljevstvo = dao.nadjiDrzavu("Ujedinjeno Kraljevstvo");
        Grad grad = new Grad();
        grad.setNaziv("Bristol");
        grad.setBrojStanovnika(459300);
        grad.setDrzava(ujedinjeno_kraljevstvo);
        dao.dodajGrad(grad);

        final ArrayList<Grad> gradovi = dao.gradovi();

        for (Grad city : gradovi) {
            System.out.println(city.getNaziv());
        }

        //System.out.println("Gradovi su:\n" + ispisiGradove());
        //glavniGrad();
    }
}
