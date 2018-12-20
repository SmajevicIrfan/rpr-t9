package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Grad {
    private int id;
    private SimpleStringProperty naziv = new SimpleStringProperty("");
    private SimpleIntegerProperty brojStanovnika = new SimpleIntegerProperty(0);
    private Drzava drzava = null;

    public Grad() {
    }

    public Grad(int id, String naziv, int brojStanovnika, Drzava drzava) {
        this.setId(id);
        this.setNaziv(naziv);
        this.setBrojStanovnika(brojStanovnika);
        this.setDrzava(drzava);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return this.naziv.get();
    }

    public SimpleStringProperty nazivProperty() {
        return this.naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv.set(naziv);
    }

    public int getBrojStanovnika() {
        return this.brojStanovnika.get();
    }

    public SimpleIntegerProperty brojStanovnikaProperty() {
        return this.brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika.set(brojStanovnika);
    }

    public Drzava getDrzava() {
        return this.drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }

    @Override
    public String toString() {
        return this.getNaziv() + " (" + this.getDrzava() + ") - " + this.getBrojStanovnika();
    }
}
