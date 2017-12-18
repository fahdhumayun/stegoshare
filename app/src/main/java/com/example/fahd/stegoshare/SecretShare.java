// By Nathan Morgenstern
// SecretShare (Class) - Used as a helper class to the Shamir (i.e. Shamir secret sharing algorithm)

package com.example.fahd.stegoshare;

import java.math.BigInteger;

public class SecretShare {

    private final int number;
    private final BigInteger share;

    public SecretShare(final int number, final BigInteger share) {
        this.number = number;
        this.share  = share;
    }

    public SecretShare(String s){

        //This will separate the last digit which represents the share number
        char lastDigit  = s.charAt(s.length() - 1);
        String share    = s.substring(0,s.length() - 1);

        final BigInteger bg_share = new BigInteger(share);


        this.number =   Integer.parseInt(Character.toString(lastDigit));
        this.share  = bg_share;

    }


    public int getNumber()
    {
        return number;
    }

    public BigInteger getShare()
    {
        return share;
    }

    @Override
    public String toString()
    {
        return "SecretShare [num=" + number + ", share=" + share + "]";
    }

}

//reference: https://stackoverflow.com/questions/19327651/java-implementation-of-shamirs-secret-sharing#23470662
