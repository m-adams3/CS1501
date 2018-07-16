/**********************************************************************
*   Author:         Michael Adams
*   Last Edit:      12/10/17
*
***********************************************************************/

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.security.MessageDigest;

public class RsaSign {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("include operation (s or v) and filename");
            System.exit(1);
        }
        String operation = args[0];
        String filename = args[1];

        // make sure s or v is used
        if (!operation.equals("v") && !operation.equals("s")) {
            System.err.println("include operation (s or v) and filename");
            System.exit(1);
        }

        if (operation.equals("s")) {
            sign(filename); // hash and sign the filename
        } else {
            verify(filename); //verify hash of a signed file
        }
    }

    public static void sign(String filename) {
        try {
            Path path = Paths.get(filename); //read in the file to hash
            byte[] data = Files.readAllBytes(path);

            MessageDigest md = MessageDigest.getInstance("SHA-256"); //create class instance to create SHA-256 hash

            md.update(data); //process the file
            byte[] digest = md.digest(); //generate a has of the file

            PrivateKey privKey = new PrivateKey("privkey.rsa");  //read the private key
            privKey.sign(digest, filename); //use the private key to sign the hash of the file
            System.out.println("succesfully signed");
        } catch (Exception e) {
            // print the stack trace for exceptions
            e.printStackTrace();
        }
    }

    public static void verify(String filename) {
        try {
            Path path = Paths.get(filename);  //read in the file to hash
            byte[] data = Files.readAllBytes(path);

            
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // create class instance to create SHA-256 hash
            md.update(data); // process the file
            byte[] digest = md.digest(); // generate a has of the file

            PublicKey pubKey = new PublicKey("pubkey.rsa");  // read the private key

            File sigFile = new File(filename + ".sig"); // ensure signature exists

            if (!sigFile.exists()) {
                System.err.println("file DNE " + sigFile.getName());
                return;
            }

            boolean verified = pubKey.verify(digest, filename); // use the private key to sign the hash of the file

            if (verified) {
                System.out.println("The file was succesfully verified!");
            } else {
                System.out.println("The file was uunable to be verified!");
            }
        } catch (Exception e) {
            e.printStackTrace(); // print the stack trace for exceptions
        }
    }
}
