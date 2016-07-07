package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import ca.useful.customcollection.R;
import data.Collection;
import data.CollectionItem;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class CollectionItemAdapter extends BaseAdapter {
    private Context context;
    private Collection collection;
    ArrayList<CollectionItem> items = new ArrayList<>();
    public CollectionItemAdapter(Context context, Collection collection) {
        this.context = context;
        this.collection = collection;
        this.items = collection.getItems();
    }

    @Override
    public int getCount() {
        return items.size() + 1;
    }

    @Override
    public CollectionItem getItem(int position) {
        if (position == 0) {
            return null;
        }
        return items.get(position - 1);
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
        if (position == 0) {
            iv.setImageResource(R.drawable.greenadd);
            tvName.setText(R.string.add);
            tvDescription.setVisibility(View.GONE);
            tvValue.setVisibility(View.GONE);
            tvReference.setVisibility(View.GONE);
        } else {
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

        }
        return convertView;
    }
}
