/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atmattempt;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author kelok
 */
public class ATM {

    public static Scanner sc = new Scanner(System.in);

    public static Account[] accounts;
    public static Client[] clients;
    public static ArrayList<Receipt> receiptList = new ArrayList<>();
    public static final String LOCATION = "Bukit Jalil";

    public static void main(String[] args) {
        // TODO code application logic here
        int count;
        System.out.println("~~~~~~~~~~~~~~~");
        System.out.println("ATM Simulation");
        System.out.println("~~~~~~~~~~~~~~~");
        System.out.println("How many accounts? : ");
        count = sc.nextInt();

        if (count <= 1) {
            System.out.println("Please input more than 1 account in order to demonstrate multithreading properly.");
            System.exit(0);
        }

        accounts = new Account[count];
        clients = new Client[count];

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
            client.start();                         //start all the Threads
        }

        try {
            for (Client client : clients) {
                client.join();                      //wait for all the thread to finish
            }

            for (Client client : clients) {
                receiptList.addAll(client.getReceipts());   //retrieve client receipts and add to main receiptList
            }

            System.out.println("====================");
            System.out.println("Transaction all done");
            System.out.println("====================");

            for (int i = 0; i < accounts.length; i++) {
                System.out.println("Account ID : " + i + " current balance: RM" + accounts[i].getBal());
            }
        } catch (Exception ex) {

        }
        generateReceipt();                          //generate receipt in the end of program
    }

    public static void initilizeAccount(int count) {
        System.out.println("====================");
        System.out.println("Accounts ");
        System.out.println("====================");
        for (int i = 0; i < count; i++) {
            System.out.println("Enter initial balance for account " + i + ":");
            int bal = sc.nextInt();
            int randomPIN = (int) (Math.random() * 9000) + 1000;
            Account account = new Account(i, bal, randomPIN);
            accounts[i] = account;
            clients[i].srcAccount = account;    //initialize each client with account 
        }

        System.out.println(count + " accounts created.");

        for (Account acc : accounts) {
            acc.showDetails();
        }
    }

    public static void showMenu(Account account) {
        String input;
        do {
            System.out.println("====================");
            System.out.println("Menu for Account " + account.getId());
            System.out.println("====================");
            System.out.println("1. Balance enquiry");
            System.out.println("2. Change PIN");
            System.out.println("3. Withdrawal");
            System.out.println("4. Deposit");
            System.out.println("5. Transfer");
            System.out.println("Input choice 1-5");
            input = sc.next();

            switch (input) {
                case "1":
                    clients[account.getId()].isReadBalance = true;
                    break;
                case "2":
                    changePIN(account);
                    break;
                case "3":
                    int withAmt = withdrawalInput(account);
                    clients[account.getId()].withdrawalAmt = withAmt;
                    break;
                case "4":
                    int depAmt = depositInput(account);
                    clients[account.getId()].depositAmt = depAmt;
                    break;
                case "5":
                    int transAmt = transferInput(account);
                    clients[account.getId()].transferAmt = transAmt;
                    break;
                default:
                    System.out.println("Invalid selection");
            }
            System.out.println("Continue? (y/n): ");
            input = sc.next();
        } while (input.equalsIgnoreCase("y"));
    }

    //set scenarios for each clients
    public static void showScenario(Account account) {

        int attempt = 0;
        int cancelTransaction = 0;

        System.out.println("");
        System.out.println("====================");
        System.out.println("Scenario for Account " + account.getId());
        System.out.println("====================");
        while (true) {
            try {
                System.out.print("No. of PIN attempt (>3 will freeze account): ");
                attempt = sc.nextInt();
                clients[account.getId()].pinAttempt = attempt;
                if (attempt <= 3) {
                    System.out.print("Cancel mid-way transaction? (1 for yes 0 for no): ");
                    cancelTransaction = sc.nextInt();
                    if (cancelTransaction == 1) {
                        clients[account.getId()].hasFailedTrans = true;
                    }
                }
                break;
            } catch (InputMismatchException ex) {
                System.out.println("Invalid input. Retry");
                sc.nextLine();
            }
        }
    }

    //change PIN, can be performed without concurrency
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
        account.setPin(newPIN);
        System.out.println("New PIN is " + account.getPin());
    }

    //initialize withdrawal
    public static int withdrawalInput(Account account) {
        int selection, amount = 0;
        while (true) {
            System.out.println("You may withdraw only in multiples of RM20. Select amount: ");
            System.out.println("1. RM20     2. RM40");
            System.out.println("3. RM60     4. RM80");
            System.out.println("5. RM100    6. Other amount");
            try {
                selection = sc.nextInt();
                switch (selection) {
                    case 1:
                        amount = 20;
                        break;
                    case 2:
                        amount = 40;
                        break;
                    case 3:
                        amount = 60;
                        break;
                    case 4:
                        amount = 80;
                        break;
                    case 5:
                        amount = 100;
                        break;
                    case 6:
                        System.out.println("Enter amount");
                        while (true) {
                            try {
                                amount = sc.nextInt();
                                if (amount % 20 == 0) {
                                    break;
                                } else {
                                    System.out.println("Invalid amount.");
                                }
                            } catch (InputMismatchException ex) {
                                System.out.println("Invalid amount. Re-enter amount");
                                sc.nextLine();
                            }
                        }
                        break;
                    default:
                }
                if (amount <= account.getBal()) {
                    break;
                } else {
                    System.out.println("Insufficient fund! Re-enter");
                }

            } catch (InputMismatchException ex) {
                System.out.println("Invalid input. Re-enter selection");
                sc.nextLine();
            }
        }
        return amount;
    }

    //initialize deposit
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
                            clients[account.getId()].isEvelopeDep = true;
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

    //initialize transfer
    public static int transferInput(Account account) {
        int destAccountID;
        int amt = 0;
        destAccountID = 0;
        while (true) {
            System.out.println("Enter destination account id from account " + account.getId() + " (Input own account id or -1 if no transfer) ");
            try {
                destAccountID = sc.nextInt();
                if (destAccountID < accounts.length) {
                    if (destAccountID == -1 || destAccountID == account.getId()) {          //-1 or self transfer
                    } else {
                        System.out.println("Enter transfer amount: ");
                        amt = sc.nextInt();
                        clients[account.getId()].srcAccount = accounts[account.getId()];
                        clients[account.getId()].destAccount = accounts[destAccountID];
                        return amt;
                    }
                    break;
                } else {
                    System.out.println("Destination account ID not found.");
                }
            } catch (Exception ex) {
                System.out.println("Invalid input.");
                sc.nextLine();
            }
        }
        return 0;
    }

    public static void generateReceipt() {
        System.out.println("====================");
        System.out.println("Receipts for all transaction");
        System.out.println("====================");
        for (Receipt r : receiptList) {
            System.out.println("Receipt no: " + (receiptList.indexOf(r) + 1));
            System.out.printf("%s, Location: %s, Type: %s, Account ID: %d, Transaction amount: %s, Account balance: RM%d", r.dateTime, r.machineLocation, r.transactionType, r.accountID, r.transAmount, r.bal);
            System.out.println("");
        }
    }
}
