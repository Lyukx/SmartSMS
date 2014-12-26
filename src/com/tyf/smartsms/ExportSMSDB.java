package com.tyf.smartsms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ExportSMSDB{ //ExportSMSDB类用于将短信备份到本地数据库
	Context mcontext;
	public static final String SMS_URI_ALL = "content://sms/"; //系统短信数据库的uri
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase db;

	public ExportSMSDB(Context context){
		mcontext = context; //保存上下文
	}

	public boolean createTable() throws Exception{ //在本地数据库中创建备份。抛出异常用来指示备份过程中出现了错误

		dbHelper = new MyDatabaseHelper(mcontext, "MyContacts.db", null, 2);
		db = dbHelper.getWritableDatabase(); //获取可用的数据库
		db.delete("smsbackup", null, null); //每次备份前先删除先前备份的内容
		Cursor cursor = null;
		ContentResolver conResolver = mcontext.getContentResolver();
		String[] projection = new String[] { SMSField.ADDRESS, SMSField.PERSON, SMSField.DATE, SMSField.PROTOCOL,
				SMSField.READ, SMSField.TYPE, SMSField.BODY };
		Uri uri = Uri.parse(SMS_URI_ALL);
		cursor = conResolver.query(uri, projection, null, null, "_id asc"); //从系统的短信数据库中读取需要的字段，并按照_id正序排列

		if (cursor.moveToFirst()) { //如果度取出的内容不为空则开始备份
			String address;
			String person;
			String date;
			String protocol;
			String read;
			String type;
			String body;
			do {
				address = cursor.getString(cursor.getColumnIndex(SMSField.ADDRESS));
				if(address == null) //如果读出的内容为null则将空字符串保存到本地数据库
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
				db.insert("smsbackup", null, values); //将读出的数据插入到本地数据库的表smsbackup中
			} while (cursor.moveToNext()); //循环保存下一条
		}
		else {
			return false;
		}
		if(cursor != null) {
			cursor.close();//关闭cursor释放资源
		}
		return true;
	}
}