package it.duccius.musicplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;


public class Utilities {
	
	public static boolean downloadFile(String url, String filePath, String nomeFile,
			int timeoutSec) throws MalformedURLException, IOException {
		try
		{
			final URL aUrl = new URL(url);
			final URLConnection conn = aUrl.openConnection();
			conn.setConnectTimeout(timeoutSec * 1000);
			conn.setReadTimeout(timeoutSec * 1000);  
			conn.connect();
	
			Utilities.StreamToFile(aUrl.openStream(), filePath, nomeFile);
			
			return true;
		}
		//NetworkOnMainThreadException
		catch(Exception e)
		{
			Log.d("downloadFile()",e.toString());
			return false;
		}
	}
	private static void verifyFile(String filePath) {
		File picFolder = new File(filePath);
		if (!picFolder.exists())
			picFolder.mkdirs();
	}
	public static void StreamToFile(InputStream input, String destFld, String fileName)
	{
		FileOutputStream output = null;
		try {
			verifyFile(destFld);
			output = new FileOutputStream(destFld +File.separator+ fileName);
		
		    byte data[] = new byte[1024];	   
		    int count;	    
		  
				while ((count = input.read(data)) != -1) {	        
				    output.write(data, 0, count);
				}
			}
		    catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			finally {
				try {
					if (output != null)
						output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
	}
	public static String getTempSDFld() {
		String sourcePath = Environment.getExternalStorageDirectory().toString()+File.separator+ ApplicationData.getAppName()+File.separator+"temp";
		return sourcePath;
	}
	public static String getDownloadsSDPath() {
		String sourcePath = Environment.getExternalStorageDirectory().toString()+File.separator+ ApplicationData.getAppName()+File.separator+"temp"+File.separator+"downloads.xml";
		return sourcePath;
	}
	public static String getKMLSDPath() {
		String sourcePath = Environment.getExternalStorageDirectory().toString()+File.separator+ ApplicationData.getAppName()+File.separator+"temp"+File.separator+"SenaVetus.kml";
		return sourcePath;
	}
	/**
	 * Function to convert milliseconds time to
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * */
	public String milliSecondsToTimer(long milliseconds){
		String finalTimerString = "";
		String secondsString = "";
		
		// Convert total duration into time
		   int hours = (int)( milliseconds / (1000*60*60));
		   int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		   int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		   // Add hours if there
		   if(hours > 0){
			   finalTimerString = hours + ":";
		   }
		   
		   // Prepending 0 to seconds if it is one digit
		   if(seconds < 10){ 
			   secondsString = "0" + seconds;
		   }else{
			   secondsString = "" + seconds;}
		   
		   finalTimerString = finalTimerString + minutes + ":" + secondsString;
		
		// return timer string
		return finalTimerString;
	}
	
	/**
	 * Function to get Progress percentage
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;
		
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);
		
		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;
		
		// return percentage
		return percentage.intValue();
	}

	/**
	 * Function to change progress to timer
	 * @param progress - 
	 * @param totalDuration
	 * returns current duration in milliseconds
	 * */
	public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);
		
		// return current duration in milliseconds
		return currentDuration * 1000;
	}
	public boolean saveObject(Object obj, String objName, Context mContext) throws IllegalArgumentException, IllegalAccessException { 
		  SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
		  SharedPreferences.Editor editor = prefs.edit();
		  editor.clear();
		  
		  editor.putString(objName +"_classname", obj.getClass().getCanonicalName());
		  
		  for(Field field : obj.getClass().getDeclaredFields())
		  {
			  String val = "";
			  if (field.get(obj) != null) 
					  val = field.get(obj).toString();
		    editor.putString(objName+"_"+field.getName(), val);
		  }
		  return editor.commit();
		}
	public Object loadObject(String objName, Context mContext) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {
		  SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
		  String className = prefs.getString(objName+"_classname", "");
		  Object obj = Class.forName(className).newInstance();	

		  for(Field field : obj.getClass().getDeclaredFields())
		  {			  	
			  Object value = prefs.getString(objName + "_" + field.getName(), null);
			  field.set(obj, value);
			  
		  }
		  return obj;
		}
}
