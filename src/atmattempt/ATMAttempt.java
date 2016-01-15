/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atmattempt;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author kelok
 */
public class ATMAttempt {

    /**
     * @param args the command line arguments
     */
    public Scanner scan = new Scanner(System.in);
    
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        int n;
        int bal;
        int destAccountID;
        int amt = 0;

        System.out.println("ATM Attempt!");
        System.out.println("How many accounts? : ");
        n = sc.nextInt();
        ArrayList<Account>accounts = initilizeAccount(n);
        Client[] clients = new Client[n];
        
        for(Account account:accounts){
            System.out.println("Account " + account.id + ":" );
            if(account.enterPIN()){     //successful PIN
                showMenu();
            }else{
               // accounts.remove(account);                        //delete from list
            }
            System.out.println(accounts.size());
        }

        System.out.println("Transactions ");
        System.out.println("==========");
        for (int i = 0; i < n; i++) {
            destAccountID = 0;
            do {
                System.out.println("Enter destination account id from account " + i + " (Input own account id or -1 if no transfer) ");
                destAccountID = sc.nextInt();
                if (destAccountID < accounts.size()) {
                    if (destAccountID == -1 || destAccountID == i) {
                        clients[i] = new Client();                      //self transfer
                    } else {
                        System.out.println("Enter transfer amount: ");
                        amt = sc.nextInt();
                        if (amt <= accounts.get(i).bal) {
                            clients[i] = new Client(accounts.get(i), accounts.get(destAccountID), amt);
                        } else {
                            System.out.println("Insufficient fund");
                        }
                    }
                } else {
                    System.out.println("Invalid destination account ID.");
                }
            } while (!checkInput(destAccountID, accounts.get(i), accounts, amt));
        }

        for (Client client : clients) {
            client.start();
        }

        System.out.println("Transferring process is happening...");

        try {
            for (Client client : clients) {
                client.join();
            }

            System.out.println("Transaction all done...");

            for (int i = 0; i < accounts.size(); i++) {
                System.out.println("Account ID : " + i + " current balance: " + accounts.get(i).bal);
            }
        } catch (InterruptedException ex) {

        }
    }

    public static ArrayList<Account> initilizeAccount(int count) {
        ArrayList<Account> accounts = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("Accounts ");
        System.out.println("================");
        for (int i = 0; i < count; i++) {
            System.out.println("Enter initial balance for account " + i + ":");
            int bal = sc.nextInt();
            int randomPIN = (int) (Math.random() * 9000) + 1000;
            Account account = new Account(i, bal, randomPIN);
            accounts.add(account);
        }

        for (Account acc : accounts) {
            acc.showDetails();
        }
        return accounts;
    }
    
    public static void showMenu(){
        System.out.println("Menu");
        System.out.println("==========");
        System.out.println("1. Balance enquiry");
        System.out.println("2. Change PIN");
        System.out.println("3. Withdraw");
        System.out.println("4. Deposit");
        System.out.println("5. Transfer");
        System.out.println("Input choice 1-6");
    }

    public static boolean checkInput(int destID, Account src, ArrayList<Account> allAccounts, int amount) {
        if (destID >= allAccounts.size() || amount < 0 || amount > src.bal) {
            System.out.println("Error input");
            return false;
        }
        return true;
    }
    
    
}
