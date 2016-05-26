package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import data.Collection;
import data.CollectionItem;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class CollectionItemAdapter extends BaseAdapter {
    private Context context;
    private Collection collection;

    public CollectionItemAdapter(Context context, Collection collection) {
        this.context = context;
        this.collection = collection;
    }

    @Override
    public int getCount() {
        return collection.getItems().size();
    }

    @Override
    public CollectionItem getItem(int position) {
        return collection.getItems().get(position);
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
