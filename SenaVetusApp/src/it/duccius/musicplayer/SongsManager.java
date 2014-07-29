package it.duccius.musicplayer;

import it.duccius.maps.MapService;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

public class SongsManager {
	// SDCard Path
	//final String MEDIA_PATH = new String("/sdcard/");
	//final String MEDIA_PATH = new String("/storage/sdcard0/duccius/");    
	String _mediaPath;
	String _language="ITA";
	String _langMediPath="";
	
	
	private ArrayList<_Audio> songsList = new ArrayList<_Audio>();
	
	// Constructor
	public SongsManager(String lang){
		_language = lang;
		String appDir = ApplicationData.getAppName()+"/"+lang;
		_mediaPath = ApplicationData.getAppName();
		File path = Environment.getExternalStoragePublicDirectory(appDir);
		_langMediPath = path.toString();
	}
	public String getLangMediaPath ()
	{		
		return _langMediPath;
	}
	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<_Audio> getPlayList(){
//		File home = new File(MEDIA_PATH + lang + "/");
		File home = new File(_langMediPath);
		if (!home.exists())
			home.mkdirs(); 

		// Se la dir non esiste occorre crearl
			try{
			if (home.listFiles(new FileExtensionFilter()).length > 0) {
				int i=0 ;
				for (File file : home.listFiles(new FileExtensionFilter())) {
					_Audio song = new _Audio();
					song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
					song.put("songPath", file.getPath());
					song.setSongPositionInSd(i);
					i++;
					// Adding each song to SongList
					songsList.add(song);
				}
			}
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		// return songs list array
		return songsList;
	}
	public ArrayList<AudioGuide> getSdAudioList(){
		ArrayList<AudioGuide> songsList = new ArrayList<AudioGuide>();
		File home = new File(_langMediPath);
		if (!home.exists())
			home.mkdirs(); 

		// Se la dir non esiste occorre crearl
			try{
			if (home.listFiles(new FileExtensionFilter()).length > 0) {
				int i=0 ;
				for (File file : home.listFiles(new FileExtensionFilter())) {
					AudioGuide song = new AudioGuide();
					song.setName(file.getName().substring(0, (file.getName().length() - 4)));
					song.setPath(file.getPath());
					song.setSdPosition(i);
					// devo recuperare il title a partire dal name
					// ...
					// Leggo file download.xml
					// scorro tutti gli elementi cercando quello con name giusto
					// recupero il title
					
					i++;
					// Adding each song to SongList
					songsList.add(song);
				}
			}
		}
		catch(Exception e)
		{
			Log.d("getSdAudioList",e.toString());
		}
		// return songs list array
		return songsList;
	}
	public ArrayList<AudioGuide> getSdAudioList(ArrayList<AudioGuide> audioGuideListLang){
		ArrayList<AudioGuide> songsList = new ArrayList<AudioGuide>();
		File home = new File(_langMediPath);
		if (!home.exists())
			home.mkdirs(); 

		// Se la dir non esiste occorre crearl
			try{
			if (home.listFiles(new FileExtensionFilter()).length > 0) {
				int i=0 ;
				for (File file : home.listFiles(new FileExtensionFilter())) {
					AudioGuide song = new AudioGuide();
					song.setName(file.getName().substring(0, (file.getName().length() - 4)));
					song.setPath(file.getPath());
					song.setSdPosition(i);
					// devo recuperare il title a partire dal name
					// ...
					// Leggo file download.xml
					// scorro tutti gli elementi cercando quello con name giusto
					for (AudioGuide ag: audioGuideListLang)
					{
						if(ag.getName().equals(song.getName()))
						{
							song.setTitle(ag.getTitle());
						}
					}
					// recupero il title
					
					i++;
					// Adding each song to SongList
					songsList.add(song);
				}
			}
		}
		catch(Exception e)
		{
			Log.d("getSdAudioList",e.toString());
		}
		// return songs list array
		return songsList;
	}
    public boolean loadGuideList(ArrayList<AudioGuide> guides)
	{	
    	boolean res;
    	
			res = false;
			guides.removeAll(guides);
			ArrayList<AudioGuide> filesToDownload = new ArrayList<AudioGuide>();			
			
			//String xmlFile = Utilities.getTempSDFld()+ File.separator+ "downloads.xml";
			String xmlFile = Utilities.getDownloadsSDPath();
			String UrlXmlFile = "file://"+xmlFile;
			File f = new File(xmlFile);
			if(f.exists()) {  
				filesToDownload = MapService.getDownloadsDataSet(UrlXmlFile);					 
			}						
			if (filesToDownload != null)
			{
				guides.addAll(filesToDownload);
				res = true;
			}			
			return res;				
	}
 
    public ArrayList<AudioGuide> guideListByLang(ArrayList<AudioGuide> guides)
	{
		ArrayList<AudioGuide> langGuides= new ArrayList<AudioGuide>();

		for (AudioGuide audio: guides) {
		    if (audio.getLang().equals(_language))
		    	langGuides.add(audio);
		}
		
		return langGuides;
	}
	public ArrayList<AudioGuide> getAudioToDownload(ArrayList<AudioGuide> sdAudios,
			ArrayList<AudioGuide> audioDisponibiliServer) {
		
		ArrayList<AudioGuide> audioToDownload = new ArrayList<AudioGuide>();
		
		for (AudioGuide disponibileSuServer :audioDisponibiliServer) {
						
			// creating new HashMap
			boolean presente = false;
			int i=0;
			//for (Audio audioInSD: songsListData )
			for (AudioGuide audioInSD: sdAudios )
			{
				
				if( audioInSD.getName().equals(disponibileSuServer.getName())){
					presente=true;	
					disponibileSuServer.setToBeDownloaded(true);
					disponibileSuServer.setSdPosition(audioInSD.getSdPosition());	
					//audioInSD.setTitle(disponibileSuServer.getTitle());
					sdAudios.get(i).setTitle(disponibileSuServer.getTitle());
					break;}
				i++;
			}
			if (!presente)
				audioToDownload.add(disponibileSuServer);
		}
		return audioToDownload;
	}
	public ArrayList<String> getSdAudioStrings(ArrayList<AudioGuide> sdAudios) {
		ArrayList<String> sdAudiosStrings = new ArrayList<String>();
		for (AudioGuide audio: sdAudios)
		{
			sdAudiosStrings.add(audio.getTitle());
		}
		return sdAudiosStrings;
	}
	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}
