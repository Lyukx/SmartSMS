package com.tyf.smartsms;

//The class to store one message
public class Message {
	public boolean isLeft;
    private String content;
    private int _id;
    private String time;

    public Message(boolean isLeft, String content){
        this.isLeft = isLeft;
        this.content = content;
    }

    public void setId(int _id){
        this._id = _id;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getContent(){
        return content;
    }

    public boolean getIsLeft(){
        return isLeft;
    }

    public int getId(){
        return _id;
    }

    public String getTime(){
        return time;
    }
}
