package it.duccius.maps;

import it.duccius.musicplayer.ApplicationData;
import it.duccius.musicplayer.AudioGuide;
import it.duccius.musicplayer.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.Environment;
import android.util.Log;

public class MapService {

public static final int MODE_ANY = 0;
public static final int MODE_CAR = 1;
public static final int MODE_WALKING = 2;

public static  ArrayList<Trail> _trails = new  ArrayList<Trail>(); 

public static String inputStreamToString (InputStream in) throws IOException {
    StringBuffer out = new StringBuffer();
    byte[] b = new byte[4096];
    for (int n; (n = in.read(b)) != -1;) {
        out.append(new String(b, 0, n));
    }
    return out.toString();
}


public static NavigationDataSet calculateRoute(Double startLat, Double startLng, Double targetLat, Double targetLng, int mode) {
    return calculateRoute(startLat + "," + startLng, targetLat + "," + targetLng, mode);
}

public static NavigationDataSet calculateRoute(String startCoords, String targetCoords, int mode) {
    String urlPedestrianMode = "http://maps.google.com/maps?" + "saddr=" + startCoords + "&daddr="
            + targetCoords + "&sll=" + startCoords + "&dirflg=w&hl=en&ie=UTF8&z=14&output=kml";

    Log.d(ApplicationData.getAppName(), "urlPedestrianMode: "+urlPedestrianMode);

    String urlCarMode = "http://maps.google.com/maps?" + "saddr=" + startCoords + "&daddr="
            + targetCoords + "&sll=" + startCoords + "&hl=en&ie=UTF8&z=14&output=kml";

    Log.d(ApplicationData.getAppName(), "urlCarMode: "+urlCarMode);

    NavigationDataSet navSet = null;
    // for mode_any: try pedestrian route calculation first, if it fails, fall back to car route
    if (mode==MODE_ANY||mode==MODE_WALKING) navSet = MapService.getNavigationDataSet(urlPedestrianMode);
    if (mode==MODE_ANY&&navSet==null||mode==MODE_CAR) navSet = MapService.getNavigationDataSet(urlCarMode);
    return navSet;
}


/**
 * Retrieve navigation data set from either remote URL or String
 * @param url
 * @return navigation set
 * Il risultato é un elenco di coppie con il nome ed il titolo dei POI presenti in downloads.xml
 */
public static NavigationDataSet getNavigationDataSet(String url) {

    // urlString = "http://192.168.1.100:80/test.kml";
	//url = "http://2.227.2.94:8080/audio/SenaVetus.kml";
    Log.d(ApplicationData.getAppName(),"urlString -->> " + url);
    NavigationDataSet navigationDataSet = null;
    try
        {           
        final URL aUrl = new URL(url);
        final URLConnection conn = aUrl.openConnection();
        conn.setReadTimeout(5 * 1000);  // timeout for reading the google maps data: 15 secs
        conn.connect();

        //Utilities.StreamToFile(aUrl.openStream(),Utilities.getTempSDFld(),"prova.kml");
        
        /* Get a SAXParser from the SAXPArserFactory. */
        SAXParserFactory spf = SAXParserFactory.newInstance(); 
        SAXParser sp = spf.newSAXParser(); 

        /* Get the XMLReader of the SAXParser we created. */
        XMLReader xr = sp.getXMLReader();

        /* Create a new ContentHandler and apply it to the XML-Reader*/ 
        NavigationSaxHandler navSax2Handler = new NavigationSaxHandler(); 
        xr.setContentHandler(navSax2Handler); 

        /* Parse the xml-data from our URL. */ 
        xr.parse(new InputSource(aUrl.openStream()));

        /* Our NavigationSaxHandler now provides the parsed data to us. */ 
        navigationDataSet = navSax2Handler.getParsedData(); 
        _trails = navSax2Handler.get_trails();
        /* Set the result to be displayed in our GUI. */ 
        Log.d(ApplicationData.getAppName(),"navigationDataSet: "+navigationDataSet.toString());

    } catch (Exception e) {
        // Log.e(myapp.APP, "error with kml xml", e);
        navigationDataSet = null;
    }   

    return navigationDataSet;
}

public static NavigationDataSet getNavigationDataSetFile(String url) {

    // urlString = "http://192.168.1.100:80/test.kml";
	//url = "http://2.227.2.94:8080/audio/SenaVetus.kml";
    Log.d(ApplicationData.getAppName(),"urlString -->> " + url);
    NavigationDataSet navigationDataSet = null;
    try
        {           
        final URL aUrl = new URL(url);
        final URLConnection conn = aUrl.openConnection();
        conn.setReadTimeout(5 * 1000);  // timeout for reading the google maps data: 15 secs
        conn.connect();

        //Utilities.StreamToFile(aUrl.openStream(),Utilities.getTempSDFld(),"prova.kml");
        
        /* Get a SAXParser from the SAXPArserFactory. */
        SAXParserFactory spf = SAXParserFactory.newInstance(); 
        SAXParser sp = spf.newSAXParser(); 

        /* Get the XMLReader of the SAXParser we created. */
        XMLReader xr = sp.getXMLReader();

        /* Create a new ContentHandler and apply it to the XML-Reader*/ 
        NavigationSaxHandler navSax2Handler = new NavigationSaxHandler(); 
        xr.setContentHandler(navSax2Handler); 

        /* Parse the xml-data from our URL. */ 
        xr.parse(new InputSource(aUrl.openStream()));

        /* Our NavigationSaxHandler now provides the parsed data to us. */ 
        navigationDataSet = navSax2Handler.getParsedData(); 

        /* Set the result to be displayed in our GUI. */ 
        Log.d(ApplicationData.getAppName(),"navigationDataSet: "+navigationDataSet.toString());

    } catch (Exception e) {
        // Log.e(myapp.APP, "error with kml xml", e);
        navigationDataSet = null;
    }   

    return navigationDataSet;
}
@SuppressWarnings("finally")
public static ArrayList<AudioGuide> getDownloadsDataSet(String url) {

    // urlString = "http://192.168.1.100:80/test.kml";
	//url = "http://2.227.2.94:8080/audio/downloads.xml";
    Log.d(ApplicationData.getAppName(),"urlString -->> " + url);
    ArrayList<AudioGuide> downloads = null;
   
    XMLReader xr;
    
    try
        {                    
        final URL aUrl = new URL(url);
        final URLConnection conn = aUrl.openConnection();
        conn.setReadTimeout(5 * 1000);
        conn.connect();
        
        /* Get a SAXParser from the SAXPArserFactory. */
        SAXParserFactory spf = SAXParserFactory.newInstance(); 
        SAXParser sp = spf.newSAXParser(); 

        /* Get the XMLReader of the SAXParser we created. */
        xr = sp.getXMLReader();

        /* Create a new ContentHandler and apply it to the XML-Reader*/ 
        NavigationSaxHandlerDownloads navSax2Handler = new NavigationSaxHandlerDownloads(); 
        xr.setContentHandler(navSax2Handler); 

        /* Parse the xml-data from our URL. */ 
        xr.parse(new InputSource(aUrl.openStream()));

        /* Our NavigationSaxHandler now provides the parsed data to us. */ 
        downloads = navSax2Handler.getParsedData(); 
        //_trails = navSax2Handler.get_trails();
        /* Set the result to be displayed in our GUI. */ 
        Log.d(ApplicationData.getAppName(),"downloads.size(): "+downloads.size());

    } 
    catch(SocketTimeoutException ss){
	    // show message to the user
		Log.d("getDownloadsDataSet", "Non è stato possibile stabilire una connessione web.");
		downloads = null;
	}	
    catch (Exception e) {
        // Log.e(myapp.APP, "error with kml xml", e);
    	downloads = null;
    } finally 
	{			    
	    return downloads;
	}  

   
}
}