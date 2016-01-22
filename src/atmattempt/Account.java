/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atmattempt;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kelok
 */
public class Account {

    int id;
    int bal;
    int pin;
    Lock lock = new ReentrantLock();
    Scanner sc = new Scanner(System.in);

    public Account(int id, int bal, int pin) {
        this.id = id;
        this.bal = bal;
        this.pin = pin;
    }

    int getBal() {
        if (lock.tryLock()) {
            try {
                System.out.println("Loading balance");
                Thread.sleep(1000);
                System.out.println(id + " current balance: RM" + bal);
                lock.unlock();
                return 1;
            } catch (Exception ex) {
                return 0;
            }
        } else {
            System.out.println("Account is busy. Unable to read balance");
            return 0;
        }
    }

    int withdraw(int amt) {
        if (amt != 0) {
            if (lock.tryLock()) {
                if (amt <= bal) {
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
                    System.out.println(id + " has insufficient fund");
                    lock.unlock();
                    return 1;
                }
            } else {
                return 0;
            }
        } else {
            return 1;
        }
    }

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

    boolean enterPIN() {
        int input;
        int counter = 0;
        System.out.println("Enter PIN: ");
        while (counter < 3) {
            try {
                input = sc.nextInt();
                if (input != pin) {
                    System.out.println("Welcome to APU ATM");
                    System.out.println("==========");
                    return true;
                } else {
                    System.out.println("Wrong PIN. Please retry. Failed attempt(s): " + (++counter));
                }
            } catch (InputMismatchException e) {
                counter++;
                System.out.print("Invalid input. Please re-enter: ");
                sc.nextLine();
            }
        }
        if (counter >= 3) {
            System.out.println("Too many failed attempt. Exiting");
        }
        return false;
    }

    void showDetails() {
        System.out.println("ACCOUNT DETAILS: ");
        System.out.println("ID: " + id);
        System.out.println("PIN: " + pin);
        System.out.println("BALANCE: RM" + bal);
        System.out.println("");
    }
}
