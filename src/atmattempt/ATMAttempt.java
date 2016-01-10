/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atmattempt;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kelok
 */
public class ATMAttempt {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        int n;
        int bal;
        int destAccountID;
        int amt;

        System.out.println("ATM Attempt!");
        System.out.println("How many accounts? : ");
        n = sc.nextInt();
        Account[] accounts = new Account[n];
        Client[] clients = new Client[n];

        System.out.println("Accounts ");
        System.out.println("================");
        for (int i = 0; i < n; i++) {
            System.out.println("Enter initial balance for account " + i + ":");
            bal = sc.nextInt();
            accounts[i] = new Account(i, bal);
        }

        System.out.println("Transactions ");
        System.out.println("================");
        for (int i = 0; i < n; i++) {
            System.out.println("Enter destination account id from account " + i + " (-1 if no transfer) ");
            destAccountID = sc.nextInt();
            if (destAccountID < accounts.length) {
                if (destAccountID != -1) {
                    System.out.println("Enter transfer amount: ");
                    amt = sc.nextInt();
                    clients[i] = new Client(accounts[i], accounts[destAccountID], amt);
                } else {
                    clients[i] = new Client();               //self transfer
                }
            } else {
                System.out.println("Invalid destination account ID.");
            }
        }

        for (int i = 0; i < clients.length; i++) {
            clients[i].start();
        }

        System.out.println("Transferring process is happening...");

        try {
            for (int i = 0; i < clients.length; i++) {
                clients[i].join();
            }

            System.out.println("Transaction all done...");

            for (int i = 0; i < accounts.length; i++) {
                System.out.println("Account ID : " + i + " current balance: " + accounts[i].bal);
            }
        } catch (InterruptedException ex) {

        }
    }

}
