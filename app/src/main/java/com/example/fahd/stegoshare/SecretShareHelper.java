package com.example.fahd.stegoshare;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by nathanmorgenstern on 12/17/17.
 */

public class SecretShareHelper {
    private String     hash;
    private BigInteger prime;
    private BigInteger share;
    private int        shareNumber;
    private int        totalShares;
    private int        requiredShares;


    public SecretShareHelper(String longString){
        String[] data = longString.split(",");
        System.out.println(Arrays.toString(data));

        this.hash           =  data[0];
        this.prime          =  new BigInteger(data[1]);
        this.share          =  new BigInteger(data[2]);
        this.shareNumber    =  Integer.parseInt(data[3]);
        this.totalShares    =  Integer.parseInt(data[4]);
        this.requiredShares =  Integer.parseInt(data[5]);
    }


    public int getRequiredShares() {
        return requiredShares;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }

    public int getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(int shareNumber) {
        this.shareNumber = shareNumber;
    }

    public BigInteger getShare() {
        return share;
    }

    public void setShare(BigInteger share) {
        this.share = share;
    }

    public BigInteger getPrime() {
        return prime;
    }

    public void setPrime(BigInteger prime) {
        this.prime = prime;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void printAll(){
        System.out.println("hash: " + hash);
        System.out.println("prime: " + prime.toString());
        System.out.println("share: " + share.toString());
        System.out.println("share number: " + shareNumber);
        System.out.println("n: " + totalShares);
        System.out.println("m: " + requiredShares);
    }
}
