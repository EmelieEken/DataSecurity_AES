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
        return "*";
    }

    public static void main(String[] args) {
        int[] in = {0,0};
        int[] plainText = {0x56, 0xA2};
        int[] key = {0x56, 0xA2};
        Encryption encrypt = new Encryption(0,0,in);
        System.out.println(encrypt.Encrypt(plainText, key));
    }


}