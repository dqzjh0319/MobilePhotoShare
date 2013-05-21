package polyu.comp.mps.activity;

import polyu.comp.mps.R;
import polyu.comp.mps.util.JsonUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private String url;
	private String userName;
	private String userPsw;
	private String showName;
	private EditText txtRegPassword;
	private EditText txtRegUserName;
	private EditText txtRegConfirmPsw;
	private EditText txtRegNickName;
	private TextView tvRegUserName;
	private TextView tvRegPassword;
	private TextView tvRegConfirmPsw;
	private TextView tvRegNickName;
	private Button btn_register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()     
    		.detectDiskReads()     
    		.detectDiskWrites()     
    		.detectNetwork()
    		.penaltyLog()     
    		.build());     
  
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()     
    		.detectLeakedSqlLiteObjects()     
    	  //.detectLeakedClosableObjects()     
    		.penaltyLog()     
    		.penaltyDeath()     
    		.build());  
		
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		setContentView(R.layout.register_activity_layerout);
		initView();
		setListener();
	}
	
	private void initView() {
		tvRegUserName = (TextView) findViewById(R.id.tvRegUserName);
		tvRegPassword = (TextView) findViewById(R.id.tvRegPassword);
		tvRegConfirmPsw = (TextView) findViewById(R.id.tvRegConfirmPsw);
		tvRegNickName = (TextView) findViewById(R.id.tvRegNickName);
		txtRegPassword = (EditText) findViewById(R.id.txtRegPassword);
		txtRegUserName = (EditText) findViewById(R.id.txtRegUserName);
		txtRegConfirmPsw = (EditText) findViewById(R.id.txtRegConfirmPsw);
		txtRegNickName = (EditText) findViewById(R.id.txtRegNickName);
		btn_register = (Button) findViewById(R.id.btn_register);
	}
	
	private void setListener() {
		
		
		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userName = txtRegUserName.getText().toString();
				userPsw = txtRegPassword.getText().toString();
				showName = txtRegNickName.getText().toString();
				if(!userPsw.endsWith(txtRegConfirmPsw.getText().toString())) {
					Toast.makeText(getApplicationContext(), "Confirmation password is different with orginal password",Toast.LENGTH_SHORT).show();
				}
				else if(userName == null || userPsw == null || showName == null) {
					Toast.makeText(getApplicationContext(), "Please fill in the blank",Toast.LENGTH_SHORT).show();
				}
				else {
					register();
				}
			}
		});
	}
	
	public boolean register() {
		
		url = "http://158.132.11.225:8080/MPServer/userAction!registerUser?user.userName="+userName+"&user.userPsw="+userPsw+"&user.showName="+showName;	
		if(JsonUtil.getJson(url) == true) {
			Toast.makeText(getApplicationContext(), "Success",Toast.LENGTH_SHORT).show();
			return true;
		}  
		else {
			Toast.makeText(getApplicationContext(), "Failed",Toast.LENGTH_SHORT).show(); 
			return false;
		}	 
	}
}
