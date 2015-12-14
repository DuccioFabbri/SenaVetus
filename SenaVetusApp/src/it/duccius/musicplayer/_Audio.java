package it.duccius.musicplayer;

import java.util.HashMap;

public class _Audio extends HashMap<String, String>{
	
	private Integer imageId;
	private String songTitle;
	private String songPath;
	private String songLang;
	private Integer songPositionInSd;
	private boolean toBeDownloaded = false;
	private String point;
	
	 public String getPoint() {
	        return this.point;
	    }
	 public void setPoint(String point) {
	        this.point = point;
	    }
	 
	public Integer getImageId() {
        return imageId;
    }

    public String getSongTitle() {
        return this.get("songTitle");
    }

    public String getSongPath() {
        return this.get("songPath");
    }
    
    public String getSongLang() {
        return this.songLang;
    }
    
    public boolean getToBeDownloaded() {
        return toBeDownloaded;
    }
    public void setToBeDownloaded(boolean readyToDownload) {
        this.toBeDownloaded = readyToDownload;
    }
    public void setSongPositionInSd(Integer songPositionInSd) {
        this.songPositionInSd = songPositionInSd;
    }
    public void setSongLang(String songLang) {
        this.songLang = songLang;
    }
    public Integer getSongPositionInSd() {
        return this.songPositionInSd;
    }
}

