package it.duccius.download;

//http://theopentutorials.com/tutorials/android/dialog/android-download-multiple-files-showing-progress-bar/
//http://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog

import it.duccius.musicplayer.ApplicationData;
import it.duccius.musicplayer.PlayListAudio;
import it.duccius.musicplayer.R;

import java.io.BufferedInputStream;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
 
public class Download extends Activity {
 
    ProgressDialog progressDialog;
    CustomListViewAdapter listViewAdapter;
    ListView listView;
    
    String _tempSdFld = "";
    String _destSdFld = "";
    String _destSdImgFld = "";
    
 
//    public static final String URL =
//        "http://theopentutorials.com/wp-content/themes/theopentutorials/images/open_tutorials_logo_v4.png";
//    public static final String URL1 =
//        "http://theopentutorials.com/wp-content/themes/theopentutorials/images/logo.jpg";
//    public static final String URL2 =
//        "http://theopentutorials.com/wp-content/themes/theopentutorials/images/open_tutorials_logo_v4.png";
//    
    SharedPreferences preferenceManager;
    String Download_ID = "DOWNLOAD_ID";
    String _language = "ITA";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            listView = (ListView) findViewById(R.id.imageList);
 
            Bundle b = getIntent().getExtras();
            String[] resultArr = b.getStringArray("selectedItems");
            _language = b.getString("language");
            
           
        /*Creating and executing background task*/
            //http://developer.android.com/reference/android/os/AsyncTask.html
                    
        final GetXMLTask task = new GetXMLTask(this);
        task.execute(resultArr);
 
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("In progress...");
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setIcon(R.drawable.arrow_stop_down);
        progressDialog.setCancelable(true);
        progressDialog.show();
        
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                task.cancel(true);
                task.closingActivity();
            }
        });
    }
    //---------------------------------------------    

	 private String getDestSDFld(String url) {
			String sourcePath = Environment.getExternalStorageDirectory().toString()+"/"+ ApplicationData.getAppName()+"/"+_language;
			return sourcePath;
		}
	 private String getTempSDFld(String url) {
			String sourcePath = Environment.getExternalStorageDirectory().toString()+"/"+ ApplicationData.getAppName()+"/temp/"+_language;
			return sourcePath;
		}
	 private String getdestSdImgFld(String url) {
			String destSdImgFld = Environment.getExternalStorageDirectory().toString()+"/"+ ApplicationData.getPicFolder();
			return destSdImgFld;
		}
	 
	 private String getAudioName(String url)
	 {
		 String title = "";
		 String [] tokens = url.split("/");
		 title = tokens[tokens.length-1];
		 return title;
	 }
	 private String getPicName(String url)
	 {
		 String title = "";
		 String [] tokens = url.split("/");
		 title = tokens[tokens.length-1];
		 //title = title.substring(4);
		 return title;
	 }
	 private String getAudioLang(String url)
	 {
		 String lang = "";
		 String [] tokens = url.split("/");
		 lang = tokens[tokens.length-2];
		 return lang;
	 }
	 private String getImgUrl(String url)
	 {
		 String lang = "";
		 String jpgPath = url.replace(".mp3", ".jpg");
		 String[] tokens = jpgPath.split(File.separator);
		tokens[tokens.length-2]="pics";
		//tokens[tokens.length-1]=getPicName(url).replace(".mp3", ".jpg");
		String res = "";
		 for (int i=0; i < tokens.length; i++)
		 {
			 res = res +tokens[i]+File.separator;
		 }
		 res = res.substring(0, res.length()-1);
		 return res;
	 }
	 //
 //---------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    private class GetXMLTask extends AsyncTask<String, Integer, List<RowItem>> {
        private Activity context;
        List<RowItem> rowItems;
        int noOfURLs;
        public GetXMLTask(Activity context) {
            this.context = context;
        }
 
        @Override
        protected List<RowItem> doInBackground(String... sUrl) {
            // take CPU lock to prevent CPU from going off if the user 
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                 getClass().getName());
            wl.acquire();

            rowItems = new ArrayList<RowItem>();
            noOfURLs=sUrl.length;
            _tempSdFld = getTempSDFld(sUrl[0]);
            _destSdFld = getDestSDFld(sUrl[0]);
            
            _destSdImgFld = getdestSdImgFld(sUrl[0]);            
            
            //createFolder(_tempSdFld);
            verifyFile(_tempSdFld);
            verifyFile(_destSdFld);
            verifyFile(_destSdImgFld); 
            
            try {
            	for (int i=0; i<sUrl.length; i++ )
            	{try {
            		rowItems = getWebAudio(sUrl[i]);
            		if (rowItems != null)
	            		{
	            		//String picUrl = sUrl[i].replace(".mp3", ".jpg") ;
	            		String picUrl =getImgUrl(sUrl[i]);
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

		private void verifyFile(String filePath) {
			File picFolder = new File(filePath);
    		if (!picFolder.exists())
    			picFolder.mkdirs();
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
				Log.d(getPackageName(), "Non è stato possibile stabilire una connessione web.");
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
//                URL url = new URL(sUrl);
			    connection = (HttpURLConnection) url.openConnection();
			    //http://stackoverflow.com/questions/3212792/how-to-implement-request-timeout-in-android
			    connection.setConnectTimeout(5000); // set 5 seconds for timeout
			    connection.connect();
 
			    // expect HTTP 200 OK, so we don't mistakenly save error report 
			    // instead of the file
			    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			    {			
			    	Log.d(getLocalClassName(), "Impossibile scaricare l\'immagine dal server");
			         return null;
			    }
			    // this will be useful to display download percentage
			    // might be -1: server did not report the length
			    int fileLength = connection.getContentLength();
			    // download the file

			    
//                inputStream = new BufferedInputStream(url.openStream());
			    inputStream = connection.getInputStream();
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
 
                outputStream = new BufferedOutputStream(dataStream);
 
                byte data[] = new byte[512];
//                long total = 0;
 
                while ((count = inputStream.read(data)) != -1) {
//                    total += count;
                    /*publishing progress update on UI thread.
                    Invokes onProgressUpdate()*/
//                    publishProgress((int)((total*100)/lenghtOfFile));
 
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
            progressDialog.setProgress(progress[0]);
            if(rowItems != null) {
            	//int currentFileCount = (rowItems.size()+1 > noOfURLs) ?rowItems.size()+1:noOfURLs;
            	int currentFileCount = rowItems.size();
            	if(currentFileCount>0)
            		progressDialog.setMessage("Loading " + (currentFileCount) + "/" + noOfURLs);
            }
       }
 
       @Override
       protected void onPostExecute(List<RowItem> rowItems) {
//        listViewAdapter = new CustomListViewAdapter(context, rowItems);
//        listView.setAdapter(listViewAdapter);
        progressDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(),
        		PlayListAudio.class);
     		
     		// Sending songIndex to PlayerActivity        
        intent.putExtra("language", _language);       
     		//startActivity(intent);
        // http://stackoverflow.com/questions/2497205/how-to-return-a-result-startactivityforresult-from-a-tabhost-activity
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
        finish();
       }

	private void closingActivity() {
		deleteDirContents(_tempSdFld);
     // Starting new intent
       
     		
	}    
 
    }


}