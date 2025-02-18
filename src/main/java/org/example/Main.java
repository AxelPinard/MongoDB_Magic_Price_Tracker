package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {

        MongoDB_Connect server = new MongoDB_Connect();
        Scanner reader = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("Welcome to the Magic card database and price tracker");
            System.out.println("1. Add Card");
            System.out.println("2. Display Cards");
            System.out.println("3. Delete Card");
            System.out.println("4. Get Scryfall prices");
            System.out.println("5.Quit");
            System.out.print("What would you like to do: ");

            choice = Integer.parseInt(reader.next());
            System.out.println();

            if (choice == 1) {
                server.addCard(reader);

            } else if (choice == 2) {
                server.showCards();

            } else if (choice == 3) {
                server.deleteCard(reader);

            } else if (choice == 4) {
                server.getPrices(reader);

            } else if (choice == 5) {
                System.exit(0);

            }
        }
    }
}