package com.tyf.smartsms;

public class MyContacts { //MyContacts类用于保存联系人信息
	private String name;
	private String number;
	private boolean check; //标记联系人是否被选中
	private String neckName; //联系人昵称

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

	public void setNeckName(String neck){
		neckName = neck;
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
