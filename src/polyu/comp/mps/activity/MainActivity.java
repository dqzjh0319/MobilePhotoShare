package polyu.comp.mps.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import polyu.comp.mps.R;
import polyu.comp.mps.MyApp;
import polyu.comp.mps.util.JsonUtil;
import polyu.comp.mps.util.LocationUtil;
import polyu.comp.mps.util.SyncTask;
import polyu.comp.mps.util.UploadFileTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private MyApp myApp; 
	private List<Map<String, Object>> fileNameList;
	private File path;
	private ListView mylv;
	public SimpleAdapter mAdapter;
	private String optAbsPath;
	private String photoName;
	private String myDir;
	public Stack<String> pathList;
	private Dialog fileOptDialog;
	private EditText et_PhotoTitle;
	private EditText et_PhotoComments;
	private LocationUtil locationUtil;
	private Dialog commentsDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationUtil = new LocationUtil(MainActivity.this);
		myApp = (MyApp) getApplication();
		
		Intent intent = getIntent();
		myDir = intent.getStringExtra("curPath");
		setContentView(R.layout.activity_main);
        mylv = (ListView)findViewById(R.id.myListView);
              
		fileNameList = new ArrayList<Map<String, Object>>();
		path = new File(myDir);
		TextView curPath = (TextView)findViewById(R.id.curPath);
		curPath.setText("  " + path.getAbsolutePath().toString());
		File[] files = path.listFiles();
		pathList = new Stack<String>();
		getFileName(files);
        String[] fromColumns = {"Icon", "Name"};
        int[] toViews = {R.id.itemImage, R.id.itemName}; 
		mAdapter = new SimpleAdapter(this, fileNameList,R.layout.simple_list_item,fromColumns,toViews);
        mylv.setOnItemClickListener(new myOnLVItemClickListener());
        mylv.setOnItemLongClickListener(new myOnItemLongClickListener());
        mylv.setAdapter(mAdapter);
	}


	class myOnLVItemClickListener implements OnItemClickListener{

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Log.i("ItemClicked", "ItemClicked");
			String p = (String) fileNameList.get(arg2).get("AbsPath");
			path = new File(p);
			if(!path.isDirectory())
			{
				DisplayImage(path);
				return;
			}
			TextView tmpTV = (TextView)findViewById(R.id.curPath);
			tmpTV.setText(p);
			File[] files = path.listFiles();
			Log.i("ItemClicked", "File number is " + Integer.toString(files.length));
			pathList.push(path.getParent());
			getFileName(files);
			mAdapter.notifyDataSetChanged();
		}
		
		public void DisplayImage(File image){
			Intent toImageIntent = new Intent(Intent.ACTION_VIEW);
			toImageIntent.setDataAndType(Uri.fromFile(image), "image/*");
			startActivity(toImageIntent);
		}
		
	}
	
	class myOnItemLongClickListener implements OnItemLongClickListener{

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			Log.i("LongClickEvent", "called successfully!!!");
			AlertDialog.Builder fileOptDialogBuilder = new AlertDialog.Builder(MainActivity.this);
			fileOptDialogBuilder.setTitle(R.string.fileopt);
			String[] OptItemName = new String[]{"Delete","Rename","Upload","Share"}; 
			fileOptDialogBuilder.setItems(OptItemName, new MyDialogItemOnClickListener());
			fileOptDialog = fileOptDialogBuilder.create(); 
			optAbsPath = fileNameList.get(position).get("AbsPath").toString();
			photoName = fileNameList.get(position).get("Name").toString();
			fileOptDialog.show();
			return true;
		}
	}

	class MyDialogItemOnClickListener implements android.content.DialogInterface.OnClickListener{

		public void onClick(DialogInterface dialog, int which) {
			Log.i("DialogClickEvent", Integer.toString(which));
			File tmpFile = new File(optAbsPath);
			switch(which){
				case 0: DeleteOptFile(tmpFile);break;
				case 1: RenameOptFile(tmpFile);break;
				case 2: UploadOptFile(tmpFile);break;
				case 3: ShareOptFile(tmpFile);break;
			}
		}
	}
	
	public void DeleteOptFile(File optFile){
		if(optFile.isDirectory()){
			File[] tmpfs = optFile.listFiles();
			for(File tf : tmpfs){
				if(tf.isDirectory()){
					DeleteOptFile(tf);
				}
				else{
					tf.delete();
				}
			}
			optFile.delete();
		}
		else
		{
			optFile.delete();
		}
		Log.i("DeleteOptFile", optFile.getPath() + ", "+ path.getAbsolutePath()); 
		if(optFile.getParent().equals(path.getAbsolutePath())){
			getFileName(path.listFiles());
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void RenameOptFile(File optFile){
		
	}
	
	public void UploadOptFile(final File optFile){
		fileOptDialog.dismiss();
		AlertDialog.Builder commentsDialogBuilder = new AlertDialog.Builder(MainActivity.this);
		commentsDialogBuilder.setTitle(R.string.Setting);
		View view = LayoutInflater.from(this).inflate(R.layout.layout_uploadcomments, null);
		commentsDialogBuilder.setView(view);
		Button btn_UploadOK = (Button) view.findViewById(R.id.btn_UploadOK);
		Button btn_UploadCancel = (Button) view.findViewById(R.id.btn_UploadCancel);
		et_PhotoTitle = (EditText) view.findViewById(R.id.etPhotoTitle);
		et_PhotoComments = (EditText) view.findViewById(R.id.etPhotoComments);
		commentsDialog = commentsDialogBuilder.create();
		
		btn_UploadOK.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String photoTitle = et_PhotoTitle.getText().toString();
				String photoComments = et_PhotoComments.getText().toString();
				String url = "http://158.132.11.225:8080/MPServer/photoAction!addPhoto?photo.photoTitle="+photoTitle+
						    "&photo.isShared=0"+"&photo.photoAuthor="+myApp.getUserName()+
						    "&photo.photoComments="+photoComments+
						    "&photo.picPath=/upload/pic/"+photoName+
							"&photo.location="+locationUtil.getLocationInfo();
				JsonUtil.getJson(url);
				//UploadFileTask uploadFileTask = new UploadFileTask(MainActivity.this);
				//uploadFileTask.execute(optFile.getAbsolutePath());
			}
		});
		
		btn_UploadCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				commentsDialog.dismiss();
			}
		});
		
		commentsDialog.show();

	}
	
	public void ShareOptFile(File optFile){
		fileOptDialog.dismiss();
		String url = "http://158.132.11.225:8080/MPServer/photoAction!sharePhoto?photo.photoTitle="+photoName+
			    "&photo.isShared=1"+"&photo.photoAuthor="+myApp.getUserName()+
			    "&photo.picPath="+photoName+
				"&photo.location="+locationUtil.getLocationInfo();
		if(JsonUtil.getJson(url) != true) {
			//UploadFileTask uploadFileTask=new UploadFileTask(MainActivity.this);
			//uploadFileTask.execute(optFile.getAbsolutePath());
		}
	}
	
	public void onBackPressed() {
		if(pathList.empty()){
			super.onBackPressed();
		}
		else{
			String tmpStr = pathList.pop();
			path = new File(tmpStr);
			TextView tmpTV = (TextView)findViewById(R.id.curPath);
			tmpTV.setText(tmpStr);
			File[] files = path.listFiles();
			Log.i("onBackPressed", "Current path is " + tmpStr);
			getFileName(files);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private void getFileName(File[] files) {
		Log.i("getFileName", "Accessed!!!");
		fileNameList.clear();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					String fileName = file.getName().toString();
					String AbsPath = file.getAbsolutePath();
					map.put("Name", fileName);
					map.put("Icon", R.drawable.folder_icon);
					map.put("AbsPath", AbsPath);
					fileNameList.add(map);
				} else {
						String fileName = file.getName();
						HashMap<String, Object> map = new HashMap<String, Object>();
						String AbsPath = file.getAbsolutePath();
						map.put("Name", fileName);
						map.put("Icon", R.drawable.image_icon);
						map.put("AbsPath", AbsPath);
						fileNameList.add(map);
				}
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);		
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false); 
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {		
		switch(item.getItemId()){
			case R.id.menu_update: 
				Log.i("MenuItem", "Update Clicked");
				doUpdate();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}	
	
	public void doUpdate(){
		Thread SyncThread = new Thread(new SyncTask());
		SyncThread.start();
		
		try {
			SyncThread.join();
		} catch (Exception e) {
			
		}
	}
}
