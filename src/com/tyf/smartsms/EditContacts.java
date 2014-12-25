package com.tyf.smartsms;

public class EditContacts {
	private String name;
	private String number;
	private String neckName;

	public EditContacts(String na, String nu, String neck){
		name = na;
		number = nu;
		neckName = neck;
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
	
	public void setNeckName(String neck){
		neckName = neck;
	}
}
