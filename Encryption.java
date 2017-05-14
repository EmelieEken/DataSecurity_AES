public class Encryption{
    //private Block[] blocks; //Array of blocks
    private int[][][] blocks;
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
        encryptionFunctions encr = new encryptionFunctions();

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
        blocks = new int[text.length/16][4][4]; //Initialise array of textblocks for encryption
        for (int i=0; i<text.length/16; i++) {
            int n=0;
            //blocks[i] = new int[4][4];
            for (int j=0; j<4; j++) {
                for (int k=0; k<4; k++) {
                    blocks[i][k][j] = text[16*i+n];
                    System.out.print(blocks[i][k][j] + " ");
                    n++;
                }
            }
            System.out.println();
        }


        if (modeOfOperation == 0) { //ECB, Electronic code book. Same key used for all blocks
            for (int i = 0; i<text.length/16; i++) {
                EncryptBlock(blocks[i], key);
            }

        }

        //Print blocks
        System.out.print("\nAfter encryption: \n");
        for (int i=0; i<text.length/16; i++) {
            System.out.print("{ ");
            for (int j=0; j<4; j++) {
                for (int k=0; k<4; k++) {
                    System.out.print(blocks[i][k][j] + " ");
                }
                System.out.println();
            }
            System.out.print("} \n");
        }
            

        //Encrypt every block following the given modeOfOperation (call functions below)

        return "*"; //Return encrypted 
    }

    //create functions for all modeOfOperation

    //ECB
    private static void EncryptBlock(int[][] block, int[] key) {

        //Expand key
        int[][] expandedKey = encryptionFunctions.keyExpansion(key);

        encryptionFunctions.addRoundKey(block, key); //Add round key (original key) before going into first round

        for (int i = 0; i<9; i++) { //Do 9 rounds: byte sustitution, shift rows, mix col, add round key
            encryptionFunctions.substituteBytes(block);
            //encryptionFunctions.shiftRows(block);
            //encryptionFunctions.mixCols(block);
            int[] currentKey = new int[16];
            for (int j = 0; j<16; j++) {

                currentKey[j] = expandedKey[(i+1)*4][j%4]; 
                System.out.print(currentKey[j] + " ");
            }
            encryptionFunctions.addRoundKey(block,currentKey);
        }
    }

    public static void main(String[] args) {
        int[] in = {0,0};
        int[] plainText = {0x56, 0xA2, 0x5F, 0x9F, 0x5F, 0x5F, 0x8F, 0x5F, 0x5F, 0x52, 0x5F, 0x5F, 0x5F, 0x5F, 0x4F, 0x5F, 0x46, 0xA2, 0x5F, 0x9F, 0x5F, 0x5F, 0x8F, 0x5F, 0x5F, 0x52, 0x5F, 0x5F, 0x5F, 0x5F, 0x4F, 0x5F};
        int[] key = {0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0x56, 0xA2, 0xE5};
        Encryption encrypt = new Encryption(0,0,in);
        System.out.println(encrypt.Encrypt(plainText, key));
    }


}