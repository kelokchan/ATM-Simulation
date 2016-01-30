/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atmattempt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author kelok
 */
public class ATMAttempt {

    public static Scanner sc = new Scanner(System.in);

    public static Account[] accounts;
    public static Client[] clients;
    public static ArrayList<Receipt> receiptList;
    public static final String LOCATION = "Bukit Jalil";

    public static void main(String[] args) {
        // TODO code application logic here
        int count;
        System.out.println("ATM Simulation");
        System.out.println("How many accounts? : ");
        count = sc.nextInt();

        accounts = new Account[count];
        clients = new Client[count];
        receiptList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            clients[i] = new Client();
        }

        initilizeAccount(count);

        for (Account account : accounts) {
            showMenu(account);
            showScenario(account);
        }

        System.out.println("====================");
        System.out.println("Concurrency starts");
        System.out.println("====================");

        for (Client client : clients) {
            client.start();
        }

        try {
            for (Client client : clients) {
                client.join();
            }
            System.out.println("====================");
            System.out.println("Transaction all done");
            System.out.println("====================");

            for (int i = 0; i < accounts.length; i++) {
                System.out.println("Account ID : " + i + " current balance: " + accounts[i].bal);
            }
        } catch (Exception ex) {

        }
        generateReceipt();
    }

    public static void initilizeAccount(int count) {
        System.out.println("====================");
        System.out.println("Accounts ");
        System.out.println("================");
        for (int i = 0; i < count; i++) {
            System.out.println("Enter initial balance for account " + i + ":");
            int bal = sc.nextInt();
            int randomPIN = (int) (Math.random() * 9000) + 1000;
            Account account = new Account(i, bal, randomPIN);
            accounts[i] = account;
            clients[i].srcAccount = account;    //initialize each client with account 
        }

        for (Account acc : accounts) {
            acc.showDetails();
        }
    }

    public static void showMenu(Account account) {
        String input;
        do {
            System.out.println("====================");
            System.out.println("Menu for Account " + account.id);
            System.out.println("====================");
            System.out.println("1. Balance enquiry");
            System.out.println("2. Change PIN");
            System.out.println("3. Withdrawal");
            System.out.println("4. Deposit");
            System.out.println("5. Transfer");
            System.out.println("Input choice 1-5");
            input = sc.next();

            String timeStamp = new SimpleDateFormat("dd/MM/yy hh:mma").format(Calendar.getInstance().getTime());
            Receipt receipt = null;
            switch (input) {
                case "1":
                    clients[account.id].isReadBalance = true;
                    break;
                case "2":
                    changePIN(account);
                    break;
                case "3":
                    int withAmt = withdrawalInput(account);
                    clients[account.id].withdrawalAmt = withAmt;
                    break;
                case "4":
                    int depAmt = depositInput(account);
                    clients[account.id].depositAmt = depAmt;
                    break;
                case "5":
                    int transAmt = transferInput(account);
                    break;
                default:
                    System.out.println("Invalid selection");
            }
            ATMAttempt.receiptList.add(receipt);
            System.out.println("Continue? (y/n): ");
            input = sc.next();
        } while (input.equalsIgnoreCase("y"));
    }

    public static void showScenario(Account account) {

        int attempt = 0;
        int cancelTransaction = 0;

        System.out.println("");
        System.out.println("====================");
        System.out.println("Scenario for Account " + account.id);
        System.out.println("====================");
        while (true) {
            try {
                System.out.print("No. of PIN attempt (>3 will freeze account): ");
                attempt = sc.nextInt();
                clients[account.id].pinAttempt = attempt;
                if (attempt <= 3) {
                    System.out.print("Cancel mid-way transaction? (1 for yes 0 for no): ");
                    cancelTransaction = sc.nextInt();
                    if (cancelTransaction == 1) {
                        clients[account.id].hasFailedTrans = true;
                    }
                }
                break;
            } catch (InputMismatchException ex) {
                System.out.println("Invalid input. Retry");
            }
        }
    }

    public static void changePIN(Account account) {
        System.out.println("Enter new PIN: ");
        int newPIN;
        while (true) {
            try {
                newPIN = sc.nextInt();
                if (String.valueOf(newPIN).length() == 4) {
                    break;
                } else {
                    System.out.println("Invalid length of PIN. Re-enter");
                }
            } catch (InputMismatchException ex) {
                System.out.println("Invalid new PIN. Re-enter");
                sc.nextLine();
            }
        }
        account.pin = newPIN;
        System.out.println("New PIN is " + account.pin);
    }

    public static int withdrawalInput(Account account) {
        System.out.println("You may withdraw only in multiples of RM20. Enter amount: ");
        int amount;
        while (true) {
            try {
                amount = sc.nextInt();
                if (amount <= account.bal) {
                    if (amount % 20 == 0) {
                        break;
                    } else {
                        System.out.println("Invalid amount. Re-enter");
                    }
                } else {
                    System.out.println("Insufficient fund!");
                }

            } catch (InputMismatchException ex) {
                System.out.println("Invalid amount. Re-enter");
                sc.nextLine();
            }
        }
        return amount;
    }

    public static int depositInput(Account account) {
        int selection;
        int amount;
        while (true) {
            try {
                System.out.println("Select deposit type: ");
                System.out.println("1. Cash");
                System.out.println("2. Envelope");
                selection = sc.nextInt();

                switch (selection) {
                    case 1:
                        System.out.println("Enter amount: ");
                        amount = sc.nextInt();
                        if (amount > 0) {
                            return amount;
                        } else {
                            System.out.println("Invalid amount.");
                        }
                        break;
                    case 2:
                        System.out.println("Enter amount in the envelope: ");
                        amount = sc.nextInt();
                        if (amount > 0) {
                            clients[account.id].isEvelopeDep = true;
                        } else {
                            System.out.println("Invalid amount.");
                        }
                        break;
                    default:
                        System.out.println("Invalid selection.");
                }
                break;
            } catch (Exception ex) {
                System.out.println("Invalid input. Re-enter");
                sc.nextLine();
            }
        }
        return 0;
    }

    public static int transferInput(Account account) {
        int destAccountID;
        int amt = 0;
        destAccountID = 0;
        while (true) {
            System.out.println("Enter destination account id from account " + account.id + " (Input own account id or -1 if no transfer) ");
            try {
                destAccountID = sc.nextInt();
                if (destAccountID < accounts.length) {
                    if (destAccountID == -1 || destAccountID == account.id) {
                    } else {
                        System.out.println("Enter transfer amount: ");
                        amt = sc.nextInt();
                        clients[account.id].srcAccount = accounts[account.id];
                        clients[account.id].destAccount = accounts[destAccountID];
                        return amt;
                    }
                    break;
                } else {
                    System.out.println("Invalid destination account ID.");
                }
            } catch (Exception ex) {
                System.out.println("Invalid input.");
                sc.nextLine();
            }
        }
        return 0;
    }

    public static void generateReceipt() {
        System.out.println(receiptList.size());
        System.out.println("====================");
        System.out.println("Receipt for all transaction");
        System.out.println("====================");
        for (Receipt r : receiptList) {
            System.out.println("Receipt no: " + (receiptList.indexOf(r) + 1));
            System.out.printf("%s, Location: %s, Type: %s, Account ID: %d, Transaction amount: %s, Account balance: %d", r.dateTime, r.machineLocation, r.transactionType, r.accountID, r.transAmount, r.bal);
            System.out.println("");
        }
    }
}
