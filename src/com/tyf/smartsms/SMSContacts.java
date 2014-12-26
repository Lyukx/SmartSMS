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

public class SMSContacts extends Activity { //SMSContacts用于从联系人列表中选择收信人

    private ListView listView;
    private Button commit;
    private List<MyContacts> myContactsList = new ArrayList<MyContacts>();
    private boolean[] choose;
    ContactsAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Map<String,String[]> myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //使用自定义的标题栏需要屏蔽掉系统的标题栏
        setContentView(R.layout.smscontacts);
        initContacts(); //初始化联系人列表
        adapter = new ContactsAdapter(SMSContacts.this,R.layout.my_contacts_list,myContactsList); //获取自定义适配器的实例
        listView = (ListView)findViewById(R.id.my_listView);
        listView.setAdapter(adapter); //将自定义适配器的实例传入到listView中
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){ //listView的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choose[position] = myContactsList.get(position).ChangeCheck(); //更改点击子项实例的选中状态
                adapter.notifyDataSetChanged(); //刷新listView
            }
        });

        commit = (Button)findViewById(R.id.my_commit); //获取确定按钮的实例
        commit.setOnClickListener(new OnClickListener() { //确定按钮的点击事件

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                myMap = new HashMap<String, String[]>(); //HashMap用来存放电话号码对应的联系人姓名和昵称
                for(int i = 0; i < choose.length; i++){
                    if(choose[i]){
                        //电话号码作为键，String数组作为值。String数组中存放了联系人姓名和昵称
                        myMap.put(myContactsList.get(i).getNumber(), new String[] {myContactsList.get(i).getNeckName(),myContactsList.get(i).getName()});
                    }
                }
                SerializableMap tmpmap=new SerializableMap(); //生成SerializableMap类的实例
                Bundle bundle = new Bundle();
                tmpmap.setMap(myMap);  //将HashMap封装到SerializableMap中
                bundle.putSerializable("mymap", tmpmap); //使用Bundle类将封装了HashMap的SerializableMap返回到上一级Activity中
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initContacts(){ //initContacts用于初始化联系人列表
        dbHelper = new MyDatabaseHelper(this, "MyContacts.db", null, 2);
        db = dbHelper.getWritableDatabase(); //获取可用的数据库
        Cursor cursor1 = null;
        Cursor cursor2 = null;
        MyContacts temp;
        try{
            cursor1 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, "sort_key"); //读取系统联系人列表
            while(cursor1.moveToNext()){ //循环更新本地联系人列表
                String displayName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                cursor2 = db.query("neckname", null, "phoneNumber = ?", new String[] {number}, null, null, null); //查找系统联系人的电话号码是否在本地数据库中
                if(cursor2.moveToFirst()){ //存在则从本地数据库中获取联系人的昵称
                    String neckName = cursor2.getString(cursor2.getColumnIndex("neckname"));
                    temp = new MyContacts(displayName, number, neckName);
                }
                else{ //不存在则昵称默认为联系人姓名
                    temp = new MyContacts(displayName, number, displayName);
                }
                myContactsList.add(temp);
            }
            choose = new boolean[myContactsList.size()];
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
