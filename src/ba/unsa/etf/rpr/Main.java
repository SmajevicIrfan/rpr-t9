package ba.unsa.etf.rpr;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        final ArrayList<Grad> gradovi = dao.gradovi();

        for (Grad grad : gradovi) {
            System.out.println(grad.getNaziv());
        }

        //System.out.println("Gradovi su:\n" + ispisiGradove());
        //glavniGrad();
    }
}
