package com.tyf.smartsms;

public class MyContacts {
	private String name;
	private String number;
	private boolean check;
	private String neckName;

	public MyContacts(String na, String nu, String neck){
		name = na;
		number = nu;
		neckName = neck;
		check = false;
	}
	
	public String getName(){
		return name;
	}
	
	public String getNumber(){
		return number;
	}
	
	public String getNeckName(){
		return neckName;
	}
	
	public boolean getCheck(){
		return check;
	}
	
	public boolean ChangeCheck(){
		if(check == false)
			check = true;
		else
			check = false;
		return check;
	}
}
