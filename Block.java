//package data_security_assignment_2;

public class Block {

	// mix columns ; shift rows
	
	//every element is a integer representing a byte ( 0x5A )
	private int[][] data;
	
	final static public int[][] ENCRYPTION_MATRIX = new int[][]{new int[]{2,3,1,1},new int[]{1,2,3,1},new int[]{1,1,2,3},new int[]{3,1,1,2}};
	final static public int[][] DECRYPTION_MATRIX = new int[][]{new int[]{14,11,13,9},new int[]{9,14,11,13},new int[]{13,9,14,11},new int[]{11,13,9,14}};
	
	public Block(int[] byteArray) {
		data = new int[4][4];
		int i,j;
		for(j=0;j<4;j++){
			
			for(i=0;i<4;i++){
				data[i][j] = byteArray[4*j+i];
			}
		}
	}
	
	public Block(int[][] byteMatrix){
		data = new int[4][4];
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				data[i][j] = byteMatrix[i][j];
			}
		}
	}
	
	public void regularRoundEncryption(int[] key){
		this.substituteBytes();
		this.shiftRowsEncryption();
		this.mixColumnsEncryption();
		this.addRoundKey(key);
	}
	
	public void lastRoundEncryption(int[] key){
		this.substituteBytes();
		this.shiftRowsEncryption();
		this.addRoundKey(key);
	}
	
	public void mixColumn(int columnNum,int[][] multMatrix){
		
		// we built the new column
		int[] newColumn = new int[4];
		for(int i=0;i<4;i++){
			Polynomial p = new Polynomial();
			for(int j=0;j<4;j++){
				
				// elt i,j from the multmatrix
				Polynomial p1 = Polynomial.fromByte(multMatrix[i][j]);
//				System.out.println("p1 "+i+": "+multMatrix[i][j]+" -> "+p1.toString());
				
				Polynomial p2 = Polynomial.fromByte(this.data[j][columnNum]);
//				System.out.println("p2 "+i+": "+String.format("%02X", this.data[j][columnNum])+" -> "+p2.toString());
				
				Polynomial tmp = p1.mult(p2);
//				System.out.println("tmp: "+tmp.toString()+" ("+String.format("%02X", tmp.toByte())+")");
				tmp = tmp.divide(Polynomial.RED);
//				System.out.println("tmp: "+tmp.toString());
//				System.out.println("p: "+p.toString());
				p = p.add(tmp);
//				System.out.println("p: "+p.toString());
//				System.out.println();
			}
			
//			System.out.println("poly "+i+": "+p.toString());
			newColumn[i] = p.toByte();
//			System.out.println();
		}
				
		// we replace the column in the block
		for(int i=0;i<4;i++){
			this.data[i][columnNum] = newColumn[i];
		}
	}
	
	public void mixColumnsEncryption(){
		for(int j=0;j<4;j++){
			mixColumn(j,ENCRYPTION_MATRIX);
		}
	}
	
	public void mixColumnsDecryption(){
		for(int j=0;j<4;j++){
			mixColumn(j,DECRYPTION_MATRIX);
		}
	}
	
	public void shiftRowsEncryption(){
		int[][] newMatrix = new int[4][4];
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				newMatrix[i][j] = data[i][(j+i) % 4]; 
			}
		}
		data = newMatrix;
	}
	public void shiftRowsDecryption(){
		int[][] newMatrix = new int[4][4];
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				newMatrix[i][j] = data[i][(j-i) % 4]; 
			}
		}
		data = newMatrix;
	}
	
	public String toString(){
		String res = "";
		int i,j;
		for(i=0;i<4;i++){
			for(j=0;j<4;j++){
				res += String.format("%02X", data[i][j])+" ";
			}
			res += "\n\n";
		}
		return res;
	}
	
	public String toStringOneLine(){
		String res = "";
		int i,j;
		for(i=0;i<4;i++){
			for(j=0;j<4;j++){
				res += String.format("%02X", data[i][j])+" ";
			}
		}
		return res;
	}
	
	//block is a 4x4 2d array, key is a 16 byte long array
    public void addRoundKey(int[] key) {
        int k = 0;
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                data[j][i] = data[j][i]^key[k]; //do columnwise
                k++;
            }
        }

        // System.out.println("\n After addRoundKey \n");
        // for (int i=0; i<4; i++) {
        //     System.out.print("[ ");
        //     for (int j=0;j<4;j++) {
        //         System.out.print(Integer.toHexString(data[i][j]) + " ");
        //     }
        //     System.out.println("]");
        // }
    }

	//Substitute all bytes in data array
	public void substituteBytes() {

        //System.out.println("\n After byte substitution \n");

        //Substitute bytes, fist 4 bits = row, last 4 bits = column
        for (int i=0; i<4; i++) {
            //System.out.print("[ ");
            for (int j=0;j<4;j++) {
                data[i][j] = substituteByte(data[i][j]);
                //System.out.print(Integer.toHexString(data[i][j]) + " ");
            }
            //System.out.println("]");
        }

    }


	//Substitute byte to entry in S-box, called by substituteBytes
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


	//Inverse functions

	//The same as addRoundKey
    public void invAddRoundKey(int[] key) {
       addRoundKey(key);
    }


	//Not implemented yet
	public void invSubstituteBytes() {
		//Like substBytes but with SI-matrix, call invSubstByte
	}
	//Not implemented yet
	private int invSubstituteByte(int toBeSubst) {
		int[][] sBox = {
			{0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb},
			{0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb},
			{0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e},
	};
        return 0;
    }
}
