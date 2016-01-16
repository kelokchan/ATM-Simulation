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

    void getBal() {
        System.out.println("Balance: RM" + bal);
    }

    int withdraw(int amt) {
        if (lock.tryLock()) {
            System.out.println(id + " withdrew RM" + amt + " from the account.");
            bal -= amt;
            lock.unlock();
            return 1;
        } else {
            return 0;
        }
    }

    int deposit(int amt) {
        if (lock.tryLock()) {
            bal += amt;
            lock.unlock();
            return 1;
        } else {
            return 0;
        }
    }

    int transfer(int amt, Account recipient) {
        if (withdraw(amt) == 1) {
            if (recipient.deposit(amt) == 1) {
                if (recipient.id != id) {
                    System.out.println(id + " successfully transferred RM" + amt + " to account id " + recipient.id);
                }
                return 1;
            }
        } else {
            System.out.println(id + " reimbursing ");
            while (deposit(amt) == 0);
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
                if (input == pin) {
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
