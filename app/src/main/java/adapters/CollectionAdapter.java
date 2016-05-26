package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ca.useful.customcollection.R;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.item_collection, parent, false);
        TextView tvName = (TextView)convertView.findViewById(R.id.collection_name);
        TextView tvNumber = (TextView)convertView.findViewById(R.id.collection_number_items);
        Collection item = getItem(position);
        tvName.setText(item.getTitle());
        tvNumber.setText(item.getItems().size() + " items");
        return convertView;
    }
}
