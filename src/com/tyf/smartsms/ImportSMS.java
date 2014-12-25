package com.tyf.smartsms;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ImportSMS {
	private Context mcontext;
	 
    private List<SMSItem> smsItems = new ArrayList<SMSItem>();
    private ContentResolver conResolver;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db; 
 
    public ImportSMS(Context context) {
        mcontext = context;
    }
 
    public void testInsertSMS() throws Exception{
    	
    	getSmsItemsFromDB();
        conResolver = mcontext.getContentResolver();
    	Uri uri = Uri.parse("content://sms/");
        for (SMSItem item : smsItems) {
 
            Cursor cursor = conResolver.query(uri, new String[] { SMSField.DATE }, SMSField.DATE + "=?",
                    new String[] { item.getDate() }, null);
 
            if (!cursor.moveToFirst()) {// 没有该条短信
            	Log.d("sqk", item.getAddress() + " " +item.getPerson() + " " + item.getDate());
                ContentValues values = new ContentValues();
                values.put(SMSField.ADDRESS, item.getAddress());
                // 如果是空字符串说明原来的值是null，所以这里还原为null存入数据库
                values.put(SMSField.PERSON, item.getPerson().equals("") ? null : item.getPerson());
                values.put(SMSField.DATE, item.getDate());
                values.put(SMSField.PROTOCOL, item.getProtocol().equals("") ? null : item.getProtocol());
                values.put(SMSField.READ, item.getRead());
                values.put(SMSField.TYPE, item.getType());
                values.put(SMSField.BODY, item.getBody());
                conResolver.insert(uri, values);
            }
            cursor.close();
        }
    }
 
    public List<SMSItem> getSmsItemsFromDB() throws Exception{
 
        SMSItem smsItem = null;
        dbHelper = new MyDatabaseHelper(mcontext, "MyContacts.db", null, 2);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        cursor = db.query("smsbackup", null, null, null, null, null, null);
        while(cursor.moveToNext()){
        	
        	smsItem = new SMSItem();
        	smsItem.setAddress(cursor.getString(cursor.getColumnIndex("address")));
        	smsItem.setPerson(cursor.getString(cursor.getColumnIndex("person")));
        	smsItem.setDate(cursor.getString(cursor.getColumnIndex("date")));
        	smsItem.setProtocol(cursor.getString(cursor.getColumnIndex("protocol")));
        	smsItem.setRead(cursor.getString(cursor.getColumnIndex("read")));
        	smsItem.setType(cursor.getString(cursor.getColumnIndex("type")));
        	smsItem.setBody(cursor.getString(cursor.getColumnIndex("body")));
        	smsItems.add(smsItem);
        	smsItem = null;
        	}
        if(cursor != null){
        	cursor.close();
        }
        return smsItems;
    }
}
