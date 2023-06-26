package ORMIntroLab;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Diablo {
    private static final String SELECT_USER_GAMES_COUNT_BY_USERNAME =
            "SELECT first_name, last_name, COUNT(ug.game_id) FROM users AS u JOIN users_games AS ug ON ug.user_id = u.id WHERE user_name = ? GROUP BY first_name, last_name";

    public static void main(String[] args) throws SQLException {

        Connection connection = getMySQLConnection();


        String username = readUsername();

        PreparedStatement statement = connection.prepareStatement(SELECT_USER_GAMES_COUNT_BY_USERNAME);
        statement.setString(1, username);

        ResultSet result = statement.executeQuery();
        boolean hasRow = result.next();

        if (hasRow) {
            System.out.printf("User: %s%n%s %s has played %d games", username, result.getString("first_name"), result.getString("last_name"), result.getInt("COUNT(ug.game_id)"));
        } else {
            System.out.println("No such user exists");
        }
    }

    private static String readUsername() {
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        return username;
    }

    private static Connection getMySQLConnection() throws SQLException {
        Properties userPass = new Properties();
        userPass.setProperty("user", "root");
        userPass.setProperty("password", "JavaDBMay2023-");

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/diablo", userPass);
        return connection;
    }
}