package ORMIntroExercises;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class RemoveVillain {

    private static final String GET_VILLAIN_NAME_AND_COUNT_MINIONS_BY_VILLAIN_ID = "SELECT v.name, COUNT(mv.minion_id) AS count_minions FROM villains AS v JOIN minions_villains AS mv ON mv.villain_id = v.id WHERE id = ? GROUP BY villain_id;";
    private static final String DELETE_MINIONS_AND_VILLAIN_FROM_COMMON_TABLE = "DELETE FROM minions_villains WHERE villain_id = ?";
    private static final String DELETE_VILLAIN = "DELETE FROM villains WHERE id = ?";
    private static final String PRINT_SUCCESSFUL_OPERATION = "%s was deleted%n%d minions released";
    private static final String PRINT_UNSUCCESSFUL_OPERATION = "No such villain was found";

    public static void main(String[] args) throws SQLException {

//        Write a program that receives an ID of a villain, deletes him from the database and releases his minions from serving him.
//        As an output print the name of the villain and the number of minions released.
//        Make sure all operations go as planned, otherwise do not make any changes to the database.
//        For the output use the format given in the examples.

        final Connection connection = Utils.getSQLConnection();

        final int villainId = new Scanner(System.in).nextInt();

        final PreparedStatement getVillainNameAndCountMinions = connection.prepareStatement(GET_VILLAIN_NAME_AND_COUNT_MINIONS_BY_VILLAIN_ID);

        getVillainNameAndCountMinions.setInt(1, villainId);
        ResultSet villainResult = getVillainNameAndCountMinions.executeQuery();

        if (!villainResult.next()) {
            System.out.println(PRINT_UNSUCCESSFUL_OPERATION);
            connection.close();
            return;
        }

        final String villainName = villainResult.getString("v.name");
        final int countMinions = villainResult.getInt("count_minions");

        connection.setAutoCommit(false);

        try {
            deleteVillainReleaseMinions(connection, villainId);
            connection.commit();
            System.out.printf(PRINT_SUCCESSFUL_OPERATION, villainName, countMinions);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            connection.rollback();
        }

        connection.close();
    }

    private static void deleteVillainReleaseMinions(Connection connection, int villainId) throws SQLException {
        final PreparedStatement releaseMinionsFromVillain = connection.prepareStatement(DELETE_MINIONS_AND_VILLAIN_FROM_COMMON_TABLE);
        releaseMinionsFromVillain.setInt(1, villainId);
        releaseMinionsFromVillain.executeUpdate();

        final PreparedStatement deleteVillain = connection.prepareStatement(DELETE_VILLAIN);
        deleteVillain.setInt(1, villainId);
        deleteVillain.executeUpdate();
    }
}
