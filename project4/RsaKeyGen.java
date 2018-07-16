/**********************************************************************
*   Author:         Michael Adams
*   Last Edit:      12/10/17
*
***********************************************************************/

public class RsaKeyGen {
    public static void main(String[] args) {
        RsaKey rkey = RsaKey.generate(512); // create a new 512-bit RSA Key
        //rkey.write(); // write the RSA Key to file
        System.out.println("Public key succesfully created and written to file");
        System.out.println("Private key succesfully created and written to file");
    }
}
