/**********************************************************************
*   Author:         Michael Adams
*   Last Edit:      12/10/17
*
***********************************************************************/

import java.util.Random;
import java.lang.IllegalArgumentException;

public class RsaKey {
    // public and private keys
    private PublicKey pub;
    private PrivateKey priv;

    //constructor
    public RsaKey(PublicKey pub, PrivateKey priv) {
        // ensure neither key is null
        if (pub == null || priv == null) {
            throw new IllegalArgumentException("neither key can be null");
        }

        this.pub = pub;
        this.priv = priv;
    }

    // generate keys
    public static RsaKey generate(int bitlength) {
        // init a Random to create primes
        Random rnd = new Random();

        // calculate p, q, n
        LargeInteger p = new LargeInteger(bitlength, rnd);
        LargeInteger q = new LargeInteger(bitlength, rnd);
        LargeInteger n = p.multiply(q);

        // calculate phi
        LargeInteger p_1 = p.subtractOne();
        LargeInteger q_1 = q.subtractOne();
        LargeInteger phi = p_1.multiply(q_1);

        // fine e
        LargeInteger one = new LargeInteger(LargeInteger.ONE);
        LargeInteger two = one.add(one);
        LargeInteger e = two.add(one);

        // iterate until gcd(phi(n), e) == 1 and e < phi(n)
        while (!phi.gcd(e).equals(one) && e.lessThan(phi)) {
            e = e.add(two);
        }

        if (!e.lessThan(phi)) {
            // no suitable e found, try again
            return generate(bitlength);
        } else {
            // get the value of d = e^-1 % phi(n)
            LargeInteger d = e.modularInverse(phi);

            if (d.lessThan(new LargeInteger(LargeInteger.ZERO))) {
                d = d.add(phi);
            }

            System.out.println("e: " + e.toString());
            System.out.println("length: " + e.length());
            System.out.println("n: " + n.toString());
            System.out.println("length: " + n.length());
            // create a public key from n and e
            PublicKey pub = new PublicKey(n, e);
            // create a private key from n and d
            PrivateKey priv = new PrivateKey(n, d);
            // set the member keys
            return new RsaKey(pub, priv);
        }
    }

    // return pub key
    public PublicKey publicKey() {
        return this.pub;
    }

    // return priv key
    public PrivateKey privateKey() {
        return this.priv;
    }

    // write to file
    public void write(String pubFilename, String privFilename) {
        // write the public key
        this.publicKey().write(pubFilename);
        // write the private key
        this.privateKey().write(privFilename);
    }

    // write to file
    public void write() {
        // call write with the default filenames
        write("pubkey.rsa", "privkey.rsa");
    }

    // toString stuff
    public String toString() {
        return this.publicKey().toString() + "\n" + this.privateKey().toString();
    }
}
