package it.duccius.musicplayer;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationData {
	private static final String appName = "SenaVetus";
	//private static final String download_path = "http://2.227.2.94:8080/audio/";
	
	// SIENA
	//	private static final String download_path = "http://2.227.2.94:8080/SenaVetus/Audio/";
	//	private static final String feedbackRemoteUrl = "http://2.227.2.94:8080/SenaVetus/Contatti?";
	//Lugano 77.57.63.163
	private static final String download_path = "http://77.57.63.163:8080/SenaVetus/Audio/";
	private static final String feedbackRemoteUrl = "http://77.57.63.163:8080/SenaVetus/Contatti?";
	
	private static final String picFolder = "SenaVetus/pics";
	// Tre lettere per ogni lingua
	private static final String[] languages = {"ITA","ENG","DEU","FRA"};
	private static final ArrayList<String> points = new ArrayList<String>(){{
		add("11.321632046626974,43.329107804416026,0.0");
		add("11.325358003377914,43.32768375737556,0.0");
		add("11.324731707572937,43.32656621348019,0.0");
		add("11.322069615125656,43.328650899137955,0.0");
	}};
		
    public static String getAppName() {
        return appName;
    }
    public static String getPicFolder() {
        return picFolder;
    }
    public static String getDownloadRemotePath() {
        return download_path;
    }
    public static String getFeedbackRemoteUrl() {
        return feedbackRemoteUrl;
    }
    public static String[] getLanguages() {
        return languages;
    }
    public static ArrayList<String>getPoints() {
        return points;
    }

}
