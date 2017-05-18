public class Block {
	
	//Every element is an integer representing a byte ( on the form: 0x5A ) stored in a 2d-array
	private int[][] data;
	
	//some raw matrix to process the mix column operation
	final static public int[][] ENCRYPTION_MATRIX = new int[][]{new int[]{2,3,1,1},new int[]{1,2,3,1},new int[]{1,1,2,3},new int[]{3,1,1,2}};
	final static public int[][] DECRYPTION_MATRIX = new int[][]{new int[]{14,11,13,9},new int[]{9,14,11,13},new int[]{13,9,14,11},new int[]{11,13,9,14}};
	
	//Default constructor
	public Block() {
		data = new int[4][4];
	}
	
	// create  Block from an array of int representing bytes
	public Block(int[] byteArray) {
		data = new int[4][4];
		int i,j;
		for(j=0;j<4;j++){
			
			for(i=0;i<4;i++){
				data[i][j] = byteArray[4*j+i];
			}
		}
	}
	
	// create a block using the same byte Matrix
	public Block(int[][] byteMatrix){
		data = new int[4][4];
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				data[i][j] = byteMatrix[i][j];
			}
		}
	}
	
	//Copy constructor
	public Block(Block b){
		data = new int[4][4];
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				data[i][j] = b.data[i][j];
			}
		}
	}
	
	//Return a block which is the XOR of the this and b
	public Block add(Block b){
		Block res = new Block();
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				res.data[i][j] = this.data[i][j] ^ b.data[i][j];
			}
		}
		return res;
	}

	//Get element i
	public int get(int i) {
		return this.data[i%4][i/4];
	}
	
	//Encrypt block with key
	// this operation is independant from the operation mode
	public void encrypt(int[] key){
		
		//Expand key
		int[][] expandedKey = KeyManipulation.keyExpansion(key);
		int rounds = 0;

		//Decide number of rounds based on key length
		switch(key.length) {
		    case 16: 
			rounds = 10;
			break;
		    case 24:
			rounds = 12;
			break;
		    case 32:
			rounds = 14;
			break;
		}

		this.addRoundKey(key); //Add round key (original key) before going into first round

		for (int i = 0; i<rounds-1; i++) { //Do 9-13 rounds: byte substitution, shift rows, mix col, add round key

		    int[] currentKey = new int[16];
		    for (int j = 0; j<16; j++) {

			currentKey[j] = expandedKey[(i+1)*4 + j/4][j%4]; 
		    }
		    this.regularRoundEncryption(currentKey);
		}

		//Final round
        int[] currentKey = new int[16];
        for (int j = 0; j<16; j++) {
            currentKey[j] = expandedKey[(rounds-1+1)*4 + j/4][j%4]; 
        }
        this.lastRoundEncryption(currentKey);
	}
	
	//Decrypt block with key
	// this is the reverse of the encryption method
	public void decrypt(int[] key){
		
		//Expand key
		int[][] expandedKey = KeyManipulation.keyExpansion(key);
		int rounds = 0;

		//Decide number of rounds based on key length
		switch(key.length) {
		    case 16: 
			rounds = 10;
			break;
		    case 24:
			rounds = 12;
			break;
		    case 32:
			rounds = 14;
			break;
		}

	int[] currentKey = new int[16]; //Extract the key to use for this round from the expanded key
        for (int j = 0; j<16; j++) {
            currentKey[j] = expandedKey[(rounds)*4 + j/4][j%4]; //40-43
        }

		this.invAddRoundKey(currentKey); //Add round key (original key) before going into first round


		for (int i = 0; i<rounds-1; i++) { //Do 9-13 rounds: byte substitution, shift rows, mix col, add round key

		    currentKey = new int[16];
		    for (int j = 0; j<16; j++) {

			currentKey[j] = expandedKey[(rounds-(i+1))*4 + j/4][j%4];
		    }
		    this.regularRoundDecryption(currentKey);
		}

		//Final round
        currentKey = new int[16];
        for (int j = 0; j<16; j++) {
            currentKey[j] = expandedKey[0 + j/4][j%4]; 
        }
        this.lastRoundDecryption(currentKey);
	}

	//Procedure for encrypting, round 1-9, 1-11 or 1-13 (depending on the initial keylength)
	public void regularRoundEncryption(int[] key){
		this.substituteBytes();
		this.shiftRowsEncryption();
		this.mixColumnsEncryption();
		this.addRoundKey(key);
	}
	
	//Procedure for encrypting, last round
	public void lastRoundEncryption(int[] key){
		this.substituteBytes();
		this.shiftRowsEncryption();
		this.addRoundKey(key);
	}
	
	//Procedure for decrypting, round 1-9, 1-11 or 1-13 (depending on the initial keylength)
	public void regularRoundDecryption(int[] key){
		this.shiftRowsDecryption();
		this.invSubstituteBytes();
		this.invAddRoundKey(key);
		this.mixColumnsDecryption();
	}

	//Procedure for decryptiong, last round
	public void lastRoundDecryption(int[] key){
		this.shiftRowsDecryption();
		this.invSubstituteBytes();
		this.invAddRoundKey(key);
	}
	
	//Mix column
	public void mixColumn(int columnNum,int[][] multMatrix){
		
		//Build the new column
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
		//System.out.println("Mixcols Encr  \n"+this.toString());
	}
	
	public void mixColumnsDecryption(){
		for(int j=0;j<4;j++){
			mixColumn(j,DECRYPTION_MATRIX);
		}
		//System.out.println("Mixcols Decr  \n"+this.toString());
	}
	
	public void shiftRowsEncryption(){
		int[][] newMatrix = new int[4][4];
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				newMatrix[i][j] = data[i][(j+i) % 4]; 
			}
		}
		data = newMatrix;
		//System.out.println("ShiftRows Encr \n "+this.toString()); //Works
	}
	public void shiftRowsDecryption(){
		int[][] newMatrix = new int[4][4];
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				newMatrix[i][j] = data[i][((j-i)+4) % 4]; 
			}
		}
		data = newMatrix;
		//System.out.println("ShiftRows Decr  \n"+this.toString());
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
	
	//Print bytes in block on one line, read column by column
	public String toStringOneLine(){
		String res = "";
		int i,j;
		for(i=0;i<4;i++){
			for(j=0;j<4;j++){
				res += String.format("%02X", data[j][i])+" ";
			}
		}
		return res;
	}
	
	//XOR block with key, key is a 16 byte long array
    public void addRoundKey(int[] key) {
        int k = 0;
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                data[j][i] = data[j][i]^key[k]; //XOR columnwise
                k++;
            }
        }
		//System.out.println("AddRoundKey Encr  \n" +this.toString());
    }

	//Substitute all bytes in data array
	public void substituteBytes() {

        //Substitute bytes, fist 4 bits = row, last 4 bits = column
        for (int i=0; i<4; i++) {

            for (int j=0;j<4;j++) {
                data[i][j] = substituteByte(data[i][j]);
            }
        }

		//System.out.println("SubstBytes Encr  \n"+this.toString());

    }


	//Substitute byte to entry in S-box, called by substituteBytes
    protected static int substituteByte(int toBeSubst) {
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


	//The same as addRoundKey
    public void invAddRoundKey(int[] key) {
       this.addRoundKey(key);
	   //System.out.println("invAddRoundKey  \n"+this.toString());
    }


	//Inverse Substitute bytes, fist 4 bits = row, last 4 bits = column
	public void invSubstituteBytes() {

        for (int i=0; i<4; i++) {
            
            for (int j=0;j<4;j++) {
                data[i][j] = invSubstituteByte(data[i][j]);
            }
            
        }
		//System.out.println("invSubstBytes  \n"+this.toString());
	}

	private int invSubstituteByte(int toBeSubst) {
		int[][] siBox = {
			{0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb},
			{0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb},
			{0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e},
			{0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25},
			{0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92},
			{0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84},
			{0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06},
			{0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b},
			{0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73},
			{0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e},
			{0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b},
			{0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4},
			{0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f},
			{0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef},
			{0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61},
			{0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d}
		};
		String hex = Integer.toHexString(toBeSubst);
        if (hex.length() < 2) { //If int < 16, add initial 0
            hex = '0'+hex;
        }
        int row = Integer.parseInt(""+hex.charAt(0),16);
        int col = Integer.parseInt(""+hex.charAt(1),16);
        return siBox[row][col];

    }
}
