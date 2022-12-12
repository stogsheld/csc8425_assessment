/*
    A basic Java server that allows the client to access various tables within the book depository database.

    Functionality has been added for books and customers (i.e. the user is able to access data in these tables). The
    program is able to add/search/update/delete data from both of these tables.

    NOTE - for the update/delete functions to work, you need to go into MySQL Workbench -> Preferences -> SQL Editor
    and disable the 'Safe Updates' box, then restart MySQL.
 */

import java.io.PrintWriter;
import java.net.*;
import java.sql.*;
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

            // Checking which table is to be accessed
            String tableField = splitString[0].toLowerCase();
            System.out.println("Table Accessed: " + tableField);


            // Checking to see if one of the keywords has been triggered
            switch (convertedString) {
                case "ADD":
                    insertIntoTable(splitString, tableField);
                    break;
                case "SEARCH": {
                    //Checking which field needs to be targeted in the SEARCH/UPDATE/DELETE fields
                    String searchField = splitString[2].toLowerCase();
                    String SQLField = valueToSQLField(searchField, tableField);
                    if (SQLField.equals("error")) {
                        out.println("Incorrect field entered!");
                    } else {
                        searchTable(splitString, tableField, SQLField);
                    }
                    break;
                }
                case "UPDATE": {
                    //Checking which field needs to be targeted in the SEARCH/UPDATE/DELETE fields
                    String searchField = splitString[2].toLowerCase();
                    String SQLField = valueToSQLField(searchField, tableField);
                    if (SQLField.equals("error")) {
                        out.println("Incorrect field entered!");
                    } else {
                        modifyTable(splitString, tableField, SQLField);
                    }
                    break;
                }
                case "DELETE": {
                    //Checking which field needs to be targeted in the SEARCH/UPDATE/DELETE fields
                    String searchField = splitString[2].toLowerCase();
                    String SQLField = valueToSQLField(searchField, tableField);
                    if (SQLField.equals("error")) {
                        out.println("Incorrect field entered!");
                    } else {
                        deleteTable(splitString, tableField, SQLField);
                    }
                    break;
                }
                default:
                    out.println("Invalid input! Please use one of the commands displayed above.");
                    break;
            }
        } else {
            out.println("Invalid input! Please make sure your input is at least 4 words long.");
        }
    }


    // Looks at which table is being accessed, and which value from the table is to be updated/searched/deleted
    private String valueToSQLField(String searchField, String tableField) {

        switch (tableField) {
            case "book":
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
            case "customer":
                switch (searchField) {
                    case "name":
                        return "customer_name";
                    case "address":
                        return "customer_post_address";
                    case "phone":
                        return "customer_contact_number";
                    default:
                        return "error";
                }
            default:
                return "error";
        }
    }


    // Searches the chosen table and returns results based on the column & value entered
    private void searchTable(String[] splitString, String tableField, String SQLField) {
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
                String search = "SELECT * FROM `csc8425_assessment`.`" + tableField + "` WHERE "
                        + SQLField + "='" + splitString[3] + "';";
                System.out.println("The SQL statement is: " + search + "\n");

                // Execute Query
                ResultSet resultSet = statement.executeQuery(search);

                //Print out results if a match is found
                if (resultSet.next()) {
                    if (tableField.equals("book")) {
                        out.println("ISBN: " + resultSet.getString(2) + ", Title: " +
                                resultSet.getString(3) + ", Author: " + resultSet.getString(4) +
                                ", Publisher: " + resultSet.getString(5) + ", Language: " +
                                resultSet.getString(6));
                    } else if (tableField.equals("customer")) {
                        out.println("Name: " + resultSet.getString(2) + ", Address: " +
                                resultSet.getString(3) + ", Phone Number: "
                                + resultSet.getString(4));
                    }
                } else out.println("There are no matches for this search term!");

            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Method failed");
            }
        } else
            out.println("Invalid search! Please use the format SEARCH (Search Type e.g. title, ISBN) <Search term>");
    }


    // Adds a new row to the chosen table with user entered values
    public void insertIntoTable(String[] insertString, String tableField) {
        if (insertString.length == 7 || insertString.length == 5) {
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
                if (tableField.equals("book")) {
                    String insert = "INSERT INTO `csc8425_assessment`.`book`" +
                            "(`book_isbn`," +
                            "`book_title`," +
                            "`book_author`," +
                            "`book_publisher`," +
                            "`book_language`)" +
                            "VALUES" +
                            "('" + insertString[2] + "'," +
                            "'" + insertString[3] + "'," +
                            "'" + insertString[4] + "'," +
                            "'" + insertString[5] + "'," +
                            "'" + insertString[6] + "');";
                    System.out.println("The SQL statement is: " + insert + "\n");
                    //Execute Query
                    statement.execute(insert);
                    out.println("Book added!");
                } else if (tableField.equals("customer")) {
                    String insert = "INSERT INTO `csc8425_assessment`.`customer`" +
                            "(`customer_name`," +
                            "`customer_post_address`," +
                            "`customer_contact_number`)" +
                            "VALUES" +
                            "('" + insertString[2] + "'," +
                            "'" + insertString[3] + "'," +
                            "'" + insertString[4] + "');";
                    System.out.println("The SQL statement is: " + insert + "\n");
                    //Execute Query
                    statement.execute(insert);
                    out.println("Customer added!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Method failed");
            }

        } else out.println("Invalid input! Please refer to the commands above to add a book/customer.");
    }


    // Replaces all values in a chosen table with another value
    public void modifyTable(String[] modifyString, String tableField, String SQLField) {
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
                String update = "UPDATE `csc8425_assessment`.`" + tableField + "` " + "SET " + SQLField + "='" + modifyString[4] +
                        "' WHERE " + SQLField + "='" + modifyString[3] + "';";
                System.out.println("The SQL statement is: " + update + "\n");

                // Execute Query
                statement.execute(update);

                out.println("Updated successfully! " + SQLField + " with the value " + modifyString[3] +
                        " have been changed to " + modifyString[4]);

            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Method failed");
            }
        } else
            out.println("Invalid update! Please use the format (Table) update (Value to update) <Old Value> <New Value>");
    }


    // Deletes all rows in a chosen table with the chosen value
    public void deleteTable(String[] deleteString, String tableField, String SQLField) {
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
                String delete = "DELETE FROM `csc8425_assessment`.`" + tableField + "` WHERE "
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
