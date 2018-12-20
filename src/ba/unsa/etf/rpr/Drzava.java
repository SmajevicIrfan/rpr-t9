package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleStringProperty;

public class Drzava {
    private int id;
    private SimpleStringProperty naziv = new SimpleStringProperty("");
    private Grad glavniGrad = null;

    public Drzava() {
    }

    public Drzava(int id, String naziv, Grad glavniGrad) {
        this.setId(id);
        this.setNaziv(naziv);
        this.setGlavniGrad(glavniGrad);
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

    public Grad getGlavniGrad() {
        return this.glavniGrad;
    }

    public void setGlavniGrad(Grad glavniGrad) {
        this.glavniGrad = glavniGrad;
    }
}
