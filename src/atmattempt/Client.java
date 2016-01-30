/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atmattempt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author kelok
 */
public class Client extends Thread {               //every client handles account

    Account srcAccount;
    Account destAccount;
    int depositAmt;
    int withdrawalAmt;
    int transferAmt;
    int pinAttempt;
    boolean isEvelopeDep;
    boolean isReadBalance;
    boolean hasFailedTrans;

    public Client() {
        depositAmt = 0;
        withdrawalAmt = 0;
        transferAmt = 0;
        pinAttempt = 0;
        isEvelopeDep = false;
        isReadBalance = false;
        hasFailedTrans = false;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {

        if (pinAttempt <= 3) {
            if (!hasFailedTrans) {
                if (isReadBalance) {
                    while (srcAccount.getBal() == 0);
                }
                if (isEvelopeDep) {
                    while (srcAccount.depositEnvelope() == 0);
                }
                if (withdrawalAmt != 0) {
                    while (srcAccount.withdraw(withdrawalAmt) == 0);
                }
                if (depositAmt != 0) {
                    while (srcAccount.deposit(depositAmt) == 0);
                }
                if (destAccount != null) {
                    while (srcAccount.transfer(transferAmt, destAccount) == 0);
                }
            } else {
                System.out.println("Account " + srcAccount.id + " cancelled action. No changes were made into the account.");
            }
        } else {
            System.out.println("Account " + srcAccount.id + " has too many failed PIN attempt! Card is permanently retained.");
        }
    }
}
