package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleStringProperty;

public class Drzava {
    private SimpleStringProperty naziv = new SimpleStringProperty("");
    private Grad glavniGrad = null;

    public Drzava() {
    }

    public Drzava(String naziv, Grad glavniGrad) {
        this.setNaziv(naziv);
        this.setGlavniGrad(glavniGrad);
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

    public Grad getGlavniGrad() {
        return this.glavniGrad;
    }

    public void setGlavniGrad(Grad glavniGrad) {
        this.glavniGrad = glavniGrad;
    }
}
