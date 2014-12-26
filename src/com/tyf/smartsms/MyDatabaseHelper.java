package com.tyf.smartsms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper{

	private Context mContext;
	public static final String CREATE_NECKNAME = "create table neckname (phoneNumber TEXT, neckname TEXT)"; //表neckname存放号码对应的昵称
	public static final String CREATE_BACKUP = "create table smsbackup (" //表smsbackup存放从系统短信数据库中读取到的数据
			+ "address TEXT, "
			+ "person TEXT, "
			+ "date TEXT, "
			+ "protocol TEXT, "
			+ "read TEXT, "
			+ "type TEXT, "
			+ "body TEXT)";

	public MyDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context; //保存上下文
	}

	@Override
	public void onCreate(SQLiteDatabase db){ //第一次使用数据库时创建两个表并提示初始化成功
		db.execSQL(CREATE_NECKNAME);
		db.execSQL(CREATE_BACKUP);
		Toast.makeText(mContext, "Initial Success", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch(oldVersion){ //如果一开始应用使用的是开发版则，更新为正式版后只需要新建一个表即可
			case 1:
				db.execSQL(CREATE_BACKUP);
				Toast.makeText(mContext, "Update Success", Toast.LENGTH_LONG).show();
		}

	}

}
