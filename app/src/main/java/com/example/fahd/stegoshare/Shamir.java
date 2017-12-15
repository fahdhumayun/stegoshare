// By Nathan Morgenstern

package com.example.fahd.stegoshare;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public final class Shamir
{
    public static SecretShare[] split(final BigInteger secret, int m, int n, BigInteger prime, Random random)
    {
        System.out.println("Prime Number: " + prime);

        final BigInteger[] coeff = new BigInteger[m];
        coeff[0] = secret;
        for (int i = 1; i < m; i++)
        {
            BigInteger r;
            while (true)
            {
                r = new BigInteger(prime.bitLength(), random);
                if (r.compareTo(BigInteger.ZERO) > 0 && r.compareTo(prime) < 0)
                {
                    break;
                }
            }
            coeff[i] = r;
        }

        final SecretShare[] shares = new SecretShare[n];
        for (int x = 1; x <= n; x++)
        {
            BigInteger accum = secret;

            for (int exp = 1; exp < m; exp++)
            {
                accum = accum.add(coeff[exp].multiply(BigInteger.valueOf(x).pow(exp).mod(prime))).mod(prime);
            }
            shares[x - 1] = new SecretShare(x, accum);
            System.out.println("Share " + shares[x - 1]);
        }

        return shares;
    }

    public static BigInteger combine(final SecretShare[] shares, final BigInteger prime)
    {
        BigInteger accum = BigInteger.ZERO;

        for(int formula = 0; formula < shares.length; formula++)
        {
            BigInteger numerator   = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for(int count = 0; count < shares.length; count++)
            {
                if(formula == count)
                    continue; // If not the same value

                int startposition = shares[formula].getNumber();
                int nextposition = shares[count].getNumber();

                numerator = numerator.multiply(BigInteger.valueOf(nextposition).negate()).mod(prime); // (numerator * -nextposition) % prime;
                denominator = denominator.multiply(BigInteger.valueOf(startposition - nextposition)).mod(prime); // (denominator * (startposition - nextposition)) % prime;
            }
            BigInteger value = shares[formula].getShare();
            BigInteger tmp = value.multiply(numerator) . multiply(modInverse(denominator, prime));
            accum = prime.add(accum).add(tmp) . mod(prime); //  (prime + accum + (value * numerator * modInverse(denominator))) % prime;
        }

        System.out.println("The secret is: " + accum);
        System.out.println("The secret is: " + accum.toByteArray());

        //String.format("%02x", byteValue);
        String recon = new String( accum.toByteArray());
        System.out.println("reconstructed: \n" + recon);
        //System.out.println("hex: " + String.format("%02x", accum.toByteArray()));
        System.out.println("---------------------------------------------\n");

        return accum;
    }

    private static BigInteger[] gcdD(BigInteger a, BigInteger b)
    {
        if (b.compareTo(BigInteger.ZERO) == 0)
            return new BigInteger[] {a, BigInteger.ONE, BigInteger.ZERO};
        else
        {
            BigInteger n = a.divide(b);
            BigInteger c = a.mod(b);
            BigInteger[] r = gcdD(b, c);
            return new BigInteger[] {r[0], r[2], r[1].subtract(r[2].multiply(n))};
        }
    }

    private static BigInteger modInverse(BigInteger k, BigInteger prime)
    {
        k = k.mod(prime);
        BigInteger r = (k.compareTo(BigInteger.ZERO) == -1) ? (gcdD(prime, k.negate())[2]).negate() : gcdD(prime,k)[2];
        return prime.add(r).mod(prime);
    }

    public static String stringBuilder(byte[] byteArr){
        String finalString = "";

        for(int i = 0; i < byteArr.length; i++)
            finalString += String.format("%02x", byteArr[i]);

        return finalString;
    }

    public static void printSecretShares(final SecretShare[] ss){

        for(int i = 0; i < ss.length; i++){
            //System.out.println("BigInteger.toByteArray: " + ss[i].getShare().toByteArray());
            System.out.println("StringBuilder: " + stringBuilder(ss[i].getShare().toByteArray()));
            //String recon = new String( ss[i].getShare().toByteArray());
            //System.out.println("reconstructed: " + recon);
        }
    }

    /*public static void main(final String[] args)
    {
        final int CERTAINTY = 256;
        final SecureRandom random = new SecureRandom();
        String coolString = "1. Okay 2. cool 3. fire 4. cold 5. tree 6. book";
        byte[] byteArray = coolString.getBytes();
        String reconstitutedString = new String(byteArray);
        System.out.println("byteArray: " + byteArray);
        System.out.println("reconstructed: " + reconstitutedString);
        System.out.println("---------------------------");
        final BigInteger secret = new BigInteger(1,byteArray);
        System.out.println("BigInteger: " + secret);
        System.out.println("BigInteger.toByteArray: " + secret.toByteArray());
        String recon = new String(byteArray);
        System.out.println("reconstructed: " + recon + "\n");
        // prime number must be longer then secret number
        final BigInteger prime = new BigInteger(secret.bitLength() + 1, CERTAINTY, random);
        // 2 - at least 2 secret parts are needed to view secret
        // 5 - there are 5 persons that get secret parts
        final SecretShare[] shares = Shamir.split(secret, 3, 5, prime, random);
        printSecretShares(shares);
        // we can use any combination of 2 or more parts of secret
        SecretShare[] sharesToViewSecret = new SecretShare[] {shares[4],shares[1], shares[2]}; // 4 & 1 & 2
        BigInteger result = Shamir.combine(sharesToViewSecret, prime);
        sharesToViewSecret = new SecretShare[] {shares[1],shares[4],shares[0]}; // 1 & 4 & 0
        result = Shamir.combine(sharesToViewSecret, prime);
        sharesToViewSecret = new SecretShare[] {shares[0],shares[1],shares[3]}; // 0 & 1 & 3
        result = Shamir.combine(sharesToViewSecret, prime);
    }*/
}


//reference: https://stackoverflow.com/questions/19327651/java-implementation-of-shamirs-secret-sharing#23470662
