//A basic java client using Sockets

import java.net.*;
import java.util.Objects;
import java.util.Scanner;
import java.io.PrintWriter;

public class Client {
    //initialise instance variables used throughout class. We don't need them to be used
    // outside this class, so we make them private
    private Socket socket = null;
    private Scanner in = null;
    private PrintWriter out = null;
    private Scanner userInput = null;

    //constructor for Client class, takes an IP address and port as arguments
    public Client(String IPaddress, int port) {
        //establish connection within try/catch
        try {
            //Attempts to make a connection at the given address and port
            socket = new Socket(IPaddress, port);
            System.out.println("Connected to server at " + IPaddress + ":" + port);
            //Connect a Scanner to the socket's InputStream to read lines from it
            in = new Scanner(socket.getInputStream());
            //Connect a PrintWriter to the OutputStream to send lines to server
            out = new PrintWriter(socket.getOutputStream(), true);
            //Connect a Scanner to system input to take command line instructions

            displayCommandMenu();
            // Displaying menu giving the user info about commands they can use

            userInput = new Scanner(System.in);
            while (userInput.hasNextLine()) {
                out.println(userInput.nextLine()); //handle output to server
                System.out.println(in.nextLine()); //print input from server
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void displayCommandMenu() {
        System.out.println("Welcome to the book depository!");
        System.out.println();
        System.out.println("--- COMMANDS ---");
        System.out.println("To search: (Table) search (Title/ISBN/Author/Publisher/Language) <Search Term>");
        System.out.println("    e.g. book search Title The_Hobbit");
        System.out.println("To add: (Table) add (All info needed)");
        System.out.println("    for books: book add <ISBN> <Title> <Author> <Publisher> <Language>");
        System.out.println();
        System.out.println("Please enter a command to begin.");
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 54321); //could also use "127.0.0.1"
    }
}