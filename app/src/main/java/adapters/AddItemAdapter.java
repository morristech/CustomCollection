package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.OnChildSelectedListener;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ca.useful.customcollection.R;
import data.Bundles;
import data.CollectionItem;
import fragments.AddCollectionItemFragment;

public class AddItemAdapter extends BaseAdapter {
    private static final int TAKE_PICTURE = 1;
    private Context context;
    private CollectionItem item;
    private Uri imageUri;

    public AddItemAdapter(Context context, CollectionItem item) {
        this.context = context;
        this.item = item;
        if (this.item == null) {
            this.item = new CollectionItem();
        }
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
        final EditText et;
        switch(position) {
            case 0:
                //photo
                convertView = inflater.inflate(R.layout.item_photo, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_photo_title);
                HorizontalGridView gridView = (HorizontalGridView)convertView.findViewById(R.id.item_photo_gallery);
                tvTitle.setText(R.string.photos);
                HorizontalGalleryAdapter adapter = new HorizontalGalleryAdapter(context, item);
                gridView.setAdapter(adapter);
                gridView.setOnChildSelectedListener(new OnChildSelectedListener() {
                    @Override
                    public void onChildSelected(ViewGroup parent, View view, int position, long id) {
                        if (position == 0) {
                            takePhoto();
                        } else {

                        }
                    }
                });
                break;
            case 1:
                //name
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_string_title);
                et = (EditText)convertView.findViewById(R.id.item_string_text);
                tvTitle.setText(R.string.name);
                et.setHint(R.string.enter_name);

                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.setName(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                et.setText(item.getName());
                break;
            case 2:
                //description
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_string_title);
                et = (EditText)convertView.findViewById(R.id.item_string_text);
                tvTitle.setText(R.string.description);
                et.setHint(R.string.enter_description);
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.setDescription(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                et.setText(item.getDescription());
                break;
            case 3:
                //value
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_string_title);
                et = (EditText)convertView.findViewById(R.id.item_string_text);
                tvTitle.setText(R.string.value);
                et.setHint(R.string.enter_value);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString() != null && !s.toString().equals("")) {
                            item.setValue(Double.parseDouble(s.toString()));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                if (item.getValue() != 0.0) {
                    et.setText(Double.toString(item.getValue()));
                }
                break;
            case 4:
                //index
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_string_title);
                et = (EditText)convertView.findViewById(R.id.item_string_text);
                tvTitle.setText(R.string.reference);
                et.setHint(R.string.enter_reference);
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.setCustomIndexReminder(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                et.setText(item.getCustomIndexReminder());
                et.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
            default:
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_string_title);
                et = (EditText)convertView.findViewById(R.id.item_string_text);
                break;
        }
        return convertView;
    }

    public CollectionItem getCollectionItem() {
        return item;
    }

    public void takePhoto() {
        if (context != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMDDHHmmss");
            File photo = new File(Environment.getExternalStorageDirectory(), "Pic" + sdf.format(date) + ".png");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);
            Bundle uriBundle = new Bundle();
            uriBundle.putString(Bundles.IMAGEURI, imageUri.toString());
            uriBundle.putParcelable(Bundles.COLLECTIONITEMEXTRA, getCollectionItem());
            intent.putExtra(Bundles.REFERENCEEXTRA, uriBundle);
            ((Activity)context).startActivityForResult(intent, TAKE_PICTURE);
        }
    }
}
