public class KeyManipulation{

    //Return key divided into words ( = 4 bytes) in 2d array
    public static int[][] keyExpansion(int[] key) {
        int keySize = key.length;
        int expandedKeySize = 0;
        int rounds = 0;
        switch (keySize) {
            case 16: 
                expandedKeySize = 44; //Number of words
                rounds = 10;
                break;
            case 24: //Test these
                expandedKeySize = 52; //Number of words
                rounds = 12;
                break;
            case 32:
                expandedKeySize = 60; //Number of words
                rounds = 14;
                break;
        }
        int[][] expandedKey = new int[expandedKeySize][4]; //words have 4 bytes

        //System.out.println("\n Key Expansion \n");

        for (int i=0; i<4; i++){ //Add first 4 words
            for (int j=0; j<4; j++) {
                expandedKey[i][j] = key[i*4+j]; //Add bytes in word
                //System.out.print(Integer.toHexString(expandedKey[i][j]) + " ");
            }
            //System.out.println();
        }

        //Create round constant (1st of 4 bytes in word, the rest are 0)
        int[] roundConstant = getRoundConstants(rounds);

        //System.out.println("\n");
        for (int i=4;i<expandedKeySize;i++) {
            if (i%4 == 0) {
                //System.out.println();
                for(int j=0;j<4;j++) {
                    expandedKey[i][j] = expandedKey[i-1][(j+1)%4]; //Shift rows
                    expandedKey[i][j] = Block.substituteByte(expandedKey[i][j]); //Subst in S-box
                    if (j == 0) {//XOR Round constant
                        expandedKey[i][j] = expandedKey[i][j]^roundConstant[i/4-1];
                    }
                    //System.out.print(Integer.toHexString(expandedKey[i][j]) + " ");
                }

            } else {
                for (int j=0; j<4; j++){
                    expandedKey[i][j] = expandedKey[i-1][j]^expandedKey[i-4][j];
                    //System.out.print(Integer.toHexString(expandedKey[i][j]) + " ");
                }
            }
            //System.out.println();
        }

        return expandedKey;
    }

    private static int[] getRoundConstants(int rounds) {
        int[] roundConstant = new int[rounds];
        roundConstant[0] = 0x01;
        Polynomial p2 = Polynomial.fromByte(0x02);
        System.out.print("\nRound constants: " + Integer.toHexString(roundConstant[0]) + " ");
        //current = previous*2 in GF(2^8)
        for (int i=1;i<roundConstant.length;i++) {
            if (2*roundConstant[i-1]>255) {
                Polynomial p1 = Polynomial.fromByte(roundConstant[i-1]);
                roundConstant[i] = p1.multGF8(p2).toByte();
            } else {
                roundConstant[i] = 2*roundConstant[i-1];
            }
            System.out.print(Integer.toHexString(roundConstant[i]) + " ");
        }
        System.out.println("\n");

        return roundConstant;
    }
}