//A basic java Server using Sockets

import java.io.PrintWriter;
import java.net.*;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Server {
    //initialise instance variables
    private ServerSocket listener = null;
    private Socket socket = null;
    private Scanner in = null;
    private PrintWriter out = null;


    //constructor for server class, taking a port and a message for clients that connect
    public Server(int port) {
        try {
            listener = new ServerSocket(port); //create serversocket to listen for clients
            System.out.println("Listening on port " + port);
            //keep trying to listen for clients forever
            while (true) {
                socket = listener.accept(); //accept connection from client and establish socket
                System.out.println("Connected to client!");
                out = new PrintWriter(socket.getOutputStream(), true); //hook up PrintWriter to OutputStream of socket
                in = new Scanner(socket.getInputStream());

                while (in.hasNext()) {
                    String option = in.nextLine();
                    String[] splitString = option.split("\\s+");

                    handleMenuChoice(splitString);
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Search function to check the database for existing books
    private void handleMenuChoice(String[] splitString) {
        if (splitString.length >= 4) {
            // Converting the input to upper case (in case the user typed it in wrong)
            String convertedString = splitString[1].toUpperCase();
            // Checking to see if one of the keywords SEARCH or ADD has been triggered
            if (Objects.equals(convertedString, "ADD")) {
                insertIntoTable(splitString);
            } else if (Objects.equals(convertedString, "SEARCH")) {
                String searchField = splitString[2].toLowerCase();
                String SQLField = valueToSQLField(searchField);
                if (SQLField.equals("error")) {
                    out.println("Incorrect field entered!");
                } else {
                    searchTable(splitString, SQLField);
                }
            } else if (Objects.equals(convertedString, "UPDATE")) {
                String searchField = splitString[2].toLowerCase();
                String SQLField = valueToSQLField(searchField);
                if (SQLField.equals("error")) {
                    out.println("Incorrect field entered!");
                } else {
                    modifyTable(splitString, SQLField);
                }
            } else if (Objects.equals(convertedString, "DELETE")) {
                String searchField = splitString[2].toLowerCase();
                String SQLField = valueToSQLField(searchField);
                if (SQLField.equals("error")) {
                    out.println("Incorrect field entered!");
                } else {
                    deleteTable(splitString, SQLField);
                }
            } else {
                out.println("Invalid input! Please use the SEARCH or ADD keywords.");
            }
        } else {
            out.println("Invalid input! Please make sure your input is at least 3 words long.");
        }
    }

    private String valueToSQLField(String searchField) {
        switch (searchField) {
            case "id":
                return "book_id";
            case "title":
                return "book_title";
            case "isbn":
                return "book_isbn";
            case "author":
                return "book_author";
            case "publisher":
                return "book_publisher";
            case "language":
                return "book_language";
            default:
                return "error";
        }
    }

    private void searchTable(String[] splitString, String SQLField) {
        if (splitString.length == 4) {
            try (
                    //Create a connection
                    Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/csc8425_assessment?useSSL=false",
                            "root", "YouTube&21");   // For MySQL only
                    // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

                    //Create a statement in the SQL connection
                    Statement statement = conn.createStatement();
            ) {
                //Build SQL statement
                String search = "SELECT * FROM `csc8425_assessment`.`book` WHERE "
                        + SQLField + "='" + splitString[3] + "';";
                System.out.println("The SQL statement is: " + search + "\n");

                // Execute Query
                ResultSet resultSet = statement.executeQuery(search);

                //Print out results if a match is found
                if (resultSet.next()) {
                    while (resultSet.next()) {
                        out.println("ISBN: " + resultSet.getString(2) + ", Title: " +
                                resultSet.getString(3) + ", Author: " + resultSet.getString(4) +
                                ", Publisher: " + resultSet.getString(5) + ", Language: " +
                                resultSet.getString(6));
                    }
                } else out.println("There are no matches for this search term!");
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Method failed");
            }
        } else out.println("Invalid search! Please use the format SEARCH (Search Type e.g. title, ISBN) (Search term");
    }


    public void insertIntoTable(String[] insertString) {
        if (insertString.length == 6) {
            try (
                    //Create a connection
                    Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/csc8425_assessment?useSSL=false",
                            "root", "YouTube&21");   // For MySQL only
                    // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

                    //Create a statement in the SQL connection
                    Statement statement = conn.createStatement();
            ) {
                //Build SQL statment
                String insert = "INSERT INTO `csc8425_assessment`.`book`" +
                        "(`book_isbn`," +
                        "`book_title`," +
                        "`book_author`," +
                        "`book_publisher`," +
                        "`book_language`)" +
                        "VALUES" +
                        "('" + insertString[1] + "'," +
                        "'" + insertString[2] + "'," +
                        "'" + insertString[3] + "'," +
                        "'" + insertString[4] + "'," +
                        "'" + insertString[5] + "');";
                System.out.println("The SQL statement is: " + insert + "\n");

                //Execute Query
                statement.execute(insert);
                out.println("Book added!");
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Method failed");
            }

        } else out.println("Invalid input! Please use the format ADD ISBN Book_Title Author Publisher Language");
    }

    public void modifyTable(String[] modifyString, String SQLField) {
        if (modifyString.length == 5) {
            try (
                    //Create a connection
                    Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/csc8425_assessment?useSSL=false",
                            "root", "YouTube&21");   // For MySQL only
                    // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

                    //Create a statement in the SQL connection
                    Statement statement = conn.createStatement();
            ) {
                //Build SQL statement
                String update = "UPDATE `csc8425_assessment`.`book` " + "SET " + SQLField + "='" + modifyString[4] + "' WHERE "
                        + SQLField + "='" + modifyString[3] + "';";
                System.out.println("The SQL statement is: " + update + "\n");

                // Execute Query
                statement.execute(update);

                out.println("Updated successfully! " + SQLField + " with the value " + modifyString[4] +
                        " have been changed to " + modifyString[3]);

            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Method failed");
            }
        } else
            out.println("Invalid update! Please use the format (Table) update (Value to update) <Old Value> <New Value>");
    }

    public void deleteTable(String[] deleteString, String SQLField){
        if (deleteString.length == 4) {
            try (
                    //Create a connection
                    Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/csc8425_assessment?useSSL=false",
                            "root", "YouTube&21");   // For MySQL only
                    // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

                    //Create a statement in the SQL connection
                    Statement statement = conn.createStatement();
            ) {
                //Build SQL statement
                String delete = "DELETE FROM `csc8425_assessment`.`book` WHERE "
                        + SQLField + "='" + deleteString[3] + "';";
                System.out.println("The SQL statement is: " + delete + "\n");

                // Execute Query
                statement.execute(delete);

                out.println("Deleted successfully! " + SQLField + " with the value " + deleteString[3] +
                        " have been deleted.");

            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Method failed");
            }
        } else
            out.println("Invalid update! Please use the format (Table) delete (Value to delete) <Value>");
    }

    public static void main(String[] args) {
        Server server = new Server(54321);
    }
}
