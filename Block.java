package data_security_assignment_2;

public class Block {

	// mix columns ; shift rows
	
	//every element is a integer representing a byte ( 0x5A )
	private int[][] data;
	
	public Block(int[] byteArray) {
		
		data = new int[4][4];
		
		
		int i,j;
		
		for(j=0;j<4;j++){
			
			for(i=0;i<4;i++){
				data[i][j] = byteArray[4*j+i];
			}
		}
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
}
