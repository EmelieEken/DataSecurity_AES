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

        //Check Initialisation vector
        if (modeOfOperation == 0 && (initVector.length != 1 || initVector[0] != 0)) {
            //System.out.println(initVector[0]);
            System.out.println("Initialisation vector must be set to 0 for ECB mode");
            System.exit(0);
        }
        
        if (modeOfOperation < 4 && modeOfOperation > 0 && initVector.length != 16) {
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
                DecryptBlockCBC(blocks, key);
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
    private void DecryptBlockCBC(Block[] blocks, int[] decKey) {
    	
    	Block c_i_1 = new Block(initVector);
    	Block c_i;
    	Block tmp;
    	
    	for(int i=0;i<blocks.length;i++){
    		c_i = new Block(blocks[i]);
    		tmp = new Block(blocks[i]);

//    		System.out.println("----- step "+i+" -----");
//    		System.out.println("c_i_1:\n"+c_i_1.toString());
//    		System.out.println("c_i:\n"+c_i.toString());
    		
    		tmp.decrypt(decKey);
//    		System.out.println("tmp after decryption:\n"+tmp.toString());
    		blocks[i] = tmp.add(c_i_1);
//    		System.out.println("new Block:\n"+blocks[i].toString());
    		c_i_1 = new Block(c_i);
    	}
    }

    private void DecryptBlockCFB(int[] text ,int[] key) {
        Block initBlock = new Block(this.initVector);
        //System.out.print("\n" + initBlock.toString());

        int[] currentText = new int[transmissionSize];

        //Divide plaintext into arrays of length transmissionSize
        for(int i=0; i<text.length/transmissionSize; i++) {
            initBlock.encrypt(key);
            int[] selectedBytes = selectSBytes(initBlock);//Select transmissionSize bytes
            int[] c_i = new int[transmissionSize]; //Ciphertext to be used in next round in shift register
            for (int j=0; j<transmissionSize; j++) {
                c_i[j] = text[i*transmissionSize + j]; //Save ciphertext bit to use in shift register
                //System.out.print( String.format("%02X",c_i[j]) + " ");
                text[i*transmissionSize + j] = text[i*transmissionSize + j]^selectedBytes[j]; //XOR with plaintext 
                //System.out.print( String.format("%02X",c_i[j]) + " ");
                
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
        //System.out.println("\n\n" + previousEncrIV.toString());
        //System.out.println("Ciphertext ");
        //for (int i=0; i<text.length; i++) {
        //    System.out.print(String.format("%02X",text[i]) + " ");;
        //}

        //System.out.println("\nnewIV ");

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

    private void DecryptBlockOFB(Block[] blocks, int[] key) {

        Block initBlock = new Block(this.initVector);
        //System.out.print("\n" + initBlock.toString());

        for (int i=0; i<blocks.length; i++) {
            initBlock.encrypt(key); //Encrypt Nonce (= initVector)
            blocks[i] = blocks[i].add(initBlock); //XOR first block and the encrypted nonce 
        }

    }

}
