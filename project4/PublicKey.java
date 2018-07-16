/**********************************************************************
*   Author:         Michael Adams
*   Last Edit:      12/10/17
*
***********************************************************************/

import java.lang.IllegalArgumentException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.MessageDigest;

public class PublicKey implements Serializable {
    // private components of the public key
    private LargeInteger n;
    private LargeInteger e;

    // make pub key
    public PublicKey(LargeInteger n, LargeInteger e) throws IllegalArgumentException {
        if (n == null || e == null) {
            throw new IllegalArgumentException("neither argument can be null");
        }

        this.n = n;
        this.e = e;
    }

    // make pub key
    public PublicKey(String filename) throws IllegalArgumentException {
        try {
            // open the filename as an input stream
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream reader = new ObjectInputStream(file);
            LargeInteger n = (LargeInteger) reader.readObject();
            LargeInteger e = (LargeInteger) reader.readObject();
            reader.close();

            this.n = n;
            this.e = e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("NOT FOUND: Have you generated a public key already?");
        }
    }

    public LargeInteger n() {
        return this.n.makeCopy();
    }

    public LargeInteger e() {
        return this.e.makeCopy();
    }

    public void write(String filename) {
        try {
            // open the file as an output stream
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream writer = new ObjectOutputStream(file);
            writer.writeObject(this.n());
            writer.writeObject(this.e());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write() {
        this.write("pubkey.rsa");
    }

    public boolean verify(byte[] data, String filename) {
        String signatureFilename = filename + ".sig";

        try {
            // read the signature
            FileInputStream sigFile = new FileInputStream(signatureFilename);
            ObjectInputStream sigReader = new ObjectInputStream(sigFile);
            LargeInteger sigFileData = (LargeInteger) sigReader.readObject();
            sigReader.close();

            // convert original data to LargeInteger
            LargeInteger originalHash = new LargeInteger(LargeInteger.extendArr(data));

            // "encrypt" the signature data with public key
            LargeInteger encrypted = sigFileData.modularExp(this.e(), this.n());
            originalHash = originalHash.trimLeadZeros();
            encrypted = encrypted.trimLeadZeros();

            return originalHash.equals(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return false;
    }

    public String toString() {
        return "PublicKey:" + "\nn = " + this.n() + "\ne = " + this.e();
    }
}
