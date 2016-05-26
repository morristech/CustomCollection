package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ca.useful.customcollection.R;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.item_collection_item, parent, false);
        ImageView iv = (ImageView)convertView.findViewById(R.id.collection_item_imageview);
        TextView tvName = (TextView)convertView.findViewById(R.id.collection_item_name);
        TextView tvDescription = (TextView)convertView.findViewById(R.id.collection_item_description);
        TextView tvValue = (TextView)convertView.findViewById(R.id.collection_item_value);
        TextView tvReference = (TextView)convertView.findViewById(R.id.collection_item_reference_index);
        CollectionItem item = getItem(position);
        if (item.getPhoto() != null) {
            iv.setImageBitmap(item.getPhoto());
        }
        tvName.setText(item.getName());
        if (item.getDescription().equals("")) {
            tvDescription.setVisibility(View.GONE);
        } else {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(item.getDescription());
        }
        if (item.getValue() == 0D) {
            tvValue.setVisibility(View.GONE);
        } else {
            tvValue.setVisibility(View.VISIBLE);
            tvValue.setText("$" + item.getValue());
        }
        if (item.getCustomIndexReminder().equals("")) {
            tvReference.setVisibility(View.GONE);
        } else {
            tvReference.setVisibility(View.VISIBLE);
            tvReference.setText(item.getCustomIndexReminder());
        }
        return convertView;
    }

}
