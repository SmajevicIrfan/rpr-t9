package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleStringProperty;

public class Drzava {
    private SimpleStringProperty name = new SimpleStringProperty("");
    private Grad capital = null;

    public Drzava(String name, Grad capital) {
        this.setName(name);
        this.setCapital(capital);
    }

    public String getName() {
        return this.name.get();
    }

    public SimpleStringProperty nameProperty() {
        return this.name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Grad getCapital() {
        return this.capital;
    }

    public void setCapital(Grad capital) {
        this.capital = capital;
    }
}
