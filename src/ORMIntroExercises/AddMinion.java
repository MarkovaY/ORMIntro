package ORMIntroExercises;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AddMinion {

    private static final String PRINT_ADD_VILLAIN_TO_DATABASE_FORMAT = "Villain %s was added to the database.%n";
    private static final String GET_VILLAIN_BY_NAME = "SELECT * FROM villains WHERE name = ?;";
    private static final String INSERT_VILLAIN_INTO_TABLE = "INSERT INTO villains (name, evilness_factor) VALUES (?, 'evil')";

    private static final String PRINT_ADD_MINION_TO_VILLAIN_FORMAT = "Successfully added %s to be minion of %s.%n";
    private static final String ADD_MINION_TO_MINIONS_TABLE = "INSERT INTO minions (name, age, town_id) VALUES (?, ?, ?);";
    private static final String ADD_MINION_TO_VILLAIN = "INSERT INTO minions_villains (minion_id, villain_id) VALUES (?, ?);";
    private static final String CHECK_MINION_ID = "(SELECT * FROM minions WHERE name = ?)";
    private static final String CHECK_VILLAIN_ID = "(SELECT * FROM villains WHERE name = ?)";

    private static final String CHECK_TOWN_ID = "(SELECT id FROM towns WHERE name = ?)";

    private static final String PRINT_ADD_TOWN_TO_DATABASE_FORMAT = "Town %s was added to the database.%n";
    private static final String GET_TOWN_BY_NAME = "SELECT name FROM towns WHERE name = ?;";
    private static final String INSERT_TOWN_INTO_TABLE = "INSERT INTO towns (name) VALUE (?);";

    public static void main(String[] args) throws SQLException {

//        Write a program that reads information about a minion and its villain and adds it to the database.
//        In case the town of the minion is not in the database, insert it as well.
//        In case the villain is not present in the database, add him too with the default evilness factor of “evil”.
//        Finally, set the new minion to be a servant of the villain. Print appropriate messages after each operation.

        final Connection connection = Utils.getSQLConnection();

        Scanner scanner = new Scanner(System.in);
//        Reading info about minions
        final String[] minions = scanner.nextLine().split(" ");

        final String nameMinion = minions[1];
        final int ageMinion = Integer.parseInt(minions[2]);
        final String nameTown = minions[3];
//        Reading info about villain name
        final String nameVillain = scanner.nextLine().split(" ")[1];


        addNameIfDoesNotExist(connection, nameTown, GET_TOWN_BY_NAME, INSERT_TOWN_INTO_TABLE, PRINT_ADD_TOWN_TO_DATABASE_FORMAT);

        addNameIfDoesNotExist(connection, nameVillain, GET_VILLAIN_BY_NAME, INSERT_VILLAIN_INTO_TABLE, PRINT_ADD_VILLAIN_TO_DATABASE_FORMAT);

        addMinionToTable(connection, nameMinion, ageMinion, nameTown);

        addMinionToVillain(connection, nameMinion, nameVillain);

        connection.close();
    }

    private static void addMinionToVillain(Connection connection, String nameMinion, String nameVillain) throws SQLException {
//        Adding the new minions as servants of the given villains

        int minionId = checkId(connection, CHECK_MINION_ID, nameMinion);

        int villainId = checkId(connection, CHECK_VILLAIN_ID, nameVillain);


        final PreparedStatement addMinionToVillain = connection.prepareStatement(ADD_MINION_TO_VILLAIN);

        addMinionToVillain.setInt(1, minionId);
        addMinionToVillain.setInt(2, villainId);

        addMinionToVillain.executeUpdate();

        System.out.printf(PRINT_ADD_MINION_TO_VILLAIN_FORMAT, nameMinion, nameVillain);
    }

    private static int checkId(Connection connection, String statement, String name) throws SQLException {
//        A method checking the ids

        final PreparedStatement checkId = connection.prepareStatement(statement);
        checkId.setString(1, name);
        final ResultSet result = checkId.executeQuery();
        result.next();
        return result.getInt("id");
    }

    private static void addMinionToTable(Connection connection, String nameMinion, int ageMinion, String nameTown) throws SQLException {
//        Adding the new minion to table minions

        final PreparedStatement addMinionToMinionsTable = connection.prepareStatement(ADD_MINION_TO_MINIONS_TABLE);

        int townId = checkId(connection, CHECK_TOWN_ID, nameTown);

        addMinionToMinionsTable.setString(1, nameMinion);
        addMinionToMinionsTable.setInt(2, ageMinion);
        addMinionToMinionsTable.setInt(3, townId);

        addMinionToMinionsTable.executeUpdate();
    }

    private static void addNameIfDoesNotExist(Connection connection, String name, String getByName, String insertIntoTable, String printAddToDatabaseFormat) throws SQLException {
//        Adding names of villain or town, if they don't exist to the respective table in the database

        final PreparedStatement checkWhetherNameExists = connection.prepareStatement(getByName);

        checkWhetherNameExists.setString(1, name);

        final ResultSet result = checkWhetherNameExists.executeQuery();

        if (!result.next()) {
            final PreparedStatement addNameToTable = connection.prepareStatement(insertIntoTable);

            addNameToTable.setString(1, name);
            addNameToTable.executeUpdate();

            System.out.printf(printAddToDatabaseFormat, name);
        }
    }
}
