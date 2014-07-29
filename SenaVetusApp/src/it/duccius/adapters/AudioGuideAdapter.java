package it.duccius.adapters;

import it.duccius.musicplayer.AudioGuide;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class AudioGuideAdapter extends BaseAdapter {
        final List<Row> rows;

        public AudioGuideAdapter(List<AudioGuide> audios, Context context) {
            rows = new ArrayList<Row>();//member variable

            for (AudioGuide audio : audios) {
                //if it has an image, use an ImageRow
                if (audio.getToBeDownloaded()) {
                    rows.add(new RowServerAudioGuide(LayoutInflater.from(context), audio, context));
                } else {//otherwise use a DescriptionRow
                    rows.add(new RowSdAudioGuide(LayoutInflater.from(context), audio, context));
                }
            }
        }

        @Override
        public int getViewTypeCount() {
            return RowType.values().length;
        }

        @Override
        public int getItemViewType(int position) {
            return rows.get(position).getViewType();
        }

        public int getCount() {
            return rows.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {        	
            return rows.get(position).getView(convertView);
        }
        
        
    }

