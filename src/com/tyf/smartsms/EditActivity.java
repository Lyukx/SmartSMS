package com.tyf.smartsms;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class EditActivity extends Activity { //EditActivity用于编辑联系人列表

	private ListView listView;
	private List<MyContacts> editContactsList = new ArrayList<MyContacts>();
	private EditAdapter adapter;
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase db;
	EditText editText;
	String tempNumber;
	MyContacts temp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //使用自定义的标题栏需要屏蔽掉系统的标题栏
		setContentView(R.layout.activity_edit);
		initContacts(); //初始化联系人列表
		adapter = new EditAdapter(EditActivity.this, R.layout.my_contacts_list, editContactsList); //获取自定义适配器的实例
		listView = (ListView)findViewById(R.id.edit_listView);
		listView.setAdapter(adapter); //将自定义适配器的实例传入到listView中
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){ //listView的点击事件
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				temp = editContactsList.get(position); //获取用户点击的子项
				tempNumber = temp.getNumber();
				//弹出对话框供用户编辑昵称
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditActivity.this);
				dialogBuilder.setTitle("Set Nickname");
				dialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
				editText = new EditText(EditActivity.this);
				editText.setText(temp.getNeckName());
				editText.setSelection(editText.length()-1);
				dialogBuilder.setView(editText);
				dialogBuilder.setPositiveButton("OK", new OnClickListener(){ //用户点击确定按钮后更新昵称数据库并且刷新listView
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbHelper = new MyDatabaseHelper(EditActivity.this, "MyContacts.db", null, 2);
						db = dbHelper.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put("neckname", editText.getText().toString());
						db.update("neckname", values, "phoneNumber = ?", new String[] {tempNumber}); //将新的昵称更新到数据库
						temp.setNeckName(editText.getText().toString());
						adapter.notifyDataSetChanged();
						Log.d("EditActivity","End save");
					}

				});
				dialogBuilder.setNegativeButton("Cancel", null);
				AlertDialog alertDialog = dialogBuilder.create();
				alertDialog.show();
				Log.d("EditActivity","end list");
			}
		});
	}

	private void initContacts(){ //initContacts用于初始化联系人列表
		dbHelper = new MyDatabaseHelper(this, "MyContacts.db", null, 2);
		db = dbHelper.getWritableDatabase(); //获取可用的数据库
		Cursor cursor1 = null;
		Cursor cursor2 = null;
		try{
			cursor1 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, "sort_key"); //读取系统联系人列表
			while(cursor1.moveToNext()){ //循环更新本地联系人列表
				MyContacts temp;
				String displayName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String number = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				cursor2 = db.query("neckname", null, "phoneNumber = ?", new String[] {number}, null, null, null); //查找系统联系人的电话号码是否在本地数据库中
				if(cursor2.moveToFirst()){ //存在则从本地数据库中获取联系人的昵称
					String neckName = cursor2.getString(cursor2.getColumnIndex("neckname"));
					temp = new MyContacts(displayName, number, neckName);
				}
				else{ //不存在则在本地数据库新建一条记录，昵称默认为联系人姓名
					ContentValues values = new ContentValues();
					values.put("phoneNumber", number);
					values.put("neckname", displayName);
					db.insert("neckname", null, values);
					temp = new MyContacts(displayName, number, displayName);
				}
				editContactsList.add(temp);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			//关闭cursor释放资源
			if(cursor1 != null){
				cursor1.close();
			}
			if(cursor2 != null){
				cursor2.close();
			}
		}
	}
}
