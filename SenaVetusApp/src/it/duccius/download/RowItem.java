package it.duccius.download;

import android.graphics.Bitmap;

public class RowItem {
 
    private Bitmap bitmapImage;
    private String title;
 
//    public RowItem(Bitmap bitmapImage) {
//        this.bitmapImage =  bitmapImage;
//    }
    public RowItem(String title) {
        this.title = title;
    }
 
    public String getTitolo() {
        return this.title;
    }
    
    public Bitmap getBitmapImage() {
        return bitmapImage;
    }
 
    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }
}