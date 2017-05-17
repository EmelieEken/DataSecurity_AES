 
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.*;
import java.nio.file.StandardOpenOption;
import java.util.*; //For list


public class AESInterface {

    public static void main(String[] args) {

        if (args.length < 1) { //If filename not provided
            System.out.println("Please include filename");
            System.exit(0);
        }
        final String filename = args[0]; //Filename provided to program from commandline
        Charset utf8 = StandardCharsets.UTF_8;

        try //Read input file to get settings
        {
            if (!Files.exists(Paths.get(filename))) //If file doesn't already exist
            {
                System.out.println("File doesn't exist");
                System.exit(0);
            }
             List<String> fileLines = new ArrayList<String>();
             fileLines = Files.readAllLines(Paths.get(filename),utf8); //Read file and save in list

             if (fileLines.size() != 6) { //If not 6 lines in inputfile
                 System.out.println("File is in the wrong format, 6 lines required");
                 System.exit(0);
             }

             //Get settings from saved filetext
             int encryptionOrDecryption = Integer.parseInt(fileLines.get(0)); //0 for encr, 1 for decr

             int modeOfOperation = Integer.parseInt(fileLines.get(1)); //0 = ECB, 1 = CFB, 2 = CBC, 3 = OFB

             int transmissionSize = Integer.parseInt(fileLines.get(2)); //Should be checked?

             String textString = fileLines.get(3); //Plaintext or ciphertext

             String keyString = fileLines.get(4); //Key

             String initVectorString = fileLines.get(5); //Initialisation vector

             //Convert to arrays of ints
             int[] key = stringToHex(keyString);
             int[] text = stringToHex(textString);
             int[] initVector = stringToHex(initVectorString);
             


             if (encryptionOrDecryption == 0) {
                 Encryption encr = new Encryption(modeOfOperation, transmissionSize, initVector);
                 System.out.println(encr.Encrypt(text, key)); //Out-comment to decrypt before printing
                 //String cipherTextStr = encr.Encrypt(text, key); //Un-comment to decrypt before printing
                 //int[] cipherText = stringToHex(cipherTextStr); //Un-comment to decrypt before printing
                 //System.out.println(cipherText);
                 //Decryption decr = new Decryption(modeOfOperation, transmissionSize, initVector); //Un-comment to decrypt before printing
                 //System.out.println(decr.Decrypt(cipherText, key)); //Un-comment to decrypt before printing
             } else if (encryptionOrDecryption == 1) {
                 Decryption decr = new Decryption(modeOfOperation, transmissionSize, initVector);
                 System.out.println(decr.Decrypt(text, key));
             } else {
                 System.out.println("First line must be a 0 for encryption or 1 for decryption");
                 System.exit(0);
             }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Convert String of hexadecimals to array of ints
    public static int[] stringToHex(String str) {
        int[] strAsHex;
        if (str.length() == 1) {
            strAsHex = new int[1];
            strAsHex[0] = 0;
        } else {
            List<Integer> strAsHexList = new ArrayList<Integer>();
            int i = 0;
            while (i < str.length()-1) { //-1 to handle case where just one number
                String number = String.valueOf(str.charAt(i)) + String.valueOf(str.charAt(i+1));//+str.charAt(0+1);
                //System.out.print(Integer.parseInt(number, 16)); //Integer.parseInt(number, 16)
                strAsHexList.add(Integer.parseInt(number, 16));
                i = i+3;
            }
            strAsHex = strAsHexList.stream().mapToInt(h->h).toArray(); //Convert Integer to int
        }
        return strAsHex;
    }
}