package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import ca.useful.customcollection.R;
import data.Collection;
import data.CollectionItem;

public class MissingValueAdapter  extends BaseAdapter {
    private Context context;
    private Collection collection;

    public MissingValueAdapter(Context context, Collection collection) {
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
        ImageView iv = (ImageView) convertView.findViewById(R.id.collection_item_imageview);
        TextView tvName = (TextView) convertView.findViewById(R.id.collection_item_name);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.collection_item_description);
        TextView tvValue = (TextView) convertView.findViewById(R.id.collection_item_value);
        TextView tvReference = (TextView) convertView.findViewById(R.id.collection_item_reference_index);
        CollectionItem item = getItem(position);
        if (item.getPhotos() != null && !item.getPhotos().isEmpty()) {
            iv.setImageBitmap(item.getPhotos().get(0).getPhotosAsBitmap());
        }
        tvName.setText(item.getName());

        tvDescription.setVisibility(View.VISIBLE);
        tvDescription.setText(item.getDescription());

        DecimalFormat df = new DecimalFormat("#.00");
        tvValue.setVisibility(View.VISIBLE);
        tvValue.setText("$" + df.format(item.getValue()));

        tvReference.setVisibility(View.VISIBLE);
        tvReference.setText(item.getCustomIndexReminder());

        return convertView;
    }
}