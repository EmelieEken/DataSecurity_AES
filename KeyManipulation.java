public class KeyManipulation{

    //Return expanded key divided into words ( = 4 bytes) in 2d array
    public static int[][] keyExpansion(int[] key) {
        int keySize = key.length;
        int expandedKeySize = 0;
        int rounds = 0;
        switch (keySize) {
            case 16: 
                expandedKeySize = 44; //Number of words
                rounds = 10;
                break;
            case 24: //Test these?
                expandedKeySize = 52; //Number of words
                rounds = 12;
                break;
            case 32:
                expandedKeySize = 60; //Number of words
                rounds = 14;
                break;
        }
        int[][] expandedKey = new int[expandedKeySize][4]; //words have 4 bytes

        for (int i=0; i<4; i++){ //Add first 4 words
            for (int j=0; j<4; j++) {
                expandedKey[i][j] = key[i*4+j]; //Add bytes in word
            }
        }

        //Create round constant (1st of 4 bytes in word, the rest are 0)
        int[] roundConstant = getRoundConstants(rounds);

        for (int i=4;i<expandedKeySize;i++) {
            if (i%4 == 0) {
                for(int j=0;j<4;j++) {

                    expandedKey[i][j] = expandedKey[i-1][(j+1)%4]; //Shift rows
                    expandedKey[i][j] = Block.substituteByte(expandedKey[i][j]); //Subst in S-box
                    
                    if (j == 0) {//XOR with Round constant for every 4th
                        expandedKey[i][j] = expandedKey[i][j]^roundConstant[i/4-1];
                    }
                    expandedKey[i][j] = expandedKey[i][j]^expandedKey[i-4][j]; //XOR with 4 back
                }


            } else {
                for (int j=0; j<4; j++){
                    expandedKey[i][j] = expandedKey[i-1][j]^expandedKey[i-4][j]; //XOR previous and 4 back
                }
            }
        }

        return expandedKey;
    }

    //Create the round constants to use in key expansion
    private static int[] getRoundConstants(int rounds) {

        int[] roundConstant = new int[rounds];
        roundConstant[0] = 0x01;
        Polynomial p2 = Polynomial.fromByte(0x02);

        //current = previous*2 in GF(2^8)
        for (int i=1;i<roundConstant.length;i++) {
            if (2*roundConstant[i-1]>255) {
                Polynomial p1 = Polynomial.fromByte(roundConstant[i-1]);
                roundConstant[i] = p1.multGF8(p2).toByte();
            } else {
                roundConstant[i] = 2*roundConstant[i-1];
            }
        }

        return roundConstant;
    }
}