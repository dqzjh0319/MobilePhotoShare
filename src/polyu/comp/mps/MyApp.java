package polyu.comp.mps;

import android.app.Application;

public class MyApp extends Application{
	
	private String userName;
	 
	public void onCreate() { 
        super.onCreate(); 
        this.setUserName("Start");
    }    
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
