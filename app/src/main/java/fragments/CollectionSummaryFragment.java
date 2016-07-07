package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ca.useful.customcollection.R;
import data.Collection;
import data.DatabaseHelper;

public class CollectionSummaryFragment extends Fragment {

    ArrayList<Collection> collections = new ArrayList<>();
    GridAdapter adapter;
    GridView gridView;
    TextView tvTitle;

    public static CollectionSummaryFragment newInstance() {
        CollectionSummaryFragment fragment = new CollectionSummaryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        bind();
    }

    private void bind() {
        if (getActivity() != null) {
            DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
            collections = databaseHelper.getCollections();
            databaseHelper.close();
            tvTitle = (TextView)getView().findViewById(R.id.collection_summary_title);
            gridView = (GridView)getView().findViewById(R.id.collection_summary_gridview);
            adapter = new GridAdapter();
            tvTitle.setText(R.string.collection_summary);
            gridView.setAdapter(adapter);
        }
    }

    private class GridAdapter extends BaseAdapter implements View.OnClickListener {

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
            //TODO priority, materials
            if (getActivity() != null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.item_collection_summary, parent, false);
                TextView tvName = (TextView)convertView.findViewById(R.id.collection_summary_name);
                TextView tvCount = (TextView)convertView.findViewById(R.id.collection_summary_item_count);
                TextView tvValue = (TextView)convertView.findViewById(R.id.collection_summary_item_value);
                Button btnExport = (Button)convertView.findViewById(R.id.collection_summary_item_export);
                final Collection collection = getItem(position);
                tvName.setText(collection.getTitle());
                tvCount.setText(Integer.toString(collection.getItems().size()));
                DecimalFormat df = new DecimalFormat("#.00");
                tvValue.setText("$" + df.format(collection.getValueSum()));
                btnExport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (getActivity() != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                            View dialogView = inflater.inflate(R.layout.dialog_export, null);
                            builder.setView(dialogView);
                            final AlertDialog dialog = builder.show();
                            ListView lv = (ListView)dialog.findViewById(R.id.dialog_export_listview);
                            final ExportAdapter adapter = new ExportAdapter(collection.getId());
                            lv.setAdapter(adapter);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    dialog.dismiss();
                                    adapter.performPositionClick(position);
                                }
                            });
                        }
                    }
                });
            }

            return convertView;
        }

        @Override
        public void onClick(View v) {

        }
    }

    protected class ExportAdapter extends BaseAdapter {
        int collectionId;

        public ExportAdapter(int collectionId) {
            this.collectionId = collectionId;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public String getItem(int position) {
            switch(position) {
                case 0:
                    return "CSV";
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
            if (getActivity() != null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.item_textview, parent, false);
                TextView tv = (TextView)convertView.findViewById(R.id.item_textview_text);
                tv.setText(getItem(position));
            }
            return convertView;
        }

        protected void performPositionClick(int position) {
            switch (position) {
                case 0:
                    //csv
                    DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                    databaseHelper.writeCSV(collectionId);
                    databaseHelper.close();
                    break;
                default:

                    break;
            }
        }
    }
}
