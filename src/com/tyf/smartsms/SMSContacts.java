package com.tyf.smartsms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class SMSContacts extends Activity {
	
	private ListView listView;
	private Button commit;
    //private ArrayList<String> numberQuery = new ArrayList<String>();
    //private ArrayList<String> nameQuery = new ArrayList<String>();
    private List<MyContacts> myContactsList = new ArrayList<MyContacts>();
    private boolean[] choose;
    ContactsAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Map<String,String[]> myMap;
    //private Map<String,String> nameMap;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.smscontacts);
		initContacts();
        adapter = new ContactsAdapter(SMSContacts.this,R.layout.my_contacts_list,myContactsList);
        listView = (ListView)findViewById(R.id.my_listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				choose[position] = myContactsList.get(position).ChangeCheck();
				adapter.notifyDataSetChanged();
			}
        });
        
        commit = (Button)findViewById(R.id.my_commit);
        commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				myMap = new HashMap<String, String[]>();
				for(int i = 0; i < choose.length; i++){
					if(choose[i]){
						//numberQuery.add(myContactsList.get(i).getNumber());
		                //nameQuery.add(myContactsList.get(i).getNeckName());
						myMap.put(myContactsList.get(i).getNumber(), new String[] {myContactsList.get(i).getNeckName(),myContactsList.get(i).getName()});
						//nameMap.put(myContactsList.get(i).getNumber(), myContactsList.get(i).getName());
					}	
				}
				//intent.putExtra("NameList", nameQuery);
				//intent.putExtra("NumberList", numberQuery);
				SerializableMap tmpmap=new SerializableMap(); 
				Bundle bundle = new Bundle();
				tmpmap.setMap(myMap);  
	            bundle.putSerializable("mymap", tmpmap);
	            intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	private void initContacts(){
		dbHelper = new MyDatabaseHelper(this, "MyContacts.db", null, 2);
        db = dbHelper.getWritableDatabase();
    	Cursor cursor1 = null;
    	Cursor cursor2 = null;
    	MyContacts temp;
        try{
        	//String sortOrder = ContactColumn.NAME + " COLLATE LOCALIZED ASC"; 
            cursor1 = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, "sort_key");
            while(cursor1.moveToNext()){
                String displayName = cursor1.getString(cursor1.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor1.getString(cursor1.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                cursor2 = db.query("neckname", null, "phoneNumber = ?", new String[] {number}, null, null, null);
                if(cursor2.moveToFirst()){
                	String neckName = cursor2.getString(cursor2.getColumnIndex("neckname"));
                	temp = new MyContacts(displayName, number, neckName);
                }
                else{
                	//ContentValues values = new ContentValues();
                	temp = new MyContacts(displayName, number, "��");
                }    
                //MyContacts temp = new MyContacts(displayName, number, "��");
                myContactsList.add(temp);
            }
            choose = new boolean[myContactsList.size()];
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
