package data_security_assignment_2;

import java.util.Arrays;

public class Polynomial {
	
	private boolean[] coeff;
	private int degree;
	
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
		this.coeff = p.coeff.clone();
	}
	
	public Polynomial(int degree, Polynomial p){
		this.degree = degree;
		this.coeff = Arrays.copyOfRange(p.coeff, 0, degree);
	}
	
	
	public Polynomial(int degree){
		this.degree = degree;
		this.coeff = new boolean[degree];
		for(int i=0;i<degree;i++){
			this.coeff[i] = false;
		}
		this.coeff[degree-1] = true;
	}
	
	public static Polynomial fromByte(int byteValue){
		Polynomial res = new Polynomial(8);
		int index = 7;
		while(index >= 0 && byteValue > 0){ // in case of a wrong input
			if(byteValue >= Math.pow(2, index)){
				byteValue -= Math.pow(2, index);
				res.coeff[index] = true;
			}
			index --;
		}
		return res;
	}
	
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
		for(int i=0; i<maxP.degree;i++){
			if(i >= minP.degree){
				res.coeff[i] = maxP.coeff[i];
			}
			else{
				res.coeff[i] = minP.coeff[i] ^ maxP.coeff[i];
			}
		}
		
		int i = res.coeff.length -1;
		while(res.coeff[i] == false){
			res.degree --;
			i --;
		}
		
		return res;
	}
	
	public Polynomial add(Polynomial... args){
		Polynomial res = new Polynomial(this);
		for(Polynomial p : args){
			res = res.add(p);
		}
		
		return res;
	}
	
	
	// /!\ DOESN'T WORK
	public Polynomial mult(Polynomial p){
		Polynomial res = new Polynomial();
		for(int i=0;i<this.degree;i++){
			if(this.coeff[i]){
				res = res.add(p.shift(i));
			}
		}
		return res;
	}
	
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
	
	
	
	public String toString(){
		String res = "[";
		for(int i=0;i<degree;i++){
			res += (coeff[i] ? "1; " : "0; ");
		}
		return res+"]";
	}
	
	// divide this with p
	public Polynomial divide(Polynomial p){
		System.out.println("divide start res = "+this.toString());
		if(this.degree < p.degree){
			return new Polynomial(this);
		}
		//useless ?
		else if(this.degree == p.degree){
			return this.add(p);
		}
		else{
			Polynomial res =  new Polynomial(this);
			int diff;
			
			while(res.degree > p.degree){
				diff = res.degree - p.degree;
				res = res.add(p.shift(diff));
				System.out.println("divide iteration res = "+res.toString());
			}
			
			
			if(res.degree == p.degree){
				res = res.add(p);
			}
			
			return res;
		}
	}
	
}
