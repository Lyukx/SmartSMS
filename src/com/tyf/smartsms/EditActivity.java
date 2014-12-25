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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class EditActivity extends Activity {

	private ListView listView;
    //private ArrayList<String> numberQuery = new ArrayList<String>();
    //private ArrayList<String> nameQuery = new ArrayList<String>();
    private List<EditContacts> editContactsList = new ArrayList<EditContacts>();
    private EditAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db; 
    EditText editText;
    String tempNumber;
    EditContacts temp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
        initContacts(/*db*/);
        adapter = new EditAdapter(EditActivity.this, R.layout.edit_contacts_list, editContactsList);
        listView = (ListView)findViewById(R.id.edit_listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				temp = editContactsList.get(position);
				tempNumber = temp.getNumber();
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditActivity.this);
				dialogBuilder.setTitle("neckname");
				dialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
				//EditText editText = new EditText(EditActivity.this);
				editText = new EditText(EditActivity.this);
				editText.setText(temp.getNeckName());
				dialogBuilder.setView(editText);
				dialogBuilder.setNegativeButton("取消", null);
				dialogBuilder.setPositiveButton("确定", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dbHelper = new MyDatabaseHelper(EditActivity.this, "MyContacts.db", null, 2);
				        db = dbHelper.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put("neckname", editText.getText().toString());
						db.update("neckname", values, "phoneNumber = ?", new String[] {tempNumber});
						temp.setNeckName(editText.getText().toString());
						adapter.notifyDataSetChanged();
						Log.d("EditActivity","End save");
					}
					
				});
				AlertDialog alertDialog = dialogBuilder.create();
				//alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				alertDialog.show();
				//adapter.notifyDataSetChanged();
				Log.d("EditActivity","end list");
			}
        });
	}
	
	private void initContacts(/*SQLiteDatabase db*/){
		dbHelper = new MyDatabaseHelper(this, "MyContacts.db", null, 2);
        db = dbHelper.getWritableDatabase();
    	Cursor cursor1 = null;
    	Cursor cursor2 = null;
        try{
        	//String sortOrder = ContactColumn.NAME + " COLLATE LOCALIZED ASC"; 
            cursor1 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, "sort_key");
            while(cursor1.moveToNext()){
            	EditContacts temp;
                String displayName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                cursor2 = db.query("neckname", null, "phoneNumber = ?", new String[] {number}, null, null, null);
                if(cursor2.moveToFirst()){
                	String neckName = cursor2.getString(cursor2.getColumnIndex("neckname"));
                	temp = new EditContacts(displayName, number, neckName);
                }
                else{
                	ContentValues values = new ContentValues();
                	values.put("phoneNumber", number);
                	values.put("neckname", displayName);
                	db.insert("neckname", null, values);
                	temp = new EditContacts(displayName, number, displayName);
                }                
                editContactsList.add(temp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor1 != null){
                cursor1.close();
            }
            if(cursor2 != null){
                cursor2.close();
            }
        }
    }
}
