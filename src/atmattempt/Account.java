package atmattempt;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author kelok
 */

public class Account {

    private int id;
    private int bal;
    private int pin;
    Lock lock = new ReentrantLock();
    Scanner sc = new Scanner(System.in);

    public Account(int id, int bal, int pin) {
        this.id = id;
        this.bal = bal;
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public int getBal() {
        return bal;
    }

    public int getPin() {
        return pin;
    }
    
    public void setPin(int newPIN){
        pin = newPIN;
    }

    //get balance of the account. If lock not acquired, show error message and return 0
    int balanceInquiry() {
        if (lock.tryLock()) {
            try {
                System.out.println(id + " reading balance");
                Thread.sleep(1000);
                System.out.println(id + " current balance: RM" + bal);
                lock.unlock();
                return 1;
            } catch (Exception ex) {
                return 0;
            }
        } else {
            System.out.println("Client is busy. Unable to read balance");
            return 0;
        }
    }

    //if withdraw amt not 0, try to acquire lock and reduce balance, else return 0
    int withdraw(int amt) {
        if (amt != 0) {
            if (lock.tryLock()) {
                try {
                    System.out.println(id + " is pending bank approval for withdrawal");
                    Thread.sleep(1000);
                    System.out.println(id + " withdrew RM" + amt + " from the account.");
                    bal -= amt;
                    lock.unlock();
                    return 1;
                } catch (Exception ex) {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return 1;
        }
    }

    //if lock acquired, print operation message, balance will not be updated due to pending verification
    int depositEnvelope() {
        if (lock.tryLock()) {
            try {
                System.out.println(id + " is reading envelope in the dispenser...");
                Thread.sleep(1000);
                System.out.println("Envelope is read");
                System.out.println("Verification from bank required. This process may take a few days.");
                lock.unlock();
                return 1;
            } catch (InterruptedException ex) {
                return 0;
            }
        }
        return 0;
    }

    //if amt not 0, try to acquire lock and increment the balance, else return 0
    int deposit(int amt) {
        if (amt != 0) {
            if (lock.tryLock()) {
                try {
                    System.out.println(id + " is pending bank approval for deposit");
                    Thread.sleep(1000);
                    System.out.println(id + " deposited RM" + amt + " into the account.");
                    bal += amt;
                    lock.unlock();
                    return 1;
                } catch (InterruptedException ex) {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return 1;
        }
    }

    //withdraw from source and depost to recipient, if both are successful, print message, else retry deposit
    int transfer(int amt, Account recipient) {
        if (withdraw(amt) == 1) {
            if (recipient.deposit(amt) == 1) {
                try {
                    Thread.sleep(1000);
                    System.out.println(id + " successfully transferred RM" + amt + " to account id " + recipient.id);
                    return 1;
                } catch (InterruptedException ex) {
                    return 0;
                }
            } else {
                System.out.println("Client is busy. " + id + " reimbursing ");
                while (deposit(amt) == 0);
            }
        }
        return 0;
    }

//    boolean enterPIN() {
//        int input;
//        int counter = 0;
//        System.out.println("Enter PIN: ");
//        while (counter < 3) {
//            try {
//                input = sc.nextInt();
//                if (input != pin) {
//                    System.out.println("====================");
//                    System.out.println("Welcome to APU ATM");
//                    System.out.println("====================");
//                    return true;
//                } else {
//                    System.out.println("Wrong PIN. Please retry. Failed attempt(s): " + (++counter));
//                }
//            } catch (InputMismatchException e) {
//                System.out.print("Invalid input. Please re-enter: ");
//                sc.nextLine();
//            }
//        }
//        if (counter >= 3) {
//            System.out.println("Too many failed attempts!!!");
//        }
//        return false;
//    }

    //prints the account details after account initialization
    void showDetails() {
        System.out.println("ACCOUNT DETAILS: ");
        System.out.println("ID: " + id);
        System.out.println("PIN: " + pin);
        System.out.println("BALANCE: RM" + bal);
        System.out.println("");
    }
}
