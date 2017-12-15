// By Nathan Morgenstern

package com.example.fahd.stegoshare;

import java.math.BigInteger;

public class SecretShare
{

    private final int number;
    private final BigInteger share;

    public SecretShare(final int number, final BigInteger share)
    {
        this.number = number;
        this.share = share;
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
