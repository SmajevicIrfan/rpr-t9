package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Grad {
    private SimpleStringProperty name = new SimpleStringProperty("");
    private SimpleIntegerProperty population = new SimpleIntegerProperty(0);
    private Drzava country = null;

    public Grad(String name, int population, Drzava country) {
        this.setName(name);
        this.setPopulation(population);
        this.setCountry(country);
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

    public int getPopulation() {
        return this.population.get();
    }

    public SimpleIntegerProperty populationProperty() {
        return this.population;
    }

    public void setPopulation(int population) {
        this.population.set(population);
    }

    public Drzava getCountry() {
        return this.country;
    }

    public void setCountry(Drzava country) {
        this.country = country;
    }
}
