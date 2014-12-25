package com.tyf.smartsms;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


public class NewSMS extends Activity {
	
	private EditText to;
    private EditText msg;
    private Button send;
    private Button contacts;
    private Map<String,String[]> myMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_sms);
        
        to = (EditText) findViewById(R.id.to);
        msg = (EditText) findViewById(R.id.msg);
        send = (Button) findViewById(R.id.send);
        contacts = (Button) findViewById(R.id.contacts);
        //edit = (Button) findViewById(R.id.edit);
        
        contacts.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(NewSMS.this, SMSContacts.class);
            	startActivityForResult(intent,1);
            }
        });
        
//        edit.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(MainActivity.this, EditActivity.class);
//            	startActivity(intent);
//			}
//		});
        
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                String list = to.getText().toString();
        		String[] listMember = list.split(";");
                for(int i = 0; i< listMember.length; i++){
               		String rex="[()]+";
               		String[] str=listMember[i].split(rex);
                	String number = str[1];
                	//Log.d("MainActivity",number);
                	String msgReplaced = msg.getText().toString().replace("[name]", myMap.get(number)[0]);
                	smsManager.sendTextMessage(number, null, msgReplaced, null, null);
                	//Log.d("MainActivity","send");
                	//Log.d("MainActivity",myMap.get(number)[0]);
                	//Log.d("MainActivity",number);
                }
                msg.setText("");
                to.setText("");
            }
        });
        
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 1:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();  
		        SerializableMap serializableMap = (SerializableMap) bundle.get("mymap");  
		        myMap = serializableMap.getMap();
				//nameQuery = data.getStringArrayListExtra("NameList");
				//numberQuery = data.getStringArrayListExtra("NumberList");
//				int i;
		        for(Map.Entry<String, String[]> entry : myMap.entrySet()){
		        	to.setText(to.getText() + entry.getValue()[1] + "(" + entry.getKey() + ");");
		        }
//				for(i = 0; i< nameQuery.size()-1; i++){
//					to.setText(to.getText() + nameQuery.get(i) + "(" + numberQuery.get(i) + ");");
//				}
//				to.setText(to.getText() + nameQuery.get(i) + "(" + numberQuery.get(i) + ")");
			}
		}
	}
    
    
    
}