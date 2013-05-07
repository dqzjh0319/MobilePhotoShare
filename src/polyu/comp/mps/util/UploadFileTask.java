package polyu.comp.mps.util;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

public class UploadFileTask extends AsyncTask<String, Void, String> {
	//public static final String requestURL = "http://158.132.11.225:8080/AndroidUploadFileWeb/FileImageUploadServlet";
	public static final String requestURL = "http://158.132.11.225:8080/MPServer/FileImageUploadServlet";
	private ProgressDialog pdialog;
	private Activity context = null;

	public UploadFileTask(Activity ctx) {
		this.context = ctx;
		pdialog = ProgressDialog.show(context, "uploading", "uploading");
	}

	protected void onPostExecute(String result) {

		pdialog.dismiss();
		if (UploadUtils.SUCCESS.equalsIgnoreCase(result)) {
			Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
			//String url = "http://158.132.11.225:8080/MPServer/photoAction!addPhoto?user.userName="+userName+"&user.userPsw="+userPsw;
		} else {
			Toast.makeText(context, "Failed!", Toast.LENGTH_LONG).show();
		}
	}

	protected void onPreExecute() {
	}

	protected void onCancelled() {
		super.onCancelled();
	}

	protected String doInBackground(String... params) {
		File file = new File(params[0]);
		return UploadUtils.uploadFile(file, requestURL);
	}

	protected void onProgressUpdate(Void... values) {

	}
}