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

public class PrivateKey implements Serializable {
    // private components of the private key
    private LargeInteger n;
    private LargeInteger d;

    // create priv key
    public PrivateKey(LargeInteger n, LargeInteger d) throws IllegalArgumentException {
        if (n == null || d == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }

        this.n = n;
        this.d = d;
    }

    // create priv key
    public PrivateKey(String filename) throws IllegalArgumentException {
        try {
            // open the filename as an input stream
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream reader = new ObjectInputStream(file);

            // read d and n from the file
            LargeInteger n = (LargeInteger) reader.readObject();
            LargeInteger d = (LargeInteger) reader.readObject();
            reader.close();

            this.n = n;
            this.d = d;
        } catch (Exception e) {
            e.printStackTrace(); //some error checking
            throw new IllegalArgumentException("NOT FOUND: Have you generated a private key already?");
        }
    }

    // return n
    public LargeInteger n() {
        return this.n.makeCopy();
    }

    // return d
    public LargeInteger d() {
        return this.d.makeCopy();
    }

    // write to file
    public void write(String filename) {
        try {
            // open the filename as an output stream
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream writer = new ObjectOutputStream(file);
            writer.writeObject(this.n());
            writer.writeObject(this.d());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace(); //error checking
            System.exit(1);
        }
    }

    // write to file
    public void write() {
        // call write with the defatult filename
        this.write("privkey.rsa");
    }

    // sign
    public void sign(byte[] digest, String filename) {
        try {
            // convert the byte array to a LargeInteger
            LargeInteger hash = new LargeInteger(LargeInteger.extendArr(digest));
            // "decrypt" hash with private key
            LargeInteger sig = hash.modularExp(this.d(), this.n());

            filename += ".sig";
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream writer = new ObjectOutputStream(file);
            writer.writeObject(sig);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace(); // error checking
            System.exit(1);
        }
    }

    public String toString() {
        return "PrivateKey:" + "\nn = " + this.n() + "\nd = " + this.d();
    }
}
