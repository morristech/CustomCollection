package adapters;

import android.content.Context;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import ca.useful.customcollection.R;
import data.CollectionItem;

public class AddItemAdapter extends BaseAdapter {
    private Context context;
    private CollectionItem item;

    public AddItemAdapter(Context context, CollectionItem item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        TextView tvTitle = null;
        EditText et = null;
        switch(position) {
            case 0:
                //photo
                convertView = inflater.inflate(R.layout.item_photo, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_photo_title);
                HorizontalGridView gridView = (HorizontalGridView)convertView.findViewById(R.id.item_photo_gallery);
                HorizontalGalleryAdapter adapter = new HorizontalGalleryAdapter(context, item);
                gridView.setAdapter(adapter);
                break;
            case 1:
                //name
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_string_title);
                et = (EditText)convertView.findViewById(R.id.item_string_text);
                tvTitle.setText(R.string.name);
                et.setHint(R.string.enter_name);
                break;
            case 2:
                //description
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_string_title);
                et = (EditText)convertView.findViewById(R.id.item_string_text);
                tvTitle.setText(R.string.description);
                et.setHint(R.string.enter_description);
                break;
            case 3:
                //value
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_string_title);
                et = (EditText)convertView.findViewById(R.id.item_string_text);
                tvTitle.setText(R.string.value);
                et.setHint(R.string.enter_value);
                break;
            case 4:
                //index
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_string_title);
                et = (EditText)convertView.findViewById(R.id.item_string_text);
                tvTitle.setText(R.string.reference);
                et.setHint(R.string.enter_reference);
                break;
            case 5:
                //ok button
                break;
            default:
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_string_title);
                et = (EditText)convertView.findViewById(R.id.item_string_text);
                break;
        }
        return convertView;
    }
}
