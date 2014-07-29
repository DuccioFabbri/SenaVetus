/*
Copyright (C) 2011 by Indrajit Khare

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package it.duccius.adapters;

import it.duccius.musicplayer.AudioGuide;
import it.duccius.musicplayer._PlayListActivity;
import it.duccius.musicplayer.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class RowSdAudioGuide implements Row, OnClickListener {
    private final AudioGuide _audioGuide;
    private final LayoutInflater _inflater;
	private Context _context;

    public RowSdAudioGuide(LayoutInflater inflater, AudioGuide audioGuide, Context context) {
        this._audioGuide = audioGuide;
        this._inflater = inflater;
        this._context = context;
    }

    public View getView(View convertView) {
        ViewHolder holder;
        View view;
        if (convertView == null) {
            ViewGroup viewGroup = (ViewGroup)_inflater.inflate(R.layout.sd_row, null);
            holder = new ViewHolder((TextView)viewGroup.findViewById(R.id.id_audioSD),(TextView)viewGroup.findViewById(R.id.title),
                    (TextView)viewGroup.findViewById(R.id.description));
            viewGroup.setTag(holder);
            view = viewGroup;
        } else {
            view = convertView;
            holder = (ViewHolder)convertView.getTag();
        }

//        holder.descriptionView.setText(audio.getDescription());
        holder.descriptionView.setText("");
        holder.titleView.setText(_audioGuide.getTitle());
        holder.id_audioSD.setText(_audioGuide.getSdPosition().toString());
                
        //view.setOnClickListener( new MyOnClickListener() );
        view.setOnClickListener(this);
        return view;
    }
   
    public void onClick(View view) {
      
    	String id_audioSD = _audioGuide.getSdPosition().toString();
    	String language = _audioGuide.getLang();
    	String point = _audioGuide.getGeoPoint();
    	
//		// Starting new intent
		((_PlayListActivity) _context).clickOnSDAudio(id_audioSD,language,point);
		
    }
    public int getViewType() {
        return RowType.DESCRIPTION_ROW.ordinal();
    }

    private static class ViewHolder {
    	final TextView id_audioSD;
    	final TextView titleView;
        final TextView descriptionView;

        private ViewHolder(TextView id_audioSD,TextView titleView, TextView descriptionView) {
        	this.id_audioSD = id_audioSD;
        	this.titleView = titleView;
            this.descriptionView = descriptionView;
        }
    }
   
    
}
