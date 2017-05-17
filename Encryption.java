//package data_security_assignment_2;
//import data_security_assignment_2.*;

public class Encryption{
    private Block[] blocks; //Array of Blocks to be encrypted (from text)
    private int modeOfOperation;
    private int transmissionSize;
    private int[] initVector;


    //Default constructor
    public Encryption() {
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

    	//prerequisites
        if (text.length % 16 != 0) {
            //Change to add padding?
            System.out.println("Text must be a multiple of 16 long");
            System.exit(0);
        }

        if (key.length % 16 != 0) {
            System.out.println("Key must be a multiple of 16 long");
            System.exit(0);
        }

        if (modeOfOperation == 0 && (initVector.length != 1 || initVector[0] != 0)) {
            System.out.println(initVector[0]);
            System.out.println("Initialisation vector must be set to 0 for ECB mode");
            System.exit(0);
        }
        
        if (modeOfOperation < 4 && modeOfOperation > 0 && initVector.length != 16) {
            System.out.println("Initialisation vector must be 16 bytes long for CBC, CFB, and OFB mode");
            System.exit(0);
        }

        //Create blocks (array of Block)
        //System.out.println("Dividing into blocks");
        blocks = new Block[text.length/16]; //Initialise array of textblocks for encryption
        for (int i=0; i<text.length/16; i++) {
            int n=0;
            int[] textToBlock = new int[16];
            for (int j=0; j<4; j++) {
                for (int k=0; k<4; k++) {
                    textToBlock[n] = text[16*i+n];
                    //System.out.print(textToBlock[n] + " ");
                    n++;
                }
            }
            blocks[i] = new Block(textToBlock);
            //System.out.println();
        }


        switch (modeOfOperation) { //ECB, Electronic code book. Same key used for all blocks
            case 0:
                //for (int i = 0; i<text.length/16; i++) {
                EncryptBlockECB(blocks, key);
                //blocks[i].toString();
                //}
                break;

            case 1: //CFB
            //EncryptBlockCFB();
                break;
            case 2:  //CBC
            //EncryptBlockCBC(blocks[0], initVector);
                for (int i = 1; i<blocks.length; i++) {
                //EncryptBlockCBC(blocks[i], blocks[i-1]);
                }
                break;
            case 3: //OFB
                break;
            default:
                System.out.println("Mode of operation must be 0,1,2, or 3 for ECB, CFB, CBC, or OFB");
                break;
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
    private static void EncryptBlockECB(Block[] blocks, int[] key) {

        for (int i=0; i<blocks.length; i++) {
            blocks[i].encrypt(key);
        }

        //Expand key
        // int[][] expandedKey = encryptionFunctions.keyExpansion(key);

        // int rounds = 0;

        // switch(key.length) {
        //     case 16: 
        //         rounds = 10;
        //         break;
        //     case 24:
        //         rounds = 12;
        //         break;
        //     case 32:
        //         rounds = 14;
        //         break;
        // }

        // block.addRoundKey(key); //Add round key (original key) before going into first round

        // for (int i = 0; i<rounds-1; i++) { //Do 9 rounds: byte sustitution, shift rows, mix col, add round key
            
        //     //block.substituteBytes();
        //     //System.out.println("\n After byte substitution \n" + block.toString());
        //     //block.shiftRows();
        //     //block.mixCols();
        //     int[] currentKey = new int[16];
        //     //System.out.print("\nCurrent key");
        //     for (int j = 0; j<16; j++) {

        //         currentKey[j] = expandedKey[(i+1)*4 + j/4][j%4]; 
        //         //System.out.print(currentKey[j] + " ");
        //     }
        //     //block.addRoundKey(currentKey);
        //     block.regularRoundEncryption(currentKey);
        //     System.out.println("\n After addRoundKey \n" + block.toString());
        // }

        // //Round 10 
        // System.out.println("-----Final Round-----");
        // //block.substituteBytes();
        // //System.out.println("\n After byte substitution \n" + block.toString());
        // //block.shiftRows();
        // int[] currentKey = new int[16];
        // for (int j = 0; j<16; j++) {
        //     currentKey[j] = expandedKey[(9+1)*4 + j/4][j%4]; 
        //     //System.out.print(currentKey[j] + " ");
        // }
        // //block.addRoundKey(currentKey);
        // block.lastRoundEncryption(currentKey);
        // System.out.println("\n After addRoundKey \n" + block.toString());
    }

    // public static void main(String[] args) {
    //     int[] in = {0};
    //     int[] plainText = {0x56, 0xA2, 0x5F, 0x9F, 0x5F, 0x5F, 0x8F, 0x5F, 0x5F, 0x52, 0x5F, 0x5F, 0x5F, 0x5F, 0x4F, 0x5F, 0x46, 0xA2, 0x5F, 0x9F, 0x5F, 0x5F, 0x8F, 0x5F, 0x5F, 0x52, 0x5F, 0x5F, 0x5F, 0x5F, 0x4F, 0x5F};
    //     int[] key = {0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0xE5};
    //     Encryption encrypt = new Encryption(0,0,in);
    //     System.out.println(encrypt.Encrypt(plainText, key));
    // }
    
    
    //not static because it uses initVector
    private void EncryptBlockCBC(Block[] blocks, int[] key) {

    	Block[] newBlocks = new Block[blocks.length];
    	Block tmp = new Block(initVector);
        for (int i=0; i<blocks.length; i++) {
        	tmp = tmp.add(blocks[i]);
        	tmp.encrypt(key);
            newBlocks[i] = new Block(tmp);
        }
        blocks = newBlocks;
    }


}
