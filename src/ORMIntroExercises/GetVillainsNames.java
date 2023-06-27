package ORMIntroExercises;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetVillainsNames {
    private static final String GET_VILLAINS_NAMES = "SELECT v.name, COUNT(DISTINCT mv.minion_id) AS count_minions FROM villains v JOIN minions_villains mv ON v.id = mv.villain_id GROUP BY v.id HAVING count_minions > ? ORDER BY count_minions DESC";

    private static final String STRING_FORMAT = "%s %d";

    public static void main(String[] args) throws SQLException {

//        Write a program that prints on the console all villains’ names and their number of minions.
//        Get only the villains who have more than 15 minions. Order them by a number of minions in descending order.

        final Connection connection = Utils.getSQLConnection();

        final PreparedStatement statement = connection.prepareStatement(GET_VILLAINS_NAMES);

        statement.setInt(1, 15);

        final ResultSet result = statement.executeQuery();

        while (result.next()) {
            print(result);
        }

        connection.close();
    }

    private static void print(ResultSet result) throws SQLException {
        String name = result.getString("v.name");
        int count = result.getInt("count_minions");
        System.out.printf(STRING_FORMAT, name, count);
    }
}
