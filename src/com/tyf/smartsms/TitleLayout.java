package com.tyf.smartsms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TitleLayout extends LinearLayout {
	
	private Context mContext;
	private String[] menuString = new String[] {"Back Up SMS Data", "Recover SMS Data"};
	
	public TitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		LayoutInflater.from(context).inflate(R.layout.title, this);
		mContext = context;
//		Button titleBackup = (Button) findViewById(R.id.backups);
//		Button titleRecover = (Button) findViewById(R.id.recover);
		Button titleEdit = (Button) findViewById(R.id.edit);
		Button titleNew = (Button) findViewById(R.id.newsms);
		Button titleMenu = (Button) findViewById(R.id.menu);

		titleNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext,NewSMS.class);
				mContext.startActivity(intent);
			}	
		});
		
		titleEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext,EditActivity.class);
				mContext.startActivity(intent);
			}	
		});

		titleMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
				dialog.setTitle("You are going to");
				dialog.setCancelable(true);
				dialog.setItems(menuString, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
							case 0:
								backUp();
								break;
							case 1:
								recovery();
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

	}

	public void backUp() {
		// TODO Auto-generated method stub
		final ProgressDialog processDialog = new ProgressDialog(mContext);
		processDialog.setTitle("applying back up");
		processDialog.setMessage("Waiting...");
		processDialog.setCancelable(true);
		processDialog.show();
		//Backup
		final Handler handler = new Handler(){
			public void handleMessage(Message msg){
				processDialog.dismiss();   //�ر�progressdialog
				switch(msg.arg1){
					case 1:
						Toast.makeText(mContext, "back up success", Toast.LENGTH_LONG).show();
						break;
					case 0:
						Toast.makeText(mContext, "back up failed", Toast.LENGTH_LONG).show();
						break;
				}
			}
		};
		Thread threadBackup = new Thread() {
			public void run() {
				ExportSMSDB export = new ExportSMSDB(mContext);
				try {
					export.createXml();
					Message msg = new Message();
					msg.arg1 = 1;
					handler.sendMessage(msg);
				} catch (Exception e) {
					Message msg = new Message();
					msg.arg1 = 0;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
				Log.d("Main","over");
			}
		};
		threadBackup.start();
	}

	public void recovery() {
		// TODO Auto-generated method stub
		final ProgressDialog processDialog = new ProgressDialog(mContext);
		processDialog.setTitle("Applying recovery");
		processDialog.setMessage("Waiting...");
		processDialog.setCancelable(true);
		processDialog.show();

		//recover
		final Handler handler = new Handler(){
			public void handleMessage(Message msg){
				processDialog.dismiss();   //�ر�progressdialog
				switch(msg.arg1){
					case 1:
						Toast.makeText(mContext, "Recover success", Toast.LENGTH_LONG).show();
						break;
					case 0:
						Toast.makeText(mContext, "Recover failed", Toast.LENGTH_LONG).show();
						break;
				}
			}
		};
		Thread threadRecover = new Thread() {
			public void run() {
				ImportSMS importSms = new ImportSMS(mContext);
				try {
					importSms.testInsertSMS();
					Message msg = new Message();
					msg.arg1 = 1;
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Message msg = new Message();
					msg.arg1 = 0;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		};
		threadRecover.start();
	}

}