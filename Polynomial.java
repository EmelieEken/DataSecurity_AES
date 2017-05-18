//package data_security_assignment_2;

import java.util.Arrays;

public class Polynomial {
	
	private boolean[] coeff;
	private int degree;
	
	// useful polynomials to perform to convert high degree polynomials to GF(2^8)
	final static public Polynomial RED = new Polynomial(new boolean[]{true,true,false,true,true,false,false,false,true});
	final static public Polynomial RED3 = new Polynomial(new boolean[]{true,true,false,true});
	
	public Polynomial(){
		degree = 0;
		coeff = null;
	}
	
	public Polynomial(boolean[] coeff){
		this.degree = coeff.length;
		int i = coeff.length -1;
		while(coeff[i] == false){
			this.degree --;
			i --;
		}
		
		this.coeff = Arrays.copyOfRange(coeff, 0, this.degree);
	}
	
	// copy constructor
	public Polynomial(Polynomial p){
		this.degree = p.degree;
		if(p.coeff == null){
			this.coeff = null;
		}
		else{
			this.coeff = p.coeff.clone();
		}
	}
	
	// copy in the range of the given degree
	public Polynomial(int degree, Polynomial p){
		this.degree = degree;
		this.coeff = Arrays.copyOfRange(p.coeff, 0, degree);
	}
	
	// initialise a polynomial at the given degree
	public Polynomial(int degree){
		this.degree = degree;
		this.coeff = new boolean[degree];
		for(int i=0;i<degree;i++){
			this.coeff[i] = false;
		}
		this.coeff[degree-1] = true;
	}
	
	// create a polynomial of GF(2^8) corresponding to the given int byte value
	public static Polynomial fromByte(int byteValue){
		Polynomial res = new Polynomial(8);
		res.coeff[7] = false;
		int index = 7;
		while(index >= 0 && byteValue > 0){ // in case of a wrong input
			if(byteValue >= Math.pow(2, index)){
				byteValue -= Math.pow(2, index);
				res.coeff[index] = true;
			}
			else{
				res.coeff[index] = false;
			}
			index --;
		}
		return res;
	}
	
	// update the int attribute of the polynomial to make it correspond to the boolean array
	// useful after an algebric operation
	private void updateDegree(){
		if(coeff == null){
			degree = 0;
		}
		else{
			int d = coeff.length;
			while(d > 0 && !coeff[d-1]){
				d --;
			}
			degree = d;
		}
	}
	
	// return an int corresponding to the byte value of the polynomial
	public int toByte(){
		int res = 0;
		for(int i=0; i<degree;i++){
			if(coeff[i])
			res += Math.pow(2,i);
		}
		return res;
	}
	
	
	public int getDegree(){
		return this.degree;
	}
	
	// XOR 2 polynomials
	public Polynomial add(Polynomial p){		
		
		Polynomial maxP, minP;
		if(this.degree >= p.degree){
			maxP = this;
			minP = p;
		}
		else{
			maxP = p;
			minP = this;
		}
		
		
		Polynomial res = new Polynomial(maxP);
		for(int i=0; i<minP.degree;i++){
			if(minP.coeff[i]){
				res.coeff[i] = !maxP.coeff[i];
			}
		}
		res.updateDegree();
		
		
		return res;
	}
	
	// XOR several polynomials
	public Polynomial add(Polynomial... args){
		Polynomial res = new Polynomial(this);
		for(Polynomial p : args){
			res = res.add(p);
		}
		
		return res;
	}
	
	// multily 2 polynomials.
	// the result is not necessary in GF(2^8)
	public Polynomial mult(Polynomial p){
		Polynomial res = new Polynomial();
		for(int i=0;i<this.degree;i++){
			if(this.coeff[i]){
				res = res.add(p.shift(i));
			}
		}
		return res;
	}
	
	// multiply then divide by an irreduceable polynomial
	//result is in GF(2^8)
	public Polynomial multGF8(Polynomial p){
		Polynomial res = new Polynomial();
		for(int i=0;i<this.degree;i++){
			if(this.coeff[i]){
				res = res.add(p.shift(i));
			}
		}
		return res.divide(RED);
	}
	
	// modify a coeff of the polynomial
	// also update the degree if necessary
	public Polynomial addCoeff(int coeff_index){
		Polynomial res = new Polynomial(this);
		res.coeff[coeff_index] = res.coeff[coeff_index] ? false : true;

		//update degree
		int i = res.coeff.length -1;
		while(!res.coeff[i]){
			i --;
		}
		res.degree = i;
		
		return res;
	}
	
	// shift the polynomial by n
	public Polynomial shift(int n){
		
		Polynomial res = new Polynomial(this.degree + n);
		for(int i = 0; i<n;i++){
			res.coeff[i] = false;
		}
		for(int i = n; i<res.degree;i++){
			res.coeff[i] = this.coeff[i-n];
		}		
		return res;
	}
	
	
	// returns a string corresponding to the polynomial
	public String toString(){
		String res = "[";
		for(int i=0;i<degree;i++){
			res += (coeff[i] ? "1; " : "0; ");
		}
		return res+"]";
	}
	
	// divide this with p
	// return the remaining
	public Polynomial divide(Polynomial p){

		if(this.degree < p.degree){
			return new Polynomial(this);
		}
		else if(this.degree == p.degree){
			return this.add(p);
		}
		else{
			Polynomial res =  new Polynomial(this);
			int diff;
			
			while(res.degree > p.degree){
				diff = res.degree - p.degree;
				res = res.add(p.shift(diff));
			}
			
			
			if(res.degree == p.degree){
				res = res.add(p);
			}
			
			return res;
		}
	}
	
}
