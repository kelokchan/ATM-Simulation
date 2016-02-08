package atmattempt;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Kelok
 */
public class Receipt {

    String dateTime;
    String machineLocation;
    String transactionType;
    int accountID;
    String transAmount;
    boolean isSuccessful;
    int bal;

    public Receipt(String dateTime, String machineLocation, String transactionType, int accountID, String transAmount, int bal) {
        this.dateTime = dateTime;
        this.machineLocation = machineLocation;
        this.transactionType = transactionType;
        this.accountID = accountID;
        this.transAmount = transAmount;
        this.bal = bal;
        isSuccessful = true;
    }
}
