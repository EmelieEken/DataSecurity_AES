//package data_security_assignment_2;
//import data_security_assignment_2.*;

public class Encryption{
    private Block[] blocks; //Array of Blocks to be encrypted (from text)
    //private int[][][] blocks;
    private int modeOfOperation;
    private int transmissionSize;
    private int[] initVector;


    //Default constructor
    public Encryption() {
        //this.blocks = new Block[]();
        //this.blocks = new int[0][0][0];
        this.modeOfOperation = 0;
        this.transmissionSize = 0;
        this.initVector = new int[0];
    }

    public Encryption(int modeOfOperation, int transmissionSize, int[] initVector) {
        this.modeOfOperation = modeOfOperation;
        this.transmissionSize = transmissionSize;
        this.initVector = initVector;
    }

    public String Encrypt(int[] text, int[] key) {
        //encryptionFunctions encr = new encryptionFunctions();

        //Print text
        System.out.print("Text: \n [ " );
        for(int i=0; i<text.length;i++){
            System.out.print(text[i] + " ");
        }
        System.out.println("]" );

        if (text.length % 16 != 0) {
            //Change to add padding?
            System.out.println("Text must be a multiple of 16 long");
            System.exit(0);
        }

        if (key.length % 16 != 0) {
            System.out.println("Key must be a multiple of 16 long");
            System.exit(0);
        }

        //Create blocks
        System.out.println("Dividing into blocks");
        blocks = new Block[text.length/16]; //Initialise array of textblocks for encryption
        for (int i=0; i<text.length/16; i++) {
            int n=0;
            int[] textToBlock = new int[16];
            for (int j=0; j<4; j++) {
                for (int k=0; k<4; k++) {
                    textToBlock[n] = text[16*i+n];
                    System.out.print(textToBlock[n] + " ");
                    n++;
                }
            }
            blocks[i] = new Block(textToBlock);
            System.out.println();
        }


        if (modeOfOperation == 0) { //ECB, Electronic code book. Same key used for all blocks
            for (int i = 0; i<text.length/16; i++) {
                EncryptBlock(blocks[i], key);
                //blocks[i].toString();
            }

        }

        //Print blocks
        System.out.print("\nAfter encryption: \n");
        for (int i=0; i<text.length/16; i++) {
            blocks[i].toString();
        }
            

        //Encrypt every block following the given modeOfOperation (call functions below)
        String cipherText = "";
        for (int i=0; i<text.length/16; i++) {
            cipherText += blocks[i].toStringOneLine(); 
        }

        return cipherText; //Return encrypted 
    }

    //create functions for all modeOfOperation

    //ECB
    private static void EncryptBlock(Block block, int[] key) {

        //Expand key
        int[][] expandedKey = encryptionFunctions.keyExpansion(key);

        block.addRoundKey(key); //Add round key (original key) before going into first round

        for (int i = 0; i<9; i++) { //Do 9 rounds: byte sustitution, shift rows, mix col, add round key
            
            block.substituteBytes();
            System.out.println("\n After byte substitution \n" + block.toString());
            //block.shiftRows();
            //block.mixCols();
            int[] currentKey = new int[16];
            //System.out.print("\nCurrent key");
            for (int j = 0; j<16; j++) {

                currentKey[j] = expandedKey[(i+1)*4 + j/4][j%4]; 
                //System.out.print(currentKey[j] + " ");
            }
            block.addRoundKey(currentKey);
            System.out.println("\n After addRoundKey \n" + block.toString());
        }

        //Round 10 
        System.out.println("-----Round 10-----");
        block.substituteBytes();
        System.out.println("\n After byte substitution \n" + block.toString());
        //block.shiftRows();
        int[] currentKey = new int[16];
        for (int j = 0; j<16; j++) {
            currentKey[j] = expandedKey[(9+1)*4 + j/4][j%4]; 
            //System.out.print(currentKey[j] + " ");
        }
        block.addRoundKey(currentKey);
        System.out.println("\n After addRoundKey \n" + block.toString());
    }

    public static void main(String[] args) {
        int[] in = {0};
        int[] plainText = {0x56, 0xA2, 0x5F, 0x9F, 0x5F, 0x5F, 0x8F, 0x5F, 0x5F, 0x52, 0x5F, 0x5F, 0x5F, 0x5F, 0x4F, 0x5F, 0x46, 0xA2, 0x5F, 0x9F, 0x5F, 0x5F, 0x8F, 0x5F, 0x5F, 0x52, 0x5F, 0x5F, 0x5F, 0x5F, 0x4F, 0x5F};
        int[] key = {0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0xE5};
        Encryption encrypt = new Encryption(0,0,in);
        System.out.println(encrypt.Encrypt(plainText, key));
    }


}