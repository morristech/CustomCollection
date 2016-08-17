package adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import ca.useful.customcollection.R;
import data.CollectionItem;
import data.CollectionItemPhoto;
import data.DatabaseHelper;
import data.Material;
import droidninja.filepicker.FilePickerBuilder;
import listeners.RecyclerItemClickListener;

public class AddItemAdapter extends BaseAdapter {
    private static final int TAKE_PICTURE = 1;
    private Context context;
    private CollectionItem item;

    public AddItemAdapter(Context context, CollectionItem item) {
        this.context = context;
        this.item = item;
        if (this.item == null) {
            this.item = new CollectionItem();
        }
    }

    @Override
    public int getCount() {
        return 6;
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
        TextView tvTitle;
        final EditText et;
        switch (position) {
            case 0:
                //photo
                convertView = inflater.inflate(R.layout.item_photo, parent, false);
                tvTitle = (TextView) convertView.findViewById(R.id.item_photo_title);
                RecyclerView recyclerView = (RecyclerView) convertView.findViewById(R.id.item_photo_gallery);
                tvTitle.setText(R.string.photos);
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);
                HorizontalGalleryAdapter adapter = new HorizontalGalleryAdapter(context, item);
                recyclerView.setAdapter(adapter);
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        if (position == 0) {
                            takePhoto();
                        } else {
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_yes_no);
                            Button btnYes = (Button) dialog.findViewById(R.id.dialog_yes_no_button_yes);
                            Button btnNo = (Button) dialog.findViewById(R.id.dialog_yes_no_button_no);
                            TextView description = (TextView) dialog.findViewById(R.id.dialog_yes_no_description);
                            TextView title = (TextView) dialog.findViewById(R.id.dialog_yes_no_title);

                            title.setText(R.string.delete_photo);
                            description.setText(R.string.delete_this_photo);
                            btnNo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            btnYes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CollectionItemPhoto photo = item.getPhotos().get(position - 1);
                                    Uri imgUri = Uri.parse(photo.getPhotoUri());
                                    if (photo.getId() != -1) {
                                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                                        databaseHelper.deletePhoto(photo.getId());
                                        databaseHelper.close();
                                    }
                                    item.getPhotos().remove(position - 1);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    }
                }));
                break;
            case 1:
                //name
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView) convertView.findViewById(R.id.item_string_title);
                et = (EditText) convertView.findViewById(R.id.item_string_text);
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
                tvTitle = (TextView) convertView.findViewById(R.id.item_string_title);
                et = (EditText) convertView.findViewById(R.id.item_string_text);
                tvTitle.setText(R.string.description);
                et.setHint(R.string.enter_description);
                et.setSingleLine(false);
                et.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
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
                tvTitle = (TextView) convertView.findViewById(R.id.item_string_title);
                et = (EditText) convertView.findViewById(R.id.item_string_text);
                tvTitle.setText(R.string.value);
                et.setHint(R.string.enter_value);
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s != null && !s.toString().equals("") && !s.toString().equals(".")) {
                            item.setValue(Double.parseDouble(s.toString()));
                        } else {
                            item.setValue(0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                et.setText(Double.toString(item.getValue()));
                break;
            case 4:
                //index
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                tvTitle = (TextView) convertView.findViewById(R.id.item_string_title);
                et = (EditText) convertView.findViewById(R.id.item_string_text);
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
            case 5:
                //material
                convertView = inflater.inflate(R.layout.item_material, parent, false);
                tvTitle = (TextView)convertView.findViewById(R.id.item_material_title);
                final Spinner spinner = (Spinner)convertView.findViewById(R.id.item_material_spinner);
                ImageButton btnAddNew = (ImageButton)convertView.findViewById(R.id.item_material_add_button);
                final MaterialAdapter spAdapter = new MaterialAdapter(context);
                spinner.setAdapter(spAdapter);
                tvTitle.setText(R.string.material_title);
                btnAddNew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setView(R.layout.dialog_edittext);
                        final AlertDialog dialog = builder.show();
                        Button btnOk = (Button)dialog.findViewById(R.id.dialog_edittext_ok_button);
                        final EditText etText = (EditText)dialog.findViewById(R.id.dialog_edittext_text);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                                databaseHelper.insertMaterial(etText.getText().toString());
                                databaseHelper.close();
                                if (spinner.getAdapter() != null) {
                                    ((MaterialAdapter)spinner.getAdapter()).notifyDataSetChanged();
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                });
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        item.setFkMaterialId(spAdapter.getItem(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                if (item.getFkMaterialId() != -1) {
                    spinner.setSelection(spAdapter.getPositionFromItemId(item.getFkMaterialId()));
                }
                break;
            default:
                convertView = inflater.inflate(R.layout.item_string, parent, false);
                break;
        }
        return convertView;
    }

    public void takePhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.dialog_export);
        final AlertDialog dialog = builder.show();
        ListView lv  = (ListView)dialog.findViewById(R.id.dialog_export_listview);
        final PhotoTypeAdapter adapter = new PhotoTypeAdapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.performPositionClick(position);
                dialog.dismiss();
            }
        });
    }

    private class PhotoTypeAdapter extends BaseAdapter {

        public PhotoTypeAdapter() {}

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public String getItem(int position) {
            switch(position) {
                case 0:
                    return "Take Picture";
                case 1:
                    return "Select Existing Picture";
                default:
                    return "N/A";
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_textview, parent, false);
            TextView tv = (TextView)convertView.findViewById(R.id.item_textview_text);
            tv.setText(getItem(position));
            return convertView;
        }

        public void performPositionClick(int position) {
            switch (position) {
                case 0:
                    if (context != null) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        ((Activity) context).startActivityForResult(intent, TAKE_PICTURE);
                    }
                    break;
                case 1:
                    //select file
                    FilePickerBuilder.getInstance().setMaxCount(3)
                            .setActivityTheme(R.style.AppTheme)
                            .pickPhoto((Activity)context);
                    break;
                default:

                    break;
            }
        }

    }
}
