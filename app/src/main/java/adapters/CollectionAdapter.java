package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import data.Collection;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class CollectionAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Collection> collections = new ArrayList<>();

    public CollectionAdapter(Context context, ArrayList<Collection> collections) {
        this.context = context;
        this.collections = collections;
    }

    @Override
    public int getCount() {
        return collections.size();
    }

    @Override
    public Collection getItem(int position) {
        return collections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
