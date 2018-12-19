package ba.unsa.etf.rpr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GeografijaDAO {
    private static GeografijaDAO instance = new GeografijaDAO();
    private static Statement statement;

    public static GeografijaDAO getInstance() {
        return instance;
    }

    private GeografijaDAO() {
        try {
            String gradCreationQuery = new String(Files.readAllBytes(Paths.get("resources/grad.sql")), StandardCharsets.UTF_8);
            String drzavaCreationQuery = new String(Files.readAllBytes(Paths.get("resources/drzava.sql")), StandardCharsets.UTF_8);

            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:baza.db");

            statement = connection.createStatement();
            statement.execute(gradCreationQuery);
            statement.execute(drzavaCreationQuery);

            addInitialData();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addInitialData() throws SQLException {
        if (!statement.executeQuery("SELECT * FROM grad").next()) {
            statement.executeUpdate("INSERT INTO grad VALUES (1, 'Paris', 2206488, NULL)");
            statement.executeUpdate("INSERT INTO grad VALUES (2, 'London', 8825000, NULL)");
            statement.executeUpdate("INSERT INTO grad VALUES (3, 'Beč', 1899055, NULL)");
            statement.executeUpdate("INSERT INTO grad VALUES (4, 'Manchester', 545500, NULL)");
            statement.executeUpdate("INSERT INTO grad VALUES (5, 'Graz', 280200, NULL)");
        }

        if (!statement.executeQuery("SELECT * FROM drzava").next()) {
            statement.executeUpdate("INSERT INTO drzava VALUES (1, 'Francuka', 1)");
            statement.executeUpdate("INSERT INTO drzava VALUES (2, 'Ujedinjeno Kraljevstvo', 2)");
            statement.executeUpdate("INSERT INTO drzava VALUES (3, 'Austrija', 3)");
        }

        statement.executeUpdate("UPDATE grad SET drzava = 1 WHERE id = 1");
        statement.executeUpdate("UPDATE grad SET drzava = 2 WHERE id = 2");
        statement.executeUpdate("UPDATE grad SET drzava = 3 WHERE id = 3");
        statement.executeUpdate("UPDATE grad SET drzava = 2 WHERE id = 4");
        statement.executeUpdate("UPDATE grad SET drzava = 3 WHERE id = 5");
    }
}
