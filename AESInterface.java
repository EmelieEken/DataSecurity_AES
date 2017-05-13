 
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.*;
import java.nio.file.StandardOpenOption;
import java.util.*; //For list

public class AESInterface {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please include filename");
            System.exit(0);
        }
        final String filename = args[0];
        Charset utf8 = StandardCharsets.UTF_8;
        //System.out.println(filename);

        try //Read input file to get settings
        {
            if (!Files.exists(Paths.get(filename))) //If file doesn't already exist
            {
                System.out.println("File doesn't exist");
                System.exit(0);
            }
             List<String> fileLines = new ArrayList<String>();
             fileLines = Files.readAllLines(Paths.get(filename),utf8); //Read file and save in list
             //System.out.println(fileLines.get(4));

             if (fileLines.size() != 6) { //If not 6 lines in inputfile
                 System.out.println("File is in the wrong format, 6 lines required");
                 System.exit(0);
             }

             //Get settings
             int encryptionOrDecryption = Integer.parseInt(fileLines.get(0)); //0 for encr, 1 for decr
             //System.out.println(encryptionOrDecryption);

             int modeOfOperation = Integer.parseInt(fileLines.get(1)); //0 = ECB, 1 = CFB, 2 = CBC, 3 = OFB

             int transmissionSize = Integer.parseInt(fileLines.get(2)); //Should be checked

             String textString = fileLines.get(3); //Plaintext or ciphertext
             //System.out.println(text);

             String keyString = fileLines.get(4); //Key

             String initVectorString = fileLines.get(5); //Initialisation vector

             //Convert to array of Integers
             Integer[] key = stringToHex(keyString);
             Integer[] text = stringToHex(textString);
             Integer[] initVector = stringToHex(initVectorString);
             


             if (encryptionOrDecryption == 1) {
                 Encryption encr = new Encryption(modeOfOperation, transmissionSize, initVector);
                 System.out.println(encr.Encrypt(text, key));
             } else if (encryptionOrDecryption == 2) {
                 //Decypt
             } else {
                 System.out.println("First line must be a 1 for encryption or 2 for decryption");
                 System.exit(0);
             }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Convert String of hexadecimals to array of Integers 
    public static Integer[] stringToHex(String str) {
        List<Integer> strAsHexList = new ArrayList<Integer>();
        int i = 0;
        while (i < str.length()) {
            String number = String.valueOf(str.charAt(i)) + String.valueOf(str.charAt(i+1));//+str.charAt(0+1);
            //System.out.print(Integer.parseInt(number, 16)); //Integer.parseInt(number, 16)
            strAsHexList.add(Integer.parseInt(number, 16));
            i = i+3;
        }
        Integer[] strAsHex = new Integer[strAsHexList.size()];
        strAsHexList.toArray(strAsHex);
        return strAsHex;
    }
}