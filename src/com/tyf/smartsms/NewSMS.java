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


public class NewSMS extends Activity { //NewSMS用于发送新短信

    private EditText to;
    private EditText msg;
    private Button send;
    private Button contacts;
    private Map<String,String[]> myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //使用自定义的标题栏需要屏蔽掉系统的标题栏
        setContentView(R.layout.new_sms);

        //获取控件实例
        to = (EditText) findViewById(R.id.to);
        msg = (EditText) findViewById(R.id.msg);
        send = (Button) findViewById(R.id.send);
        contacts = (Button) findViewById(R.id.contacts);

        contacts.setOnClickListener(new OnClickListener() { //为contacts按钮绑定点击监听器
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewSMS.this, SMSContacts.class);
                startActivityForResult(intent,1); //使用startActivityForResult的方式启动Activity获取返回数据
            }
        });

        send.setOnClickListener(new OnClickListener() { //为send按钮绑定点击监听器
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault(); //获取SmsManager的实例
                String list = to.getText().toString(); //获取待发送列表
                String[] listMember = list.split(";"); //获取需要发送的人数
                for(int i = 0; i< listMember.length; i++){
                    //获取发送的号码
                    String rex="[()]+";
                    String[] str=listMember[i].split(rex);
                    String number = str[1];
                    String msgReplaced = msg.getText().toString().replace("[name]", myMap.get(number)[0]); //如果短信内容中含有[name]则将其替换为联系人的昵称
                    smsManager.sendTextMessage(number, null, msgReplaced, null, null); //调用SmsManager的sendTextMessage的方法发送短信
                }
                //发送完成后清空收信人和信息内容文本编辑框
                msg.setText("");
                to.setText("");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1: //如果requestCode为1说明返回的是收信人列表
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras(); //获取Bundle类的实例
                    SerializableMap serializableMap = (SerializableMap) bundle.get("mymap"); //从Bundle中取出SerializableMap
                    myMap = serializableMap.getMap(); //从SerializableMap中取出存放有收信人号码和姓名、昵称的HashMap
                    for(Map.Entry<String, String[]> entry : myMap.entrySet()){
                        to.setText(to.getText() + entry.getValue()[1] + "(" + entry.getKey() + ");"); //显示收信人列表
                    }
                }
        }
    }



}