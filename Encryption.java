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

    	// Checking the prerequisites
        if (text.length % 16 != 0) {
            //Change to add padding?
            System.out.println("Text must be a multiple of 16 long");
            System.exit(0);
        }

        if (key.length != 16 && key.length != 24 && key.length != 32) {
            System.out.println("Key must be a 16, 24, or 32 bytes long");
            System.exit(0);
        }

        //Check Initialisation vector
        if (modeOfOperation == 0 && (initVector.length != 1 || initVector[0] != 0)) { //Should be 0 if ECB used
            //System.out.println(initVector[0]);
            System.out.println("Initialisation vector must be set to 0 for ECB mode");
            System.exit(0);
        }
        
        if (modeOfOperation < 4 && modeOfOperation > 0 && initVector.length != 16) { //Should be 16 bytes long for the rest
            System.out.println("Initialisation vector must be 16 bytes long for CBC, CFB, and OFB mode");
            System.exit(0);
        }

        //Check Transmission size
        if (modeOfOperation != 1 && transmissionSize != 0) {
            System.out.println("Transmission size must be set to 0 for ECB, CBC, and OFB mode");
            System.exit(0);
        } 

        if (modeOfOperation == 1 && (transmissionSize < 1 || transmissionSize > 16)) {
            System.out.println("Transmission size must be in the interval 1-16 bytes");
            System.exit(0);
        }

        //Create blocks (array of Block), not for CFB
        if (modeOfOperation != 1) {
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
        }
        


        switch (modeOfOperation) { //ECB, Electronic code book. Same key used for all blocks
            case 0:
                EncryptBlockECB(blocks, key);
                break;

            case 1: //CFB
                EncryptBlockCFB(text, key);
                //Create blocks for output
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

                break;
            case 2:  //CBC
            	EncryptBlockCBC(blocks, key);
                break;
            case 3: //OFB
                EncryptBlockOFB(blocks, key);
                break;
            default:
                System.out.println("Mode of operation must be 0,1,2, or 3 for ECB, CFB, CBC, or OFB");
                break;
        }
            

        //Read final ciphertext from the blocks
        String cipherText = "";
        for (int i=0; i<text.length/16; i++) {
            cipherText += blocks[i].toStringOneLine(); 
        }

        return cipherText; //Return encrypted text
    }


    //Functions for all modeOfOperation

    //ECB ecryption, All blocks encrypted independent of each other
    private static void EncryptBlockECB(Block[] blocks, int[] key) {

        for (int i=0; i<blocks.length; i++) {
            blocks[i].encrypt(key);
        }

    }  
    
    // Encrypts the block array with CBC Method
    //not static because it uses initVector
    private void EncryptBlockCBC(Block[] blocks, int[] key) {

    	Block tmp = new Block(initVector);
        for (int i=0; i<blocks.length; i++) {
            // add the previous encrypted block before encryot the current one
        	tmp = tmp.add(blocks[i]);
        	tmp.encrypt(key);
            blocks[i] = new Block(tmp);
        }
    }

    private void EncryptBlockCFB(int[] text ,int[] key) {

        Block initBlock = new Block(this.initVector);

        int[] currentText = new int[transmissionSize];

        //Divide plaintext into arrays of length transmissionSize
        for(int i=0; i<text.length/transmissionSize; i++) {
            initBlock.encrypt(key);
            int[] selectedBytes = selectSBytes(initBlock);//Select transmissionSize bytes
            int[] c_i = new int[transmissionSize]; //Ciphertext to be used in next round in shift register
            for (int j=0; j<transmissionSize; j++) {
                text[i*transmissionSize + j] = text[i*transmissionSize + j]^selectedBytes[j]; //XOR with plaintext 
                c_i[j] = text[i*transmissionSize + j]; 
            }
            initBlock = new Block(shiftRegister(initBlock, c_i)); //Prepare for next round

        }

    }

    private int[] selectSBytes(Block block) {
        //System.out.println(block.toString());
        int[] selectedBytes = new int[transmissionSize];
        for (int i=0; i<transmissionSize; i++) {
            selectedBytes[i] = block.get(i);
            //System.out.println( String.format("%02X",selectedBytes[i]) + " ");
        }

        return selectedBytes;
    }

    private int[] shiftRegister(Block previousEncrIV, int[] text) {

        int[] newIV = new int[16]; //16 = IV size
        for (int i=0; i<16-transmissionSize; i++) {
            newIV[i] = previousEncrIV.get(i+transmissionSize);//Get Least significant bits (to the right)
            //System.out.print(String.format("%02X",newIV[i]) + " ");
        }
        for (int i=16-transmissionSize; i<16; i++) {
            newIV[i] = text[i-(16-transmissionSize)]; 
            //System.out.print(String.format("%02X",newIV[i]) + " ");
        }

        return newIV;
    }

    private void EncryptBlockOFB(Block[] blocks, int[] key) {
        //for (int i=0; i<initVector.length; i++) {
        //    System.out.print(String.format("%02X",initVector[i]) + " ");
        //}
        
        Block initBlock = new Block(this.initVector);
        //System.out.print("\n" + initBlock.toString());

        for (int i=0; i<blocks.length; i++) {
            initBlock.encrypt(key); //Encrypt Nonce (= initVector)
            blocks[i] = blocks[i].add(initBlock); //XOR first block and the encrypted nonce 
        }
        

        
    }

    
}
