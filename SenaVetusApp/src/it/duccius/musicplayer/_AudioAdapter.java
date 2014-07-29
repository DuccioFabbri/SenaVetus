package it.duccius.musicplayer;

import it.duccius.audioriga.DownloadRow;
import it.duccius.audioriga.Row;
import it.duccius.audioriga.RowType;
import it.duccius.audioriga.SdRow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class _AudioAdapter extends BaseAdapter {
        final List<Row> rows;

        _AudioAdapter(List<_Audio> audios, Context context) {
            rows = new ArrayList<Row>();//member variable

            for (_Audio audio : audios) {
                //if it has an image, use an ImageRow
                if (audio.getToBeDownloaded()) {
                    rows.add(new DownloadRow(LayoutInflater.from(context), audio, context));
                } else {//otherwise use a DescriptionRow
                    rows.add(new SdRow(LayoutInflater.from(context), audio, context));
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

