package polyu.comp.mps.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;
import android.widget.TextView;

public final class SyncTask implements Runnable {

	private TextView statusView;
	private TextView myTextView;
	private String msg;
	private String serverIP ="158.132.11.225";
	private String localFilePath = "/mnt/sdcard/PhotoShare";
	private int port = 8080;
	
	private ObjectInputStream inputFromServer;	//8000
	private ObjectOutputStream outputToServer;
	private DataInputStream in;
	private DataOutputStream out;
	private File[] ServerFiles;
	private boolean[] ServerFilesSynced;
	private boolean[] MobileFilesSynced;
	private File[] MobileFiles;
	private String[] ServerFileNames;
	private String[] MobileFileNames;
	
	public SyncTask(){
		
	}
	@Override
	public void run() {
		
		try{
			Socket socket = new Socket(serverIP, port);
			//statusView.setText("Server connected.");
			int type = 1;
				
			if (type == 1) {
				//String sdcardPath = Environment.getExternalStorageDirectory().getPath();
				MobileFileNames = getFileNames(localFilePath);
				
				{
					//get file list from server
					in = new DataInputStream(socket.getInputStream());
					
					//Get file num on server
					int fileCnt = in.readInt();
					msg = "fileCnt: "+fileCnt; //just a test
					
					ServerFileNames = new String[fileCnt];
					ServerFilesSynced = new boolean[fileCnt];
					//Get each file name
					for (int i=0; i<fileCnt; i++) {
						ServerFileNames[i] = new String(in.readUTF());
						
						//Search locally whether this file exists, name-based
						int j;
						for (j=0; j<MobileFileNames.length; j++) {
							if (MobileFileNames[j].equals(ServerFileNames[i])) {
								//System.out.println(fileName + " found locally.");
								ServerFilesSynced[j] = false;
								break;
							}
						}
						if (j == MobileFileNames.length) {
							System.out.println("!"+ServerFileNames[i] + " not found.!");
							ServerFilesSynced[i] = true;
							addFile(ServerFileNames[i]);
						}
					}
				}
				
				{
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
   					//Send total file num
   					out.writeInt(MobileFileNames.length);
   					
   					MobileFilesSynced = new boolean[MobileFileNames.length];
   					//Send each file name
   					for (int i=0; i<MobileFileNames.length; i++) {
   						out.writeUTF(MobileFileNames[i]);
   						if (in.readUTF().equals("NOT FOUND")) {
   							MobileFilesSynced[i] = true;
   						}
   						else {
   							MobileFilesSynced[i] = false;
   						}
   					}
				}
				
				in.close();
				out.close();
			}
			else if (type == 2) { //ignore this, I just wanna try out object stream
				inputFromServer = new ObjectInputStream(socket.getInputStream());
				statusView.setText("fileCnt");
				
				int fileCnt = inputFromServer.readInt();
				if (fileCnt == 5) msg = "FileCnt";
				msg = "fileCnt";
				/*files = new File[fileCnt];
				for (int i=0; i<fileCnt; i++) {
					files[i] = (File)inputFromServer.readObject();
				}*/
				inputFromServer.close();
			}
			
            socket.close();  
		} catch (UnknownHostException e) {
			Log.e("CONNECT", e.toString());
		} catch(IOException e) {
			Log.e("CONNECT", e.toString());
		} catch(Exception e) {
			Log.e("CONNECT", e.toString());
		}
	}
	
	private String[] getFileNames(String directoryPath) {
   		File path = new File(directoryPath);
   		//Process for a folder
   		if(path.isDirectory()){
   			MobileFiles = path.listFiles();
   			if(null == MobileFiles)
   				return null;
   			else {
   				MobileFileNames = new String[MobileFiles.length];
   				for (int i=0; i<MobileFiles.length; i++) {
   					MobileFileNames[i] = new String(MobileFiles[i].getName());
   				}
   			}
   		}    
   		//else{ //Process for a file
   			//String filePath = path.getAbsolutePath();
   			//String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
   	    //}
		return MobileFileNames;
   	}

	private void addFile(String fileName) {
   		return;
	}
}
