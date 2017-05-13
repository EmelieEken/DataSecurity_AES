public class Encryption{
    //private Block[] block; //Array of blocks
    private int[][] block;
    private int modeOfOperation;
    private int transmissionSize;
    private int[] initVector;


    //Default constructor
    public Encryption() {
        //this.block = new Block[]();
        this.block = new int[0][0];
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

        if (key.length % 32 != 0) {
            System.out.println("Key must be a multiple of 32 long");
            System.exit(0);
        }

        for (int i=text.length; i>0; i--) {
            //Create blocks
        }

        //Encrypt every block following the given modeOfOperation (call functions below)

        return "*"; //Return encrypted 
    }

    //create functions for all modeOfOperation

    public static void main(String[] args) {
        int[] in = {0,0};
        int[] plainText = {0x56, 0xA2, 0x5F, 0x5F, 0x5F, 0x5F, 0x5F, 0x5F, 0x5F, 0x5F, 0x5F, 0x5F, 0x5F, 0x5F, 0x5F, 0x5F};
        int[] key = {0x56, 0xA2};
        Encryption encrypt = new Encryption(0,0,in);
        System.out.println(encrypt.Encrypt(plainText, key));
    }


}