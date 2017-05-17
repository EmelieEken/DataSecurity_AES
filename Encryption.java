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
                EncryptBlockECB(blocks, key);
                break;

            case 1: //CFB
            //EncryptBlockCFB();
                break;
            case 2:  //CBC
            	EncryptBlockCBC(blocks, key);
                break;
            case 3: //OFB
                EncryptBlockOFB();
                break;
            default:
                System.out.println("Mode of operation must be 0,1,2, or 3 for ECB, CFB, CBC, or OFB");
                break;
        }

//        //Print blocks
//        System.out.print("\nAfter encryption: \n");
//        for (int i=0; i<text.length/16; i++) {
//            blocks[i].toString();
//        }
            

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
    
    //not static because it uses initVector
    private void EncryptBlockCBC(Block[] blocks, int[] key) {

//    	Block[] newBlocks = new Block[blocks.length];
    	Block tmp = new Block(initVector);
        for (int i=0; i<blocks.length; i++) {
        	tmp = tmp.add(blocks[i]);
        	tmp.encrypt(key);
            blocks[i] = new Block(tmp);
        }
//        blocks = newBlocks;
    }

    private void EncryptBlockCFB() {

    }

    private void EncryptBlockOFB() {
        
    }

    
}
