//package data_security_assignment_2;

public class Decryption{
    private Block[] blocks; //Array of Blocks to be Decrypted (from text)
    private int modeOfOperation;
    private int transmissionSize;
    private int[] initVector;


    //Default constructor
    public Decryption() {
        this.modeOfOperation = 0;
        this.transmissionSize = 0;
        this.initVector = new int[0];
    }

    public Decryption(int modeOfOperation, int transmissionSize, int[] initVector) {
        this.modeOfOperation = modeOfOperation;
        this.transmissionSize = transmissionSize;
        this.initVector = initVector;
    }

    public String Decrypt(int[] text, int[] key) {
        //DecryptionFunctions encr = new DecryptionFunctions();

        //Print text
        // System.out.print("Text: \n [ " );
        // for(int i=0; i<text.length;i++){
        //     System.out.print(text[i] + " ");
        // }
        // System.out.println("]" );

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
        //System.out.println("Dividing into blocks");
        blocks = new Block[text.length/16]; //Initialise array of textblocks for Decryption
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
                DecryptBlockECB(blocks, key);
                break;
            case 1: //CFB
                //DecryptBlockCFB();
                break;
            case 2:  //CBC
                DecryptBlockCBC(blocks, initVector);
                break;
            case 3: //OFB
                break;
            default:
                System.out.println("Mode of operation must be 0,1,2, or 3 for ECB, CFB, CBC, or OFB");
                break;
        }

//        //Print blocks
//        System.out.print("\nAfter Decryption: \n");
//        for (int i=0; i<text.length/16; i++) {
//            blocks[i].toString();
//        }
            

        //Decrypt every block following the given modeOfOperation (call functions below)
        String plainText = "";
        for (int i=0; i<text.length/16; i++) {
            plainText += blocks[i].toStringOneLine(); 
        }

        return plainText; //Return Decrypted 
    }

    //create functions for all modeOfOperation

    //ECB
    private static void DecryptBlockECB(Block[] blocks, int[] key) {

        for (int i=0; i<blocks.length; i++) {
            blocks[i].decrypt(key);
        }

    }
    
    //CBC
    // TODO to be tested
    private void DecryptBlockCBC(Block[] blocks, int[] key) {

//    	Block[] newBlocks = new Block[blocks.length];
    	
    	// to be added at the next step
    	Block cipherBlock = new Block(blocks[0]);
    	Block tmpBlock = new Block(blocks[0]);
    	
    	tmpBlock.decrypt(key);
    	tmpBlock = tmpBlock.add(new Block(initVector));
    	blocks[0] = new Block(tmpBlock);
    			
        for (int i=1; i<blocks.length; i++) {
        	tmpBlock = new Block(blocks[i]);
        	tmpBlock.decrypt(key);
        	tmpBlock = tmpBlock.add(cipherBlock);
        	cipherBlock = new Block(blocks[i]);
        	blocks[i] = new Block(tmpBlock);
        }
//        blocks = newBlocks;
    }

//     public static void main(String[] args) {
//         int[] in = {0};
//         int[] plainText = {0x56, 0xA2, 0x5F, 0x9F, 0x5F, 0x5F, 0x8F, 0x5F, 0x5F, 0x52, 0x5F, 0x5F, 0x5F, 0x5F, 0x4F, 0x5F, 0x46, 0xA2, 0x5F, 0x9F, 0x5F, 0x5F, 0x8F, 0x5F, 0x5F, 0x52, 0x5F, 0x5F, 0x5F, 0x5F, 0x4F, 0x5F};
//         int[] key = {0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0xE5};
//         Decryption Decrypt = new Decryption(0,0,in);
//         System.out.println(Decrypt.Decrypt(plainText, key));
//     }


}
