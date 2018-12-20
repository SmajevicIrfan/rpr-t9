package ba.unsa.etf.rpr;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = new GeografijaDAO();
    private static Connection connection;

    public static GeografijaDAO getInstance() {
        if (instance == null) {
            instance = new GeografijaDAO();
        }

        return instance;
    }

    public static void removeInstance() {
        instance = null;
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
            PreparedStatement query = connection.prepareStatement("SELECT grad.naziv, grad.broj_stanovnika, drzava.naziv FROM grad, drzava WHERE grad.id = drzava.glavni_grad AND drzava.naziv = ?");
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
            res.getDrzava().setGlavniGrad(res);

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

    public ArrayList<Grad> gradovi() {
        try {
            Statement cityQuery = connection.createStatement();
            final ResultSet result = cityQuery.executeQuery("SELECT naziv, broj_stanovnika, drzava FROM grad ORDER BY broj_stanovnika DESC");

            ArrayList<Grad> returnValue = new ArrayList<>();

            while (result.next()) {
                Statement countryQuery = connection.createStatement();
                final ResultSet country = countryQuery.executeQuery("SELECT drzava.naziv, grad.naziv, grad.broj_stanovnika FROM drzava, grad WHERE drzava.glavni_grad = grad.id AND drzava.id = " + result.getInt(3));
                if (!country.next()) {
                    continue;
                }

                Grad newCity = new Grad(
                        result.getString(1),
                        result.getInt(2),
                        new Drzava(country.getString(1), null)
                );
                newCity.getDrzava().setGlavniGrad(new Grad(
                        country.getString(2),
                        country.getInt(3),
                        newCity.getDrzava()
                ));

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
            // Test if already exists
            PreparedStatement alreadyExisting = connection.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
            alreadyExisting.setString(1, grad.getNaziv());
            final ResultSet existing = alreadyExisting.executeQuery();
            if (existing.next()) {
                return;
            }

            PreparedStatement cityAdditionQuery = connection.prepareStatement("INSERT INTO grad VALUES (null, ?, ?, ?)");
            cityAdditionQuery.setString(1, grad.getNaziv());
            cityAdditionQuery.setInt(2, grad.getBrojStanovnika());

            if (grad.getDrzava() == null) {
                cityAdditionQuery.setNull(3, Types.INTEGER);
                cityAdditionQuery.executeQuery();
                return;
            }

            PreparedStatement countryIDQuery = connection.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            countryIDQuery.setString(1, grad.getDrzava().getNaziv());

            ResultSet result = countryIDQuery.executeQuery();
            if (!result.next()) {
                cityAdditionQuery.setNull(3, Types.INTEGER);
                cityAdditionQuery.executeUpdate();

                dodajDrzavu(grad.getDrzava());

                result = countryIDQuery.executeQuery();

                PreparedStatement capitalUpdateQuery = connection.prepareStatement("UPDATE grad SET drzava = ? WHERE naziv = ?");
                capitalUpdateQuery.setInt(1, result.getInt(1));
                capitalUpdateQuery.setString(2, grad.getNaziv());
                capitalUpdateQuery.executeUpdate();
            } else {
                cityAdditionQuery.setInt(3, result.getInt(1));
                cityAdditionQuery.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            // Test if already exists
            PreparedStatement alreadyExisting = connection.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            alreadyExisting.setString(1, drzava.getNaziv());
            final ResultSet existing = alreadyExisting.executeQuery();
            if (existing.next()) {
                return;
            }

            PreparedStatement countryAdditionQuery = connection.prepareStatement("INSERT INTO drzava VALUES (null, ?, ?)");
            countryAdditionQuery.setString(1, drzava.getNaziv());

            if (drzava.getGlavniGrad() == null) {
                countryAdditionQuery.setNull(2, Types.INTEGER);
                countryAdditionQuery.executeQuery();
                return;
            }

            PreparedStatement cityIDQuery = connection.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
            cityIDQuery.setString(1, drzava.getGlavniGrad().getNaziv());

            ResultSet result = cityIDQuery.executeQuery();
            if (!result.next()) {
                countryAdditionQuery.setNull(2, Types.INTEGER);
                countryAdditionQuery.executeUpdate();

                dodajGrad(drzava.getGlavniGrad());

                result = cityIDQuery.executeQuery();

                PreparedStatement capitalUpdateQuery = connection.prepareStatement("UPDATE drzava SET glavni_grad = ? WHERE naziv = ?");
                capitalUpdateQuery.setInt(1, result.getInt(1));
                capitalUpdateQuery.setString(2, drzava.getNaziv());
                capitalUpdateQuery.executeUpdate();
            } else {
                countryAdditionQuery.setInt(2, result.getInt(1));
                countryAdditionQuery.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            PreparedStatement cityModificationQuery = connection.prepareStatement("UPDATE grad SET broj_stanovnika = ? WHERE naziv = ?");
            cityModificationQuery.setInt(1, grad.getBrojStanovnika());
            cityModificationQuery.setString(2, grad.getNaziv());

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
            returnValue.getGlavniGrad().setDrzava(returnValue);

            return returnValue;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addInitialData() throws SQLException {
        Statement statement = connection.createStatement();

        if (!statement.executeQuery("SELECT * FROM grad").next()) {
            statement.executeUpdate("INSERT INTO grad (id, naziv, broj_stanovnika, drzava) VALUES (1, 'Pariz', 2206488, NULL)");
            statement.executeUpdate("INSERT INTO grad (id, naziv, broj_stanovnika, drzava) VALUES (2, 'London', 8825000, NULL)");
            statement.executeUpdate("INSERT INTO grad (id, naziv, broj_stanovnika, drzava) VALUES (3, 'Beč', 1899055, NULL)");
            statement.executeUpdate("INSERT INTO grad (id, naziv, broj_stanovnika, drzava) VALUES (4, 'Manchester', 545500, NULL)");
            statement.executeUpdate("INSERT INTO grad (id, naziv, broj_stanovnika, drzava) VALUES (5, 'Graz', 280200, NULL)");
        }

        if (!statement.executeQuery("SELECT * FROM drzava").next()) {
            statement.executeUpdate("INSERT INTO drzava (id, naziv, glavni_grad) VALUES (1, 'Francuska', 1)");
            statement.executeUpdate("INSERT INTO drzava (id, naziv, glavni_grad) VALUES (2, 'Ujedinjeno Kraljevstvo', 2)");
            statement.executeUpdate("INSERT INTO drzava (id, naziv, glavni_grad) VALUES (3, 'Austrija', 3)");
        }

        statement.executeUpdate("UPDATE grad SET drzava = 1 WHERE naziv = 'Pariz'");
        statement.executeUpdate("UPDATE grad SET drzava = 2 WHERE naziv = 'London'");
        statement.executeUpdate("UPDATE grad SET drzava = 3 WHERE naziv = 'Beč'");
        statement.executeUpdate("UPDATE grad SET drzava = 2 WHERE naziv = 'Manchester'");
        statement.executeUpdate("UPDATE grad SET drzava = 3 WHERE naziv = 'Graz'");
    }
}
