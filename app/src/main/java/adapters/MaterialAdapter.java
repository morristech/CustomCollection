package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ca.useful.customcollection.R;
import data.DatabaseHelper;
import data.Material;

public class MaterialAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Material> materials;

    public MaterialAdapter(Context context) {
        this.context = context;
        populateMaterials();
    }

    private void populateMaterials() {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        materials = databaseHelper.getMaterials();
        databaseHelper.close();
    }

    @Override
    public int getCount() {
        return materials.size();
    }

    @Override
    public Material getItem(int position) {
        return materials.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.item_spinner_textview, parent, false);
        TextView tv = (TextView)convertView.findViewById(R.id.item_spinner_textview_textview);
        tv.setText(getItem(position).getName());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        populateMaterials();
        super .notifyDataSetChanged();

    }

    public int getPositionFromItemId(int fkMaterialId) {
        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i).getId() == fkMaterialId) {
                return i;
            }
        }
        return 0;
    }
}
