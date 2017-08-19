package it.duccius.musicplayer;
import it.duccius.download.RowItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;


//import it.duccius.download.RetriveAsyncFile;

public class Utilities {
	private static final String _downloadsFileName = "downloads_catania.xml";
	// Dropbox	
	private static String urlDownloads = new String("https://sites.google.com/site/ducciofabbri/home/sv-1/" +_downloadsFileName);
	private static String baseUrlImg = "https://sites.google.com/site/ducciofabbri/pics/catania/~img_name~.jpg?attredirects=0&d=1";
	private static String baseUrlMp3 = "https://sites.google.com/site/ducciofabbri/audio/catania/ita/~mp3_name~.mp3?attredirects=0&d=1";
	
//	private static int mapType = GoogleMap.MAP_TYPE_HYBRID;
//	private static int mapType = GoogleMap.MAP_TYPE_SATELLITE;
//	private static int mapType = GoogleMap.MAP_TYPE_TERRAIN;
	private static int mapType = GoogleMap.MAP_TYPE_NORMAL;
	//downloadType = 1 : download di tutti i file audio allávvio dell'applicazione
	//downloadType = 2 : i file vengono scaricati separatamente, cliccando sui POI o dal menú download
	
	private static int downloadType = 1;
	
	public static int getDownloadType() {
		return downloadType;
	}
	public static void setDownloadType(int downloadType) {
		Utilities.downloadType = downloadType;
	}	
	public static int getMapType() {
		return mapType;
	}
	public static void setMapType(int _mapType) {
		Utilities.mapType = _mapType;
	}
	public static String getImgUrlFromName(String img_name )
	{
		return baseUrlImg.replaceAll("~img_name~", img_name);
	}
	public static String getMp3UrlFromName(String mp3_name )
	{
		return baseUrlMp3.replaceAll("~mp3_name~", mp3_name);
	}
	public static String getImgUrlFromMp3Url(String mp3_url )
	{
		int i = baseUrlMp3.indexOf("~mp3_name~");
		int lf = baseUrlMp3.length()-i-"~mp3_name~".length();
		
		String mp3_name = mp3_url.substring(i, mp3_url.length()-lf);
		return getImgUrlFromName(mp3_name);
	}
	public static String getImgNameFromUrl(String img_url )
	{
		int i = baseUrlImg.indexOf("~img_name~");
		int lf = baseUrlImg.length()-i-"~img_name~".length();
		
		return img_url.substring(i, img_url.length()-lf)+".jpg";
		 
	}
	public static String getMp3NameFromUrl(String mp3_url )
	{
		int i = baseUrlMp3.indexOf("~mp3_name~");
		int lf = baseUrlMp3.length()-i-"~mp3_name~".length();
		
		return mp3_url.substring(i, mp3_url.length()-lf)+".mp3";
		 
	}
	public static ArrayList<String> getUrlsToDownload(AudioGuideList audioToDownloadLang )
	{
		ArrayList<String> arL  = new ArrayList<String>();
		for(AudioGuide ag: audioToDownloadLang)
		{
			String str = Utilities.getMp3UrlFromName(ag.getName());
			arL.add(str);
		}
		return arL;
		 
	}
	
	// Interfaccia da richiamare quando il download del file in asincrono tramite DownloadFile è terminato
	public interface  MyCallbackInterface {

        void onDownloadFinished(List<RowItem> rowItems);
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
	public static  String getTempSDFldLang(String language) {
			String sourcePath = Environment.getExternalStorageDirectory().toString()+File.separator+ ApplicationData.getAppName()+File.separator+"temp"+File.separator+language;
			return sourcePath;
		}
	public static String getTempSDFld() {
		String sourcePath = Environment.getExternalStorageDirectory().toString()+File.separator+ ApplicationData.getAppName()+File.separator+"temp";
		return sourcePath;
	}
	public static String getDestSDFldLang(String language) {
			String sourcePath = Environment.getExternalStorageDirectory().toString()+File.separator+ ApplicationData.getAppName()+File.separator+ language;
			return sourcePath;
		}
	public static String getDownloadsSDPath() {
		String sourcePath = Environment.getExternalStorageDirectory().toString()+File.separator+ ApplicationData.getAppName()+File.separator+"temp"+File.separator+_downloadsFileName;
		return sourcePath;
	}
	public static String getdestSdImgFld() {
			String destSdImgFld = Environment.getExternalStorageDirectory().toString()+"/"+ ApplicationData.getPicFolder();
			return destSdImgFld;
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
	public static String getUrlDownloads() {
		return urlDownloads;
	}
	
}
