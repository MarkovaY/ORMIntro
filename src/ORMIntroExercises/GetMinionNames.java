package ORMIntroExercises;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GetMinionNames {
    //  Write a program that prints on the console all minion names and their age for a given villain id.
    //  For the output, use the formats given in the examples.
    private static final String GET_MINION_NAMES_AND_AGE_BY_VILLAIN_ID = "SELECT m.name, m.age " +
            "FROM minions AS m " +
            "JOIN minions_villains AS mv on m.id = mv.minion_id " +
            "WHERE villain_id = ?;";

    private static final String GET_VILLAIN_NAME = "SELECT name FROM villains WHERE id = ?;";

    public static void main(String[] args) throws SQLException {

        final Connection connection = Utils.getSQLConnection();

        final int villainID = new Scanner(System.in).nextInt();

        final PreparedStatement statementVillainName = connection.prepareStatement(GET_VILLAIN_NAME);

        statementVillainName.setInt(1, villainID);

        final ResultSet resultVillain = statementVillainName.executeQuery();

        if (!resultVillain.next()) {
            System.out.printf("No villain with ID %d exists in the database.", villainID);
            connection.close();
            return;
        }

        String nameVillain = resultVillain.getString("name");

        System.out.printf("Villain: %s%n", nameVillain);

        final PreparedStatement statementMinions = connection.prepareStatement(GET_MINION_NAMES_AND_AGE_BY_VILLAIN_ID);

        statementMinions.setInt(1, villainID);

        final ResultSet resultMinions = statementMinions.executeQuery();

        for (int index = 1; resultMinions.next(); index++) {
            System.out.printf("%d. %s %d%n", index, resultMinions.getString("m.name"), resultMinions.getInt("m.age"));
        }

        connection.close();
    }
}