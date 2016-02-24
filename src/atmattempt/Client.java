package atmattempt;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private ArrayList<Receipt> clientReceipt;
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
        Random rand = new Random();
        int networkFailureRate = rand.nextInt(10) + 1;  //random 1-10
        if (networkFailureRate > 8) {               //20% chance
            System.out.println("Client " + srcAccount.getId() + " experienced network failure. Transaction aborted");
            return;
        }
        if (pinAttempt <= 3) {
            if (!hasFailedTrans) {
                if (isReadBalance) {
                    while (srcAccount.balanceInquiry() == 0);
                    generateReceipt("Balance inquiry", "-");
                }
                if (isEvelopeDep) {
                    while (srcAccount.depositEnvelope() == 0);
                    generateReceipt("Envelope deposit", "Pending");
                }
                if (withdrawalAmt != 0) {
                    while (srcAccount.withdraw(withdrawalAmt) == 0);
                    generateReceipt("Withdrawal", String.valueOf(withdrawalAmt));
                }
                if (depositAmt != 0) {
                    while (srcAccount.deposit(depositAmt) == 0);
                    generateReceipt("Deposit", String.valueOf(depositAmt));
                }
                if (destAccount != null) {
                    while (srcAccount.transfer(transferAmt, destAccount) == 0);
                    generateReceipt("Transfer", String.valueOf(transferAmt));
                }
            } else {
                System.out.println("Account " + srcAccount.getId() + " cancelled action. No changes were made into the account.");
            }
        } else {
            System.out.println("Account " + srcAccount.getId() + " has too many failed PIN attempt! Card is permanently retained.");
        }
    }

    public void generateReceipt(String transType, String amt) {
        String timeStamp = new SimpleDateFormat("dd/MM/yy hh:mma").format(Calendar.getInstance().getTime());
        Receipt r = new Receipt(timeStamp, LOCATION, transType, srcAccount.getId(), "RM" + amt, srcAccount.getBal());
        clientReceipt.add(r);
    }

    public ArrayList<Receipt> getReceipts() {
        return clientReceipt;
    }

}
