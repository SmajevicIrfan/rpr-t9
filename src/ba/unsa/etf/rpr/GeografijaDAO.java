package ba.unsa.etf.rpr;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private static Connection connection;

    private static Statement statement;
    private static PreparedStatement countryAndCapitalQuery;
    private static PreparedStatement cityAndCountryQuery;
    private static PreparedStatement queryForCountryID;
    private static PreparedStatement queryForCityID;
    private static PreparedStatement alreadyExistsCity;
    private static PreparedStatement alreadyExistsCountry;
    private static PreparedStatement cityAdditionQuery;
    private static PreparedStatement countryAdditionQuery;
    private static PreparedStatement countryCapitalUpdateQuery;
    private static PreparedStatement capitalCountryUpdateQuery;
    private static PreparedStatement cityModificationQuery;

    public static GeografijaDAO getInstance() {
        if (instance == null) {
            instance = new GeografijaDAO();
        }

        return instance;
    }

    public static void removeInstance() {
        instance = null;
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Nothing to close");
        }
    }

    private GeografijaDAO() {
        try {
            String gradCreationQuery = new String(Files.readAllBytes(Paths.get("resources/grad.sql")), StandardCharsets.UTF_8);
            String drzavaCreationQuery = new String(Files.readAllBytes(Paths.get("resources/drzava.sql")), StandardCharsets.UTF_8);

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:baza.db");

            statement = connection.createStatement();
            statement.execute(gradCreationQuery);
            statement.execute(drzavaCreationQuery);

            addInitialData();

            /*
             * Prepared statements
             */
            countryAndCapitalQuery = connection.prepareStatement("SELECT drzava.id, grad.id, grad.naziv, grad.broj_stanovnika FROM drzava, grad WHERE drzava.glavni_grad = grad.id AND drzava.naziv = ?");
            cityAndCountryQuery = connection.prepareStatement("SELECT grad.id, grad.naziv, grad.broj_stanovnika, drzava.id, drzava.naziv FROM grad, drzava WHERE grad.id = drzava.glavni_grad AND drzava.naziv = ?");
            queryForCountryID = connection.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            queryForCityID = connection.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
            alreadyExistsCity = connection.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
            alreadyExistsCountry = connection.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            cityAdditionQuery = connection.prepareStatement("INSERT INTO grad VALUES (null, ?, ?, ?)");
            countryAdditionQuery = connection.prepareStatement("INSERT INTO drzava VALUES (null, ?, ?)");
            countryCapitalUpdateQuery = connection.prepareStatement("UPDATE grad SET drzava = ? WHERE naziv = ?");
            capitalCountryUpdateQuery = connection.prepareStatement("UPDATE drzava SET glavni_grad = ? WHERE naziv = ?");
            cityModificationQuery = connection.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ? WHERE id = ?");
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Grad glavniGrad(String drzava) {
        try {
            cityAndCountryQuery.setString(1, drzava);

            final ResultSet result = cityAndCountryQuery.executeQuery();

            if (!result.next()) {
                return null;
            }

            Grad res = new Grad(
                    result.getInt(1),
                    result.getString(2),
                    result.getInt(3),
                    new Drzava(
                            result.getInt(4),
                            result.getString(5),
                            null)
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
            queryForCountryID.setString(1, drzava);

            final ResultSet result = queryForCountryID.executeQuery();

            if (!result.next()) {
                return;
            }

            statement.executeUpdate("DELETE FROM grad WHERE drzava = " + result.getInt(1));
            statement.executeUpdate("DELETE FROM drzava WHERE id = " + result.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Grad> gradovi() {
        try {
            final ResultSet result = statement.executeQuery("SELECT g.id, g.naziv, g.broj_stanovnika, d.id, d.naziv, gg.id, gg.naziv, gg.broj_stanovnika FROM grad g, drzava d, grad gg WHERE g.drzava = d.id AND d.glavni_grad = gg.id ORDER BY g.broj_stanovnika DESC");

            ArrayList<Grad> returnValue = new ArrayList<>();

            while (result.next()) {
                Grad newCity = new Grad(
                        result.getInt(1),
                        result.getString(2),
                        result.getInt(3),
                        new Drzava(
                                result.getInt(4),
                                result.getString(5),
                                null)
                );
                newCity.getDrzava().setGlavniGrad(new Grad(
                        result.getInt(6),
                        result.getString(7),
                        result.getInt(8),
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
            if (setIfNotExists(alreadyExistsCity, cityAdditionQuery, grad.getNaziv()))  {
                return;
            }
            cityAdditionQuery.setInt(2, grad.getBrojStanovnika());

            if (grad.getDrzava() == null) {
                cityAdditionQuery.setNull(3, Types.INTEGER);
                cityAdditionQuery.executeQuery();
                return;
            }

            queryForCountryID.setString(1, grad.getDrzava().getNaziv());

            ResultSet result = queryForCountryID.executeQuery();
            if (!result.next()) {
                cityAdditionQuery.setNull(3, Types.INTEGER);
                cityAdditionQuery.executeUpdate();

                dodajDrzavu(grad.getDrzava());

                result = queryForCountryID.executeQuery();

                countryCapitalUpdateQuery.setInt(1, result.getInt(1));
                countryCapitalUpdateQuery.setString(2, grad.getNaziv());
                countryCapitalUpdateQuery.executeUpdate();
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
            if (setIfNotExists(alreadyExistsCountry, countryAdditionQuery, drzava.getNaziv())) {
                return;
            }

            if (drzava.getGlavniGrad() == null) {
                countryAdditionQuery.setNull(2, Types.INTEGER);
                countryAdditionQuery.executeQuery();
                return;
            }

            queryForCityID.setString(1, drzava.getGlavniGrad().getNaziv());

            ResultSet result = queryForCityID.executeQuery();
            if (!result.next()) {
                countryAdditionQuery.setNull(2, Types.INTEGER);
                countryAdditionQuery.executeUpdate();

                dodajGrad(drzava.getGlavniGrad());

                result = queryForCityID.executeQuery();

                capitalCountryUpdateQuery.setInt(1, result.getInt(1));
                capitalCountryUpdateQuery.setString(2, drzava.getNaziv());
                capitalCountryUpdateQuery.executeUpdate();
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
            cityModificationQuery.setString(1, grad.getNaziv());
            cityModificationQuery.setInt(2, grad.getBrojStanovnika());
            cityModificationQuery.setInt(3, grad.getId());

            cityModificationQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Drzava nadjiDrzavu(String drzava) {
        try {
            countryAndCapitalQuery.setString(1, drzava);

            final ResultSet result = countryAndCapitalQuery.executeQuery();
            if (!result.next()) {
                return null;
            }

            Drzava returnValue = new Drzava(
                    result.getInt(1),
                    drzava,
                    new Grad(
                            result.getInt(2),
                            result.getString(3),
                            result.getInt(4),
                            null
                    )
            );
            returnValue.getGlavniGrad().setDrzava(returnValue);

            return returnValue;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean setIfNotExists(PreparedStatement alreadyExistsQuery, PreparedStatement additionQuery, String naziv) throws SQLException {
        alreadyExistsQuery.setString(1, naziv);
        final ResultSet existing = alreadyExistsQuery.executeQuery();
        if (existing.next()) {
            return true;
        }

        additionQuery.setString(1, naziv);
        return false;
    }

    private void addInitialData() throws SQLException {
        if (!statement.executeQuery("SELECT * FROM grad").next()) {
            statement.executeUpdate("INSERT INTO grad (id, naziv, broj_stanovnika, drzava) VALUES (1, 'Pariz', 2206488, 1)");
            statement.executeUpdate("INSERT INTO grad (id, naziv, broj_stanovnika, drzava) VALUES (2, 'London', 8825000, 2)");
            statement.executeUpdate("INSERT INTO grad (id, naziv, broj_stanovnika, drzava) VALUES (3, 'Beč', 1899055, 3)");
            statement.executeUpdate("INSERT INTO grad (id, naziv, broj_stanovnika, drzava) VALUES (4, 'Manchester', 545500, 2)");
            statement.executeUpdate("INSERT INTO grad (id, naziv, broj_stanovnika, drzava) VALUES (5, 'Graz', 280200, 3)");
        }

        if (!statement.executeQuery("SELECT * FROM drzava").next()) {
            statement.executeUpdate("INSERT INTO drzava (id, naziv, glavni_grad) VALUES (1, 'Francuska', 1)");
            statement.executeUpdate("INSERT INTO drzava (id, naziv, glavni_grad) VALUES (2, 'Velika Britanija', 2)");
            statement.executeUpdate("INSERT INTO drzava (id, naziv, glavni_grad) VALUES (3, 'Austrija', 3)");
        }
    }
}
