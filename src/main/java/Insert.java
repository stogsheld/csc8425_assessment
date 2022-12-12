import java.sql.*;

//This class inserts a hardcoded user to the Database.
//Take care executing this twice, as it will enter in a user with the exact same properties twice.
public class Insert {

    public static void main(String[] args) {
        try (
                //Create a connection
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/csc8425_assessment?useSSL=false",
                        "root", "YouTube&21");   // For MySQL only
                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

                //Create a statement in the SQL connection
                Statement statement = conn.createStatement();
        ){
            //Build SQL statment
            String insert = "INSERT INTO `csc8425_assessment`.`book`" +
                    "(`book_isbn`," +
                    "`book_title`," +
                    "`book_author`," +
                    "`book_publisher`," +
                    "`book_language`)" +
                    "VALUES" +
                    "('128654'," +
                    "'The Lord of the Rings'," +
                    "'J R R Tolkien'," +
                    "'Penguin'," +
                    "'English');";
            System.out.println("The SQL statement is: " + insert + "\n");

            //Execute Query
            statement.execute(insert);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}