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
	private String[] menuString = new String[] {"Back Up SMS Data", "Recover SMS Data"}; //点击menu按钮后弹出的对话框中显示的选项

	public TitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.title, this); //动态加载布局文件
		mContext = context; //保存上下文
		//获取控件实例
		Button titleEdit = (Button) findViewById(R.id.edit);
		Button titleNew = (Button) findViewById(R.id.newsms);
		Button titleMenu = (Button) findViewById(R.id.menu);

		titleNew.setOnClickListener(new OnClickListener() { //为按钮newsms添加点击监听器
			@Override
			public void onClick(View v) {
				//当点击按钮newsms时，启动新的Activity NewSMS用来新建一条短信
				Intent intent = new Intent(mContext,NewSMS.class);
				mContext.startActivity(intent);
			}
		});

		titleEdit.setOnClickListener(new OnClickListener() { //为按钮edit添加点击监听器
			@Override
			public void onClick(View v) {
				//当点击按钮edit时，启动新的Activity EditActivity用来编辑联系人
				Intent intent = new Intent(mContext,EditActivity.class);
				mContext.startActivity(intent);
			}
		});

		titleMenu.setOnClickListener(new OnClickListener() { //为按钮menu添加点击监听器
			@Override
			public void onClick(View view) {
				//当点击按钮menu时，弹出对话框让用户选择要进行的操作—备份短信和恢复短信
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
				dialog.setTitle("You are going to");
				dialog.setCancelable(true);
				dialog.setItems(menuString, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
							case 0: //如果用户选择"Back Up SMS Data"，则调用backUp函数备份短信
								backUp();
								break;
							case 1: //如果用户选择"Recover SMS Data"，则调用recovery函数备份短信
								recovery();
								break;
							default:
								break;
						}
						dialog.dismiss(); //执行完用户选择的任务后关闭对话框
					}
				});

				dialog.show();
			}
		});

	}

	public void backUp() {
		//备份操作属于耗时操作，弹出ProgressDialog提示用户等待
		final ProgressDialog processDialog = new ProgressDialog(mContext);
		processDialog.setTitle("applying back up");
		processDialog.setMessage("Waiting...");
		processDialog.setCancelable(true);
		processDialog.show();
		//Backup
		final Handler handler = new Handler(){
			public void handleMessage(Message msg){
				processDialog.dismiss();   //执行备份的线程返回信息后关闭ProgressDialog
				switch(msg.arg1){
					case 1: //如果返回信息为1则提示用户备份成功
						Toast.makeText(mContext, "back up success", Toast.LENGTH_LONG).show();
						break;
					case 0: //如果返回信息为0则提示用户备份失败
						Toast.makeText(mContext, "back up failed", Toast.LENGTH_LONG).show();
						break;
				}
			}
		};
		Thread threadBackup = new Thread() { //新建线程，在线程中进行备份操作
			public void run() {
				ExportSMSDB export = new ExportSMSDB(mContext); //生成ExportSMSDB类的实例
				try {
					export.createTable(); //将短信备份到本地数据库
					Message msg = new Message();
					msg.arg1 = 1;
					handler.sendMessage(msg); //如果备份没有抛出错误，则返回一条信息表示备份成功
				} catch (Exception e) {
					Message msg = new Message();
					msg.arg1 = 0;
					handler.sendMessage(msg); //如果备份抛出了错误，则返回一条信息表示备份失败
					e.printStackTrace();
				}
//				Log.d("Main","over");
			}
		};
		threadBackup.start();
	}

	public void recovery() {
		//恢复操作属于耗时操作，弹出ProgressDialog提示用户等待
		final ProgressDialog processDialog = new ProgressDialog(mContext);
		processDialog.setTitle("Applying recovery");
		processDialog.setMessage("Waiting...");
		processDialog.setCancelable(true);
		processDialog.show();

		//recover
		final Handler handler = new Handler(){
			public void handleMessage(Message msg){
				processDialog.dismiss();   //执行恢复的线程返回信息后关闭ProgressDialog
				switch(msg.arg1){
					case 1: //如果返回信息为1则提示用户恢复成功
						Toast.makeText(mContext, "Recover success", Toast.LENGTH_LONG).show();
						break;
					case 0: //如果返回信息为0则提示用户恢复失败
						Toast.makeText(mContext, "Recover failed", Toast.LENGTH_LONG).show();
						break;
				}
			}
		};
		Thread threadRecover = new Thread() { //新建线程，在线程中进行恢复操作
			public void run() {
				ImportSMS importSms = new ImportSMS(mContext); //生成ImportSMS类的实例
				try {
					importSms.testInsertSMS(); //从本地数据库恢复短信
					Message msg = new Message();
					msg.arg1 = 1;
					handler.sendMessage(msg); //如果恢复没有抛出错误，则返回一条信息表示恢复成功
				} catch (Exception e) {
					Message msg = new Message();
					msg.arg1 = 0;
					handler.sendMessage(msg); //如果恢复抛出了错误，则返回一条信息表示恢复失败
					e.printStackTrace();
				}
			}
		};
		threadRecover.start();
	}

}