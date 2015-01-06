package it.duccius.download;

import it.duccius.musicplayer.ApplicationData;
import it.duccius.musicplayer.Utilities;
import it.duccius.musicplayer.Utilities.MyCallbackInterface;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;


public class DownloadFile extends AsyncTask<ArrayList<String>, Integer, List<RowItem>> {
    private Activity context;
    List<RowItem> rowItems;
    int noOfURLs;
	private String _tempSdFld;
	private String _destSdFld;
	private String _destSdImgFld;
	private String _language;
	private ProgressDialog _progressDialog;
	
	final MyCallbackInterface callback;

	
    public DownloadFile(Activity context, String language, ProgressDialog progressDialog, MyCallbackInterface callback) {
        this.callback = callback;
        this.context = context;
        _language = language;
        _progressDialog = progressDialog;
        
        _tempSdFld = Utilities.getTempSDFldLang(_language);        
        _destSdFld = Utilities.getDestSDFldLang(_language);
        
        _destSdImgFld = Utilities.getdestSdImgFld(); 
    }
    public DownloadFile(Activity context, String language, String destSdFld, ProgressDialog progressDialog, MyCallbackInterface callback) {
    	this.callback = callback;
        this.context = context;
        _language = language;
        _progressDialog = progressDialog;
        
        _tempSdFld = Utilities.getTempSDFldLang(_language);        
        _destSdFld = destSdFld;
        
        _destSdImgFld = Utilities.getdestSdImgFld(); 
    }
    @Override
    public List<RowItem> doInBackground(ArrayList<String>... sUrl) {
        // take CPU lock to prevent CPU from going off if the user 
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
             getClass().getName());
        wl.acquire();

        rowItems = new ArrayList<RowItem>();
        noOfURLs=sUrl.length;
                   
        
        //createFolder(_tempSdFld);
        verifyFile(_tempSdFld);
        verifyFile(_destSdFld);
        verifyFile(_destSdImgFld); 
        
        try {
        	for (int i=0; i<sUrl.length; i++ )
        	{try {
        		rowItems = getWebAudio(sUrl[0].get(i));
        		
        		if (rowItems != null && (
        								((String)sUrl[0].get(i)).endsWith(".mp3")))
            		{
            		//String picUrl = sUrl[i].replace(".mp3", ".jpg") ;
            		String picUrl =getImgUrl(sUrl[0].get(i));
            		Bitmap pic = getWebPic(picUrl);
            		FileOutputStream out;
					
            		String pn =  picUrl.split("/")[picUrl.split("/").length-1];
            		
            		String destFile = _destSdImgFld+"/"+pn;            		
            		
						out = new FileOutputStream(destFile);
					
            	       pic.compress(Bitmap.CompressFormat.JPEG, 100, out);
            	       try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
            		}
        		} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        	}
        	
        } 
        finally {
            wl.release();
        }
        return rowItems;
    }
//	 private String getDestSDFld(String url) {
//			String sourcePath = Environment.getExternalStorageDirectory().toString()+"/"+ ApplicationData.getAppName()+"/"+_language;
//			return sourcePath;
//		}
//	 private String getTempSDFld(String url) {
//			String sourcePath = Environment.getExternalStorageDirectory().toString()+"/"+ ApplicationData.getAppName()+"/temp/"+_language;
//			return sourcePath;
//		}
//	 private String getdestSdImgFld(String url) {
//			String destSdImgFld = Environment.getExternalStorageDirectory().toString()+"/"+ ApplicationData.getPicFolder();
//			return destSdImgFld;
//		}
	private void verifyFile(String filePath) {
		File picFolder = new File(filePath);
		if (!picFolder.exists())
			picFolder.mkdirs();
	}
	private String getImgUrl(String url)
	 {
		 String lang = "";
		 String jpgPath = url.replace(".mp3", ".jpg");
		 String[] tokens = jpgPath.split(File.separator);
		tokens[tokens.length-3]="pics";
		System.arraycopy(tokens,tokens.length-1,tokens,tokens.length-2,1);
		//tokens[tokens.length-1]=getPicName(url).replace(".mp3", ".jpg");
		String res = "";
		 for (int i=0; i < tokens.length-1; i++)
		 {
			 res = res +tokens[i]+File.separator;
		 }
		 res = res.substring(0, res.length()-1);
		 return res;
	 }
	private String getAudioName(String url)
	 {
		 String title = "";
		 String [] tokens = url.split("/");
		 title = tokens[tokens.length-1];
		 return title;
	 }
	private void createFolder(String path) {
		File tempDirFld = new File(path);
		if (!tempDirFld.isDirectory()) {           
        	File directory = new File(path);
        	directory.mkdirs();
        }
	}

	private List<RowItem> getWebAudio(String sUrl) {
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
		    URL url = new URL(sUrl);
		    connection = (HttpURLConnection) url.openConnection();
		    //http://stackoverflow.com/questions/3212792/how-to-implement-request-timeout-in-android
		    connection.setConnectTimeout(5000); // set 5 seconds for timeout
		    connection.connect();
				    

		    // expect HTTP 200 OK, so we don't mistakenly save error report 
		    // instead of the file
		    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
		    {
		         String error =  "Server returned HTTP " + connection.getResponseCode() 
		             + " " + connection.getResponseMessage();
		         rowItems.add(new RowItem(error));
		         return rowItems;
		    }
		    // this will be useful to display download percentage
		    // might be -1: server did not report the length
		    int fileLength = connection.getContentLength();

		    // download the file
		    input = connection.getInputStream();
		  
		    output = new FileOutputStream(_tempSdFld +File.separator+ getAudioName(sUrl));

		    byte data[] = new byte[4096];
		    long total = 0;
		    int count;
		    while ((count = input.read(data)) != -1) {
		        // allow canceling with back button
		        if (isCancelled())
		        	return rowItems;
		        total += count;
		        // publishing the progress....
		        if (fileLength > 0) // only if total length is known
		            publishProgress((int) (total * 100 / fileLength));
		        output.write(data, 0, count);
		    }
		    moveToAudioFolder(sUrl);			    
		    rowItems.add(new RowItem(getAudioName(sUrl)));
		    return rowItems;
		} 
		catch(SocketTimeoutException ss){
		    // show message to the user
			android.content.ContextWrapper cw = new android.content.ContextWrapper(context);
			
			Log.d(cw.getPackageName(),"Errore in getWebAudio (): "+ss.getMessage());
			return null;
		}	
		catch (Exception e) {                    	                
		        String error =  e.toString();
		        rowItems.add(new RowItem(error));
		        return null;	                
		} 
		finally 
		{
			try {
		        if (output != null)
		            output.close();
		        if (input != null)
		            input.close();
		    } 
		    catch (IOException ignored) { }

		    if (connection != null)
		        connection.disconnect();
		}
	}
	private Bitmap getWebPic(String urlString) {
		 
        int count = 0;
        Bitmap bitmap = null;

        URL url;
        InputStream inputStream = null;
        BufferedOutputStream outputStream = null;

        try {
        	HttpURLConnection connection = null;
            url = new URL(urlString);
//            URL url = new URL(sUrl);
		    connection = (HttpURLConnection) url.openConnection();
		    //http://stackoverflow.com/questions/3212792/how-to-implement-request-timeout-in-android
		    connection.setConnectTimeout(5000); // set 5 seconds for timeout
		    connection.connect();

		    // expect HTTP 200 OK, so we don't mistakenly save error report 
		    // instead of the file
		    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
		    {			    	
		    	Log.d("getWebPic() ", "Impossibile scaricare l\'immagine dal server");
		         return null;
		    }
		    // this will be useful to display download percentage
		    // might be -1: server did not report the length
		    int fileLength = connection.getContentLength();
		    // download the file

		    
//            inputStream = new BufferedInputStream(url.openStream());
		    inputStream = connection.getInputStream();
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

            outputStream = new BufferedOutputStream(dataStream);

            byte data[] = new byte[512];
//            long total = 0;

            while ((count = inputStream.read(data)) != -1) {
//                total += count;
                /*publishing progress update on UI thread.
                Invokes onProgressUpdate()*/
//                publishProgress((int)((total*100)/lenghtOfFile));

                // writing data to byte array stream
                outputStream.write(data, 0, count);
            }
            outputStream.flush();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            byte[] bytes = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,bmOptions);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(inputStream);
            FileUtils.close(outputStream);
        }
        return bitmap;
    }
	private int deleteDirContents(String path)
	{
		int deletedFiles = 0;
		File dir = new File(path);
		if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            new File(dir, children[i]).delete();
	        }
	        deletedFiles=children.length;
	    }
		return deletedFiles;
	}
	private void moveToAudioFolder(String sUrl) {
		File fileFrom = new File(_tempSdFld+File.separator+ getAudioName(sUrl));
		File fileTo = new File(_destSdFld+File.separator+ getAudioName(sUrl));
		
		fileFrom.renameTo(fileTo);
	}
  

    protected void onProgressUpdate(Integer... progress) {
        _progressDialog.setProgress(progress[0]);
        if(rowItems != null) {
        	//int currentFileCount = (rowItems.size()+1 > noOfURLs) ?rowItems.size()+1:noOfURLs;
        	int currentFileCount = rowItems.size();
        	if(currentFileCount>0)
        		_progressDialog.setMessage("Loading " + (currentFileCount) + "/" + noOfURLs);
        }
   }

   @Override
   protected void onPostExecute(List<RowItem> rowItems) {
//    listViewAdapter = new CustomListViewAdapter(context, rowItems);
//    listView.setAdapter(listViewAdapter);
    _progressDialog.dismiss();
    callback.onDownloadFinished(rowItems);
   }

public void closingActivity() {
	deleteDirContents(_tempSdFld);
 // Starting new intent
   
 		
}    

}