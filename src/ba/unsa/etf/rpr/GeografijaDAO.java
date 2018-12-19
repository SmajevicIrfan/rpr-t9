package ba.unsa.etf.rpr;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeografijaDAO {
    private static GeografijaDAO instance = new GeografijaDAO();
    private static Connection connection = null;

    public static GeografijaDAO getInstance() {
        return instance;
    }

    private GeografijaDAO() {
        try {
            String gradCreationQuery = new String(Files.readAllBytes(Paths.get("resources/grad.sql")), StandardCharsets.UTF_8);
            String drzavaCreationQuery = new String(Files.readAllBytes(Paths.get("resources/drzava.sql")), StandardCharsets.UTF_8);

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:baza.db");

            Statement statement = connection.createStatement();
            statement.execute(gradCreationQuery);
            statement.execute(drzavaCreationQuery);

            addInitialData();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Grad glavniGrad(String drzava) {
        try {
            PreparedStatement query = connection.prepareStatement("SELECT grad.naziv, grad.broj_stanovnika, drzava.naziv FROM grad, drzava WHERE grad.id = drzava.id AND drzava.naziv = ?");
            query.setString(1, drzava);

            final ResultSet result = query.executeQuery();

            if (!result.next()) {
                return null;
            }

            Grad res = new Grad(
                    result.getString(1),
                    result.getInt(2),
                    new Drzava(result.getString(3), null)
            );
            res.getCountry().setCapital(res);

            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void obrisiDrzavu(String drzava) {
        try {
            PreparedStatement queryForCountryID = connection.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            queryForCountryID.setString(1, drzava);

            final ResultSet result = queryForCountryID.executeQuery();

            if (!result.next()) {
                return;
            }

            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM grad WHERE drzava = " + result.getInt(1));
            statement.executeUpdate("DELETE FROM drzava WHERE id = " + result.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Grad> gradovi() {
        try {
            Statement statement = connection.createStatement();
            final ResultSet result = statement.executeQuery("SELECT naziv, broj_stanovnika, drzava FROM grad ORDER BY broj_stanovnika DESC");

            List<Grad> returnValue = new ArrayList<>();
            while (result.next()) {
                final ResultSet country = statement.executeQuery("SELECT drzava.naziv, grad.naziv, grad.broj_stanovnika FROM drzava, grad WHERE drzava.glavni_grad = grad.id AND drzava.id = " + result.getInt(3));
                if (!country.next()) {
                    continue;
                }

                Grad newCity = new Grad(
                        result.getString(1),
                        result.getInt(2),
                        new Drzava(country.getString(1), null)
                );
                newCity.getCountry().setCapital(newCity);

                returnValue.add(newCity);
            }

            return returnValue;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void dodajGrad(Grad grad) {
        try {
            PreparedStatement cityAdditionQuery = connection.prepareStatement("INSERT INTO grad (naziv, broj_stanovnika, drzava) VALUES (?, ?, ?)");
            cityAdditionQuery.setString(1, grad.getName());
            cityAdditionQuery.setInt(2, grad.getPopulation());

            if (grad.getCountry() == null) {
                cityAdditionQuery.setNull(3, Types.INTEGER);
                cityAdditionQuery.executeQuery();
                return;
            }

            PreparedStatement countryIDQuery = connection.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            countryIDQuery.setString(1, grad.getCountry().getName());

            final ResultSet result = countryIDQuery.executeQuery();
            if (result.next()) {
                cityAdditionQuery.setInt(3, result.getInt(1));
            } else {
                cityAdditionQuery.setNull(3, Types.INTEGER);
            }

            cityAdditionQuery.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            PreparedStatement countryAdditionQuery = connection.prepareStatement("INSERT INTO drzava (naziv, glavni_grad) VALUES (?, ?)");
            countryAdditionQuery.setString(1, drzava.getName());

            if (drzava.getCapital() == null) {
                countryAdditionQuery.setNull(2, Types.INTEGER);
                countryAdditionQuery.executeQuery();
                return;
            }

            PreparedStatement cityIDQuery = connection.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
            cityIDQuery.setString(1, drzava.getCapital().getName());

            final ResultSet result = cityIDQuery.executeQuery();
            if (result.next()) {
                countryAdditionQuery.setInt(2, result.getInt(1));
            } else {
                countryAdditionQuery.setNull(2, Types.INTEGER);
            }

            countryAdditionQuery.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            PreparedStatement cityModificationQuery = connection.prepareStatement("UPDATE grad SET broj_stanovnika = ? WHERE naziv = ?");
            cityModificationQuery.setInt(1, grad.getPopulation());
            cityModificationQuery.setString(2, grad.getName());

            cityModificationQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Drzava nadjiDrzavu(String drzava) {
        try {
            PreparedStatement query = connection.prepareStatement("SELECT grad.naziv, grad.broj_stanovnika FROM drzava, grad WHERE drzava.glavni_grad = grad.id AND drzava.naziv = ?");
            query.setString(1, drzava);

            final ResultSet result = query.executeQuery();
            if (!result.next()) {
                return null;
            }

            Drzava returnValue = new Drzava(drzava, new Grad(result.getString(1), result.getInt(2), null));
            returnValue.getCapital().setCountry(returnValue);

            return returnValue;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addInitialData() throws SQLException {
        Statement statement = connection.createStatement();

        if (!statement.executeQuery("SELECT * FROM grad").next()) {
            statement.executeUpdate("INSERT INTO grad (naziv, broj_stanovnika, drzava) VALUES ('Paris', 2206488, NULL)");
            statement.executeUpdate("INSERT INTO grad (naziv, broj_stanovnika, drzava) VALUES ('London', 8825000, NULL)");
            statement.executeUpdate("INSERT INTO grad (naziv, broj_stanovnika, drzava) VALUES ('Beč', 1899055, NULL)");
            statement.executeUpdate("INSERT INTO grad (naziv, broj_stanovnika, drzava) VALUES ('Manchester', 545500, NULL)");
            statement.executeUpdate("INSERT INTO grad (naziv, broj_stanovnika, drzava) VALUES ('Graz', 280200, NULL)");
        }

        if (!statement.executeQuery("SELECT * FROM drzava").next()) {
            statement.executeUpdate("INSERT INTO drzava (naziv, glavni_grad) VALUES ('Francuka', 1)");
            statement.executeUpdate("INSERT INTO drzava (naziv, glavni_grad) VALUES ('Ujedinjeno Kraljevstvo', 2)");
            statement.executeUpdate("INSERT INTO drzava (naziv, glavni_grad) VALUES ('Austrija', 3)");
        }

        statement.executeUpdate("UPDATE grad SET drzava = 1 WHERE naziv = 'Paris'");
        statement.executeUpdate("UPDATE grad SET drzava = 2 WHERE naziv = 'London'");
        statement.executeUpdate("UPDATE grad SET drzava = 3 WHERE naziv = 'Beč'");
        statement.executeUpdate("UPDATE grad SET drzava = 2 WHERE naziv = 'Manchester'");
        statement.executeUpdate("UPDATE grad SET drzava = 3 WHERE naziv = 'Graz'");
    }
}
