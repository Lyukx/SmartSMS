package com.tyf.smartsms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ExportSMSDB{
	Context mcontext;
    public static final String SMS_URI_ALL = "content://sms/";
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db; 
 
    public ExportSMSDB(Context context){
        mcontext = context;
    }
 
    public boolean createXml() throws Exception{
 
    	dbHelper = new MyDatabaseHelper(mcontext, "MyContacts.db", null, 2);
        db = dbHelper.getWritableDatabase();
        db.delete("smsbackup", null, null);
        Cursor cursor = null;
        ContentResolver conResolver = mcontext.getContentResolver();
        String[] projection = new String[] { SMSField.ADDRESS, SMSField.PERSON, SMSField.DATE, SMSField.PROTOCOL,
                                             SMSField.READ, SMSField.TYPE, SMSField.BODY };
        Uri uri = Uri.parse(SMS_URI_ALL);
        cursor = conResolver.query(uri, projection, null, null, "_id asc");
        
        if (cursor.moveToFirst()) {
        	String address;
        	String person;
        	String date;
        	String protocol;
        	String read;
        	String type;
        	String body;
        	do {
        		address = cursor.getString(cursor.getColumnIndex(SMSField.ADDRESS));
        		if(address == null)
        			address = "";
        		person = cursor.getString(cursor.getColumnIndex(SMSField.PERSON));
        		if(person == null)
        			person = "";
        		date = cursor.getString(cursor.getColumnIndex(SMSField.DATE));
        		if(date == null)
        			date = "";
        		protocol = cursor.getString(cursor.getColumnIndex(SMSField.PROTOCOL));
        		if(protocol == null)
        			protocol = "";
        		read = cursor.getString(cursor.getColumnIndex(SMSField.READ));
        		if(read == null)
        			read = "";
        		type = cursor.getString(cursor.getColumnIndex(SMSField.TYPE));
        		if(type == null)
        			type = "";
        		body = cursor.getString(cursor.getColumnIndex(SMSField.BODY));
        		if(body == null)
        			body = "";
        		ContentValues values = new ContentValues();
        		values.put(SMSField.ADDRESS, address);
        		values.put(SMSField.PERSON, person);
        		values.put(SMSField.DATE, date);
        		values.put(SMSField.PROTOCOL, protocol);
        		values.put(SMSField.READ, read);
        		values.put(SMSField.TYPE, type);
        		values.put(SMSField.BODY, body);
        		db.insert("smsbackup", null, values);
        	} while (cursor.moveToNext());	
        } 
        else {
        	return false;
        }
        if(cursor != null) {
        	cursor.close();//手动关闭cursor，及时回收
        }
        return true;
    }
}