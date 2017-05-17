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
        
        //Create blocks
        blocks = new Block[text.length/16]; //Initialise array of textblocks for Decryption
        for (int i=0; i<text.length/16; i++) {
            int n=0;
            int[] textToBlock = new int[16];
            for (int j=0; j<4; j++) {
                for (int k=0; k<4; k++) {
                    textToBlock[n] = text[16*i+n];
                    n++;
                }
            }
            blocks[i] = new Block(textToBlock);
        }


        switch (modeOfOperation) { //ECB, Electronic code book
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
                DecryptBlockOFB(blocks, key);
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


    //Functions for all modeOfOperation

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

    private void DecryptBlockCFB() {

    }

    private void DecryptBlockOFB(Block[] blocks, int[] key) {

        Block initBlock = new Block(this.initVector);
        //System.out.print("\n" + initBlock.toString());

        for (int i=0; i<blocks.length; i++) {
            initBlock.encrypt(key); //Encrypt Nonce (= initVector)
            blocks[i] = blocks[i].add(initBlock); //XOR first block and the encrypted nonce 
        }

    }

}
