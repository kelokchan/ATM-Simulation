package atmattempt;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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
    ArrayList<Receipt> clientReceipt;
    public static final String LOCATION = "Bukit Jalil";

    public Client() {
        depositAmt = 0;
        withdrawalAmt = 0;
        transferAmt = 0;
        pinAttempt = 0;
        isEvelopeDep = false;
        isReadBalance = false;
        hasFailedTrans = false;
        clientReceipt = new ArrayList<>();
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {

        String timeStamp = new SimpleDateFormat("dd/MM/yy hh:mma").format(Calendar.getInstance().getTime());
        Receipt r;
        Random rand = new Random();

        if ((rand.nextInt(10) + 1) >= 7) {               //30% chance
            System.out.println("Client " + srcAccount.id + " experienced hardware failure");
            return;
        }

        if (pinAttempt <= 3) {
            if (!hasFailedTrans) {
                if (isReadBalance) {
                    while (srcAccount.getBal() == 0);
                    r = new Receipt(timeStamp, LOCATION, "Balance inquiry", srcAccount.id, "-", srcAccount.bal);
                    clientReceipt.add(r);
                }
                if (isEvelopeDep) {
                    while (srcAccount.depositEnvelope() == 0);
                    r = new Receipt(timeStamp, LOCATION, "Envelop deposit", srcAccount.id, "Pending", srcAccount.bal);
                    clientReceipt.add(r);
                }
                if (withdrawalAmt != 0) {
                    while (srcAccount.withdraw(withdrawalAmt) == 0);
                    r = new Receipt(timeStamp, LOCATION, "Withdrawal", srcAccount.id, "RM" + withdrawalAmt, srcAccount.bal);
                    clientReceipt.add(r);
                }
                if (depositAmt != 0) {
                    while (srcAccount.deposit(depositAmt) == 0);
                    r = new Receipt(timeStamp, LOCATION, "Deposit", srcAccount.id, "RM" + depositAmt, srcAccount.bal);
                    clientReceipt.add(r);
                }
                if (destAccount != null) {
                    while (srcAccount.transfer(transferAmt, destAccount) == 0);
                    r = new Receipt(timeStamp, LOCATION, "Transfer", srcAccount.id, "RM" + transferAmt, srcAccount.bal);
                    clientReceipt.add(r);
                }
            } else {
                System.out.println("Account " + srcAccount.id + " cancelled action. No changes were made into the account.");
            }
        } else {
            System.out.println("Account " + srcAccount.id + " has too many failed PIN attempt! Card is permanently retained.");
        }
    }

    public ArrayList<Receipt> getReceipts() {
        return clientReceipt;
    }

}
