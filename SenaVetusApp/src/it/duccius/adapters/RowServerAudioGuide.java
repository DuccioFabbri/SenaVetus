package it.duccius.adapters;
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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import it.duccius.musicplayer._PlayListActivity;
import it.duccius.musicplayer.R;
import it.duccius.musicplayer.AudioGuide;

public class RowServerAudioGuide implements Row, OnClickListener {
    private final AudioGuide _audio;
    private final LayoutInflater _inflater;
    private Context _context;
    
    public RowServerAudioGuide(LayoutInflater inflater, AudioGuide audio, Context context) {
        this._audio = audio;
        this._inflater = inflater;
        this._context = context;
    }

    public View getView(View convertView) {
        ViewHolder holder;
        View view;
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null) {
            ViewGroup viewGroup = (ViewGroup)_inflater.inflate(R.layout.download_row, null);

            //use the view holder pattern to save of already looked up subviews
            holder = new ViewHolder((ImageView)viewGroup.findViewById(R.id.image),
                    (TextView)viewGroup.findViewById(R.id.title),
                    (TextView)viewGroup.findViewById(R.id.downloadUrl));
            viewGroup.setTag(holder);

            view = viewGroup;
        } else {
            //get the holder back out
            holder = (ViewHolder)convertView.getTag();

            view = convertView;
        }

        //actually setup the view
//        holder.imageView.setImageResource(audio.getImageId());
        holder.titleView.setText(_audio.getTitle());
        view.setOnClickListener(this);
        return view;
    }

    public int getViewType() {
        return RowType.IMAGE_ROW.ordinal();
    }

    private static class ViewHolder {
        final ImageView imageView;
        final TextView titleView;
        final TextView downloadUrl;
        

        private ViewHolder(ImageView imageView, TextView titleView, TextView downloadUrl) {
            this.imageView = imageView;
            this.titleView = titleView;
            this.downloadUrl = downloadUrl;
        }
    }

	@Override
	public void onClick(View view) {		
		((_PlayListActivity) _context).clickOnDownloadAudio(((TextView)view.findViewById(R.id.title)).getText().toString());		
		
	}

}
