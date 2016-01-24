package it.duccius.download;

import it.duccius.musicplayer.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class _CustomListViewAdapter extends BaseAdapter {
 
    Context context;
    List<RowItem> rowItems;
 
    public _CustomListViewAdapter(Context context, List<RowItem> items) {
        this.context = context;
        this.rowItems = items;
    }
 
    private class ViewHolder {
        ImageView imageView;
        TextView titolo;
    }
 
    public int getCount() {
        return rowItems.size();
    }
 
    public Object getItem(int position) {
        return rowItems.get(position);
    }
 
    public long getItemId(int position) {
        return 0;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.thumbnail);
            holder.titolo = (TextView) convertView
                    .findViewById(R.id.titolo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
 
        RowItem rowItem = (RowItem) getItem(position);
        holder.imageView.setImageBitmap(rowItem.getBitmapImage());
        holder.titolo.setText(rowItem.getTitolo());
        
 
        return convertView;
    }
}
