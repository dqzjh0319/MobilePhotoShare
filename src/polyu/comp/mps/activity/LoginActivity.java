package polyu.comp.mps.activity;

import polyu.comp.mps.R;
import polyu.comp.mps.MyApp;
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

public class LoginActivity extends Activity {

	
	private MyApp myApp;
	private String url;
	private EditText txtUserName;
	private EditText txtPassword;
	private TextView tvUserName;
	private TextView tvUserPsw;
	private Button regBtn;
	private Button signBtn;
	private String userName;
	private String userPsw;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		userName = "";
		userPsw = "";
		
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
		myApp = (MyApp) getApplication();
        super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		initView();
		setListener();
	}

	private void initView() {
		regBtn = (Button) findViewById(R.id.btn_register);
		signBtn = (Button) findViewById(R.id.btn_signin);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvUserPsw = (TextView) findViewById(R.id.tvUserPsw);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtUserName = (EditText) findViewById(R.id.txtUserName);
	}
	
	private void setListener() {
		
		regBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(regIntent);
			}

		});

		signBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent signToPanelIntent = new Intent(LoginActivity.this, PanelActivity.class);
				startActivity(signToPanelIntent);
				myApp.setUserName("sss");
				/*userPsw = txtPassword.getText().toString();
				userName = txtUserName.getText().toString();  
				if(login()) {
					myApp.setUserName(userName);
					Intent signToPanelIntent = new Intent(LoginActivity.this, PanelActivity.class);
					startActivity(signToPanelIntent);
				}*/
			}

		});
	}
	
	public boolean login() { 

		url = "http://158.132.11.225:8080/MPServer/userAction!androidLogin?user.userName="+userName+"&user.userPsw="+userPsw;
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
