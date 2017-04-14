package jswebproduction.searchbaractivity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

/**
 * Created by Jsweb Stage 2 on 07/04/2017.
 */

public class CustomAdapter extends BaseAdapter implements ListAdapter {
    private static final int        TYPE_MAX_COUNT = 2;
    private ArrayList               data = new ArrayList();
    private Context                 context = null;

    public CustomAdapter(Context context) {
        this.context = context;
    }

    public void addItem(SearchWidget item) {
        this.data.add(item);
        notifyDataSetChanged();
    }

    public void addItem(SearchWidget item, int position) {
        this.data.add(position, item);
        notifyDataSetChanged();
    }

    public void clear() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        this.data.remove(position);
        notifyDataSetChanged();
    }

    public void removeItem(SearchWidget item) {
        this.data.remove(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Object getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            SearchWidget widget = (SearchWidget) data.get(position);
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(widget.getLayoutId(), parent, false);
            holder = widget.getHolder();
            holder.setViewElements(convertView);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.updateViewElements(this.data.get(position));
        return convertView;
    }
}
