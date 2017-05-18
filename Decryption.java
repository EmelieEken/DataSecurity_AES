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

    //Save settings for decryption
    public Decryption(int modeOfOperation, int transmissionSize, int[] initVector) {
        this.modeOfOperation = modeOfOperation;
        this.transmissionSize = transmissionSize;
        this.initVector = initVector;
    }


    //Decrypt text with key according to the saved settings
    public String Decrypt(int[] text, int[] key) {

        //Prerequisites
        if (text.length % 16 != 0) {
            System.out.println("Text must be a multiple of 16 long");
            System.exit(0);
        }

        if (key.length != 16 && key.length != 24 && key.length != 32) { //Key must be of length 16, 24 or 32 bytes
            System.out.println("Key must be a 16, 24, or 32 bytes long");
            System.exit(0);
        }

        //Check Initialisation vector
        if (modeOfOperation == 0 && (initVector.length != 1 || initVector[0] != 0)) { //Should be 0 if ECB used
            System.out.println("Initialisation vector must be set to 0 for ECB mode");
            System.exit(0);
        }
        
        if (modeOfOperation < 4 && modeOfOperation > 0 && initVector.length != 16) { //Should be 16 bytes long for the rest
            System.out.println("Initialisation vector must be 16 bytes long for CBC, CFB, and OFB mode");
            System.exit(0);
        }

        //Check Transmission size
        if (modeOfOperation != 1 && transmissionSize != 0) { //Should be set to 0 if not CFB
            System.out.println("Transmission size must be set to 0 for ECB, CBC, and OFB mode");
            System.exit(0);
        } 

        if (modeOfOperation == 1 && (transmissionSize < 1 || transmissionSize > 16)) { //For CFB, should be between 1 and 16
            System.out.println("Transmission size must be in the interval 1-16 bytes");
            System.exit(0);
        }
        
        //Create blocks from text (array of Block), not for CFB since done after decryption
        if (modeOfOperation != 1) {
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
        }


        switch (modeOfOperation) { //ECB, Electronic code book
            case 0:
                DecryptBlockECB(blocks, key);
                break;
            case 1: //CFB
                DecryptBlockCFB(text, key);
                //Create blocks for output
                blocks = new Block[text.length/16]; //Initialise array of textblocks, to 
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
                break;
            case 2:  //CBC
                DecryptBlockCBC(blocks, key);
                break;
            case 3: //OFB
                DecryptBlockOFB(blocks, key);
                break;
            default: //If none of above, faulty input
                System.out.println("Mode of operation must be 0,1,2, or 3 for ECB, CFB, CBC, or OFB");
                break;
        }

            

        //Return blocks as one line
        String plainText = "";
        for (int i=0; i<text.length/16; i++) {
            plainText += blocks[i].toStringOneLine(); 
        }

        return plainText; //Return decrypted text
    }


    //Functions for all modeOfOperation

    //ECB, all blocks decrypted independent of each other, in the same way
    private static void DecryptBlockECB(Block[] blocks, int[] key) {

        for (int i=0; i<blocks.length; i++) {
            blocks[i].decrypt(key);
        }
    
    }
    
    //decrypt the block array with the CBC method with the given key
    private void DecryptBlockCBC(Block[] blocks, int[] decKey) {
    	
    	Block c_i_1 = new Block(initVector);
    	Block c_i;
    	Block tmp;
    	
    	for(int i=0;i<blocks.length;i++){
    		c_i = new Block(blocks[i]);
    		tmp = new Block(blocks[i]);

    		tmp.decrypt(decKey);
            // add the previous cipher block before adding it to the deciphered array
    		blocks[i] = tmp.add(c_i_1);
    		c_i_1 = new Block(c_i);
    	}
    }

    //CFB
    private void DecryptBlockCFB(int[] text ,int[] key) {
        Block initBlock = new Block(this.initVector);

        int[] currentText = new int[transmissionSize];

        //Divide plaintext into arrays of length transmissionSize
        for(int i=0; i<text.length/transmissionSize; i++) {
            initBlock.encrypt(key);
            int[] selectedBytes = selectSBytes(initBlock);//Select transmissionSize bytes
            int[] c_i = new int[transmissionSize]; //Ciphertext to be used in next round in shift register
            for (int j=0; j<transmissionSize; j++) {
                c_i[j] = text[i*transmissionSize + j]; //Save ciphertext bit to use in shift register
                text[i*transmissionSize + j] = text[i*transmissionSize + j]^selectedBytes[j]; //XOR with plaintext 
                
            }
            initBlock = new Block(shiftRegister(initBlock, c_i)); //Prepare for next round

        }

    }

    //Method used by DecryptBlockCFB before XOR with ciphertext
    private int[] selectSBytes(Block block) {
        int[] selectedBytes = new int[transmissionSize];
        for (int i=0; i<transmissionSize; i++) {
            selectedBytes[i] = block.get(i);
        }
        return selectedBytes;
    }

    //Method used by DecryptBlockCFB to create new block to encrypt
    private int[] shiftRegister(Block previousEncrIV, int[] text) {

        int[] newIV = new int[16]; //16 = IV size
        for (int i=0; i<16-transmissionSize; i++) {
            newIV[i] = previousEncrIV.get(i+transmissionSize);//Get Least significant bits (to the right)
        }
        for (int i=16-transmissionSize; i<16; i++) {
            newIV[i] = text[i-(16-transmissionSize)]; //Add ciphertext to end
        }

        return newIV;

    }

    //OFB
    private void DecryptBlockOFB(Block[] blocks, int[] key) {

        Block initBlock = new Block(this.initVector);

        for (int i=0; i<blocks.length; i++) {
            initBlock.encrypt(key); //Encrypt Nonce (= initVector)
            blocks[i] = blocks[i].add(initBlock); //XOR block and the encrypted nonce 
        }

    }

}
