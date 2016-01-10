/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atmattempt;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author kelok
 */
public class Account {

    int id;
    int bal;
    Lock lock = new ReentrantLock();

    public Account(int id, int bal) {
        this.id = id;
        this.bal = bal;
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
            } else {
                System.out.println(id + " reimbursing ");
                while (deposit(amt) == 0);
            }
        }
        return 0;
    }
}
