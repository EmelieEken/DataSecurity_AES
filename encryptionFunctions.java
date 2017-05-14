//package data_security_assignment_2;

public class encryptionFunctions{
    public static void main(String[] args) {
        int[][] block = new int[4][4];
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                block[i][j] = (int)(Math.random() * 255);
            }

        }

        for (int i=0; i<4; i++) {
            System.out.print("[ ");
            for (int j=0;j<4;j++) {
                System.out.print(Integer.toHexString(block[i][j]) + " ");
            }
            System.out.println("]");
        }



        //substituteBytes(block);

        int[] key = {0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76};
        //addRoundKey(block, key);

        int[][] expandedKey = keyExpansion(key);
        
        int[] roundConstant = new int[10];
        roundConstant[0] = 0x01;
        //System.out.print("\nRound constants: ");
        for (int i=1;i<roundConstant.length-2;i++) {
           roundConstant[i] = 2*roundConstant[i-1];
           System.out.print(Integer.toHexString(roundConstant[i]) + " ");
        }

        Polynomial p1 = Polynomial.fromByte(0x80);
        Polynomial p2 = Polynomial.fromByte(0x02);
        System.out.println(Integer.toHexString((p1.mult(p2)).toByte()));
    
    }


    //Added to Block
    // public static void substituteBytes(int[][] block) {

    //     System.out.println("\n After byte substitution \n");

    //     //Substitute bytes, fist 4 bits = row, last 4 bits = column
    //     for (int i=0; i<4; i++) {
    //         System.out.print("[ ");
    //         for (int j=0;j<4;j++) {
    //             block[i][j] = substituteByte(block[i][j]);
    //             System.out.print(Integer.toHexString(block[i][j]) + " "); //Move down to own method
    //         }
    //         System.out.println("]");
    //     }
    // }


    //added to Block
    //Substitute byte to entry in S-box
    private static int substituteByte(int toBeSubst) {
        int[][] sBox = {
            {0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76}, //1
            {0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C, 0xA4, 0x72, 0xC0},
            {0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15},
            {0x04, 0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75}, //4
            {0x09, 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84}, 
            {0x53, 0xD1, 0x00, 0xED, 0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF},
            {0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33, 0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8},
            {0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38, 0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2}, //8
            {0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73},
            {0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb },
            {0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79},
            {0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08}, //12
            {0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a},
            {0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e},
            {0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf},
            {0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16} //16
        };

        String hex = Integer.toHexString(toBeSubst);
        if (hex.length() < 2) { //If int < 16, add initial 0
            hex = '0'+hex;
        }
        int row = Integer.parseInt(""+hex.charAt(0),16);
        int col = Integer.parseInt(""+hex.charAt(1),16);
        return sBox[row][col];
    }


    //Added to Block
    //block is a 4x4 2d array, key is a 16 byte long array
    // public static void addRoundKey(int[][] block, int[] key) {
    //     int k = 0;
    //     for (int i=0; i<4; i++) {
    //         for (int j=0; j<4; j++) {
    //             block[j][i] = block[j][i]^key[k]; //do columnwise
    //             k++;
    //         }
    //     }

    //     System.out.println("\n After addRoundKey \n");
    //     for (int i=0; i<4; i++) {
    //         System.out.print("[ ");
    //         for (int j=0;j<4;j++) {
    //             System.out.print(Integer.toHexString(block[i][j]) + " ");
    //         }
    //         System.out.println("]");
    //     }
    // }



    //Return key divided into words ( = 4 bytes) in 2d array
    public static int[][] keyExpansion(int[] key) {
        int keySize = key.length;
        int expandedKeySize = 0;
        switch (keySize) {
            case 16: 
                expandedKeySize = 44; //words
                break;
            //Add more cases
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

        //add constant (1st of 4 bytes in word, the rest are 0)
        int[] roundConstant = new int[10]; //Make dynamic and change to GF
        roundConstant[0] = 0x01;
        //System.out.print("\nRound constants: ");
        for (int i=1;i<roundConstant.length-2;i++) {
            roundConstant[i] = 2*roundConstant[i-1];
            //System.out.print(Integer.toHexString(roundConstant[i]) + " ");
        }
        
        roundConstant[8] = 0x1B;
        roundConstant[9] = 0x36;

        //System.out.println("\n");
        for (int i=4;i<expandedKeySize;i++) {
            if (i%4 == 0) {
                //System.out.println();
                for(int j=0;j<4;j++) {
                    expandedKey[i][j] = expandedKey[i-1][(j+1)%4]; //Shift rows
                    expandedKey[i][j] = substituteByte(expandedKey[i][j]); //Subst in S-box
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


}