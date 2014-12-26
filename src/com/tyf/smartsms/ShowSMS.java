package com.tyf.smartsms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Lkx on 2014/12/22.
 */
public class ShowSMS extends Activity {

    private List<Message> msgList = new ArrayList<Message>();
    private ArrayList<String> number = new ArrayList<String>();
    private ArrayList<String> address = new ArrayList<String>();
    private String name;
    private Button send = null;
    private EditText msg = null;
    private ListView listView;

    private IntentFilter sendFilter;
    private SendStatusReceiver sendStatusReceiver;
    private String[] longListMenu = new String[] {"Delete message", "Copy to clipboard"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_sms);
        //Get the number and person name from the former activity.
        Intent intent = getIntent();
        name = intent.getStringExtra("personName");
        readNumbers(name);
        //Load the messages with the contact in the sms database.
        getSMS(name, number);

        final MessageAdapter adapter = new MessageAdapter(ShowSMS.this, R.layout.message_item, msgList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        //
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Message thisMessage = msgList.get(position);    //Get the current message

                AlertDialog.Builder dialog = new AlertDialog.Builder(ShowSMS.this);
                dialog.setTitle("You are going to");
                dialog.setCancelable(true);
                dialog.setItems(longListMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                //Toast.makeText(ShowSMS.this, "Applying deleting message", Toast.LENGTH_SHORT).show();
                                deleteMsg(thisMessage);
                                break;
                            case 1:
                                //Copy the message text to the clipboard
                                ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                cbm.setText(thisMessage.getContent().toString());
                                Toast.makeText(ShowSMS.this, "The message has been copied to the clipboard", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        //Register the filter and broad cast to check sms sending status
        sendFilter = new IntentFilter();
        sendFilter.addAction("SENT_SMS_ACTION");
        sendStatusReceiver = new SendStatusReceiver();
        registerReceiver(sendStatusReceiver, sendFilter);

        final String[] numbers;
        if(address.size() == 0){
            numbers = new String[] { name };
        }
        else {
            numbers = new String[address.size()];
            for (int i = 0; i < address.size(); i++) {
                numbers[i] = address.get(i);
            }
        }

        send = (Button) findViewById(R.id.send);
        msg = (EditText) findViewById(R.id.msg);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ShowSMS.this);
                dialog.setTitle("Select the number send to");
                //dialog.setMessage("ss");
                dialog.setCancelable(true);
                dialog.setItems(numbers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SmsManager smsManager = SmsManager.getDefault();
                        Intent sentIntent = new Intent("SENT_SMS_ACTION");
                        PendingIntent pi = PendingIntent.getBroadcast(ShowSMS.this, 0, sentIntent, 0);
                        smsManager.sendTextMessage(numbers[which], null, msg.getText().toString(), pi, null);
                        dialog.dismiss();
                        msg.setText("");
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(sendStatusReceiver);
    }

    public void getSMS(String name, ArrayList<String> number){
        msgList.clear();
        final String SMS_URI_ALL = "content://sms/";

        Uri uri = Uri.parse(SMS_URI_ALL);
        String[] projection = new String[] { "_id", "body", "date", "type", "read", "status" };
        String selection = "address = ?";
        String[] selectionArgs = new String[number.size()];
        selectionArgs[0] = number.get(0);

        for(int i = 1; i< number.size(); i++){
            selection += " or address = ?";
            selectionArgs[i] = number.get(i);
        }
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, "date asc");

        if(cursor.moveToFirst()){
            int indexBody = cursor.getColumnIndex("body");
            int indexDate = cursor.getColumnIndex("date");
            int indexType = cursor.getColumnIndex("type");
            int indexId = cursor.getColumnIndex("_id");

            do{
                String strBody = cursor.getString(indexBody);

                long longDate = cursor.getLong(indexDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date d = new Date(longDate);
                String strDate = dateFormat.format(d);

                int type = cursor.getInt(indexType);

                int _id = cursor.getInt(indexId);

                Message newMsg = new Message( (type==1), strBody);
                newMsg.setId(_id);
                newMsg.setTime(strDate);
                msgList.add(newMsg);
            }while(cursor.moveToNext());
        }

        cursor.close();
    }

    private void readNumbers(String personName){
        Cursor cursor = null;
        try{
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?", new String[]{ personName }, null);
            number.add(personName);
            number.add("+86" + personName);
            while (cursor.moveToNext()) {
                String currentNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                number.add(currentNumber);
                number.add("+86" + currentNumber);
                address.add(currentNumber);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null)
                cursor.close();
        }
    }

    class SendStatusReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            if(getResultCode() == RESULT_OK){
                Toast.makeText(context, "Send succeeded", Toast.LENGTH_LONG).show();
                refreshList();
            }
            else{
                Toast.makeText(context, "Send failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void deleteMsg(Message msg){
        Uri sms = Uri.parse("content://sms/");
        getContentResolver().delete(sms, "_id = ?", new String[]{Integer.toString(msg.getId())});
        refreshList();
        Toast.makeText(ShowSMS.this, "The message has been deleted", Toast.LENGTH_LONG).show();
    }

    private void refreshList(){
        getSMS(name, number);
        final MessageAdapter adapter = new MessageAdapter(ShowSMS.this, R.layout.message_item, msgList);
        listView.setAdapter(adapter);
    }
}