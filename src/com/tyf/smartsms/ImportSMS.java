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

public class ImportSMS { //ImportSMS类用于从本地数据库恢复短信
    private Context mcontext;

    private List<SMSItem> smsItems = new ArrayList<SMSItem>(); //用于保存从本地数据库中读取出的内容
    private ContentResolver conResolver;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public ImportSMS(Context context) {
        mcontext = context; //保存上下文
    }

    public void testInsertSMS() throws Exception{ //使用本地数据库恢复短信。抛出异常用来指示恢复过程中出现了错误

        getSmsItemsFromDB(); //从本地数据库中读取短信内容
        conResolver = mcontext.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        for (SMSItem item : smsItems) {
            //对于从本地数据库中读取到的每一条短信内容，查找该短信的收发时间是否在系统短信数据库中。因为时间是以毫秒为单位存放的，因此两条短信的时间相同则认为其为同一条短信
            Cursor cursor = conResolver.query(uri, new String[] { SMSField.DATE }, SMSField.DATE + "=?",
                    new String[] { item.getDate() }, null);

            if (!cursor.moveToFirst()) { //如果cursor为空，则说明系统短信数据库中没有这条短信
//            	Log.d("sqk", item.getAddress() + " " +item.getPerson() + " " + item.getDate());
                ContentValues values = new ContentValues();
                values.put(SMSField.ADDRESS, item.getAddress());
                // 如果字符串为空字符串，说明本来读出的值是null，则应该将null存入系统数据库
                values.put(SMSField.PERSON, item.getPerson().equals("") ? null : item.getPerson());
                values.put(SMSField.DATE, item.getDate());
                values.put(SMSField.PROTOCOL, item.getProtocol().equals("") ? null : item.getProtocol());
                values.put(SMSField.READ, item.getRead());
                values.put(SMSField.TYPE, item.getType());
                values.put(SMSField.BODY, item.getBody());
                conResolver.insert(uri, values); //将读出的数据插入到系统短信数据库中
            }
            cursor.close(); //关闭cursor释放资源
        }
    }

    public List<SMSItem> getSmsItemsFromDB() throws Exception{ //从本地数据库中读取短信内容并将其保存到ArrayList中。抛出异常用来指示初始化过程中出现了错误

        SMSItem smsItem = null;
        dbHelper = new MyDatabaseHelper(mcontext, "MyContacts.db", null, 2);
        db = dbHelper.getWritableDatabase(); //获取可用的数据库
        Cursor cursor = null;
        cursor = db.query("smsbackup", null, null, null, null, null, null); //从本地数据库的表smsbackup中读取所有的数据
        while(cursor.moveToNext()){ //循环读取所有的短信数据

            smsItem = new SMSItem();
            smsItem.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            smsItem.setPerson(cursor.getString(cursor.getColumnIndex("person")));
            smsItem.setDate(cursor.getString(cursor.getColumnIndex("date")));
            smsItem.setProtocol(cursor.getString(cursor.getColumnIndex("protocol")));
            smsItem.setRead(cursor.getString(cursor.getColumnIndex("read")));
            smsItem.setType(cursor.getString(cursor.getColumnIndex("type")));
            smsItem.setBody(cursor.getString(cursor.getColumnIndex("body")));
            smsItems.add(smsItem); //将smsItem保存到ArrayList中
            smsItem = null;
        }
        if(cursor != null){
            cursor.close(); //关闭cursor释放资源
        }
        return smsItems;
    }
}
