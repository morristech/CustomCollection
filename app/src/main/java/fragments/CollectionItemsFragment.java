package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import adapters.CollectionItemAdapter;
import adapters.MissingValueAdapter;
import ca.useful.customcollection.R;
import data.Collection;
import data.CollectionItem;
import data.DatabaseHelper;

public class CollectionItemsFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = "CollectionItemsFrag";
    private GridView gridView;
    private TextView tvTitle;
    private Collection collection;
    private CollectionItemAdapter adapter;
    private MissingValueAdapter missingValueAdapter;
    private AddCollectionItemFragment addCollectionItemFragment;

    //TODO Compare, Quick Search Title, Checkbox Delete, OCR Text, Add Fancy Graphs, Stock Prices, Raw Material Make, Related Products, Grade

    public static CollectionItemsFragment newInstance(Collection collection) {
        CollectionItemsFragment fragment = new CollectionItemsFragment();
        Bundle b = new Bundle();
        b.putParcelable("collection", collection);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super .onViewCreated(view, savedInstance);
        if (getArguments() != null) {
            if (getArguments().getParcelable("collection") != null) {
                collection = getArguments().getParcelable("collection");
            }
        }
        setUpGridView();
    }

    private void setUpGridView() {
        if (getActivity() != null) {
            gridView = (GridView)getView().findViewById(R.id.collection_item_gridview);
            tvTitle = (TextView)getView().findViewById(R.id.fragment_collection_item_title);

            DatabaseHelper helper = new DatabaseHelper(getActivity());
            if (collection.getId() != -1) {
                collection.setItems(helper.getCollectionItems(collection.getId()));
                collection.processPhotos(getActivity());
                if (collection != null) {
                    adapter = new CollectionItemAdapter(getActivity(), collection);
                    gridView.setAdapter(adapter);
                    gridView.setOnItemClickListener(this);
                    gridView.setOnItemLongClickListener(this);
                    tvTitle.setText(collection.getTitle());
                }
            } else {
                collection.setItems(helper.getCollectionItemsWithNoAssignedValue());
                collection.processPhotos(getActivity());
                if (collection != null) {
                    missingValueAdapter = new MissingValueAdapter(getActivity(), collection);
                    gridView.setAdapter(adapter);
                    gridView.setOnItemClickListener(this);
                    gridView.setOnItemLongClickListener(this);
                    tvTitle.setText(collection.getTitle());
                }
            }
            helper.close();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getActivity() != null) {
            if (missingValueAdapter != null) {
                addCollectionItemFragment = AddCollectionItemFragment.newInstance(collection.getItems().get(position));
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_layout, addCollectionItemFragment, "addCollectionFragment")
                        .addToBackStack("addCollectionFragment")
                        .commit();
            } else {
                if (position != 0) {
                    addCollectionItemFragment = AddCollectionItemFragment.newInstance(collection.getItems().get(position - 1));
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_layout, addCollectionItemFragment, "addCollectionFragment")
                            .addToBackStack("addCollectionFragment")
                            .commit();
                } else {
                    CollectionItem item = new CollectionItem();
                    item.setFkCollectionId(collection.getId());
                    addCollectionItemFragment = AddCollectionItemFragment.newInstance(item);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_layout, addCollectionItemFragment, "addCollectionFragment")
                            .addToBackStack("addCollectionFragment")
                            .commit();
                }
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if (position > 0) {
            if (getActivity() != null) {
                if (missingValueAdapter != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    View dialogView = inflater.inflate(R.layout.dialog_yes_no, null);
                    builder.setView(dialogView);
                    final AlertDialog dialog = builder.show();
                    Button btnYes = (Button) dialog.findViewById(R.id.dialog_yes_no_button_yes);
                    Button btnNo = (Button) dialog.findViewById(R.id.dialog_yes_no_button_no);
                    TextView description = (TextView) dialog.findViewById(R.id.dialog_yes_no_description);
                    TextView title = (TextView) dialog.findViewById(R.id.dialog_yes_no_title);
                    title.setText(R.string.delete_item);
                    description.setText(R.string.delete_this_item);
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getActivity() != null) {
                                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                                databaseHelper.deleteCollectionItem(collection.getItems().get(position));
                                databaseHelper.close();
                                collection.getItems().remove(position);
                                missingValueAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    View dialogView = inflater.inflate(R.layout.dialog_yes_no, null);
                    builder.setView(dialogView);
                    final AlertDialog dialog = builder.show();
                    Button btnYes = (Button) dialog.findViewById(R.id.dialog_yes_no_button_yes);
                    Button btnNo = (Button) dialog.findViewById(R.id.dialog_yes_no_button_no);
                    TextView description = (TextView) dialog.findViewById(R.id.dialog_yes_no_description);
                    TextView title = (TextView) dialog.findViewById(R.id.dialog_yes_no_title);
                    title.setText(R.string.delete_item);
                    description.setText(R.string.delete_this_item);
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getActivity() != null) {
                                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                                databaseHelper.deleteCollectionItem(collection.getItems().get(position - 1));
                                databaseHelper.close();
                                collection.getItems().remove(position - 1);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                }
            }
        }
        return true;
    }
}
