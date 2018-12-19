package ba.unsa.etf.rpr;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

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

    Grad glavniGrad(String drzava) {
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
