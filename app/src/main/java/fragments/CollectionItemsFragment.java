package fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import adapters.CollectionItemAdapter;
import ca.useful.customcollection.MainActivity;
import ca.useful.customcollection.R;
import data.Collection;

public class CollectionItemsFragment extends Fragment {
    private static final String TAG = "CollectionItemsFrag";
    private GridView gridView;
    private Collection collection;
    private CollectionItemAdapter adapter;
    private String message = "";
    public static CollectionItemsFragment newInstance(Collection collection) {
        CollectionItemsFragment fragment = new CollectionItemsFragment();
        Bundle b = new Bundle();
        b.putParcelable("collection", collection);
        fragment.setArguments(b);
        return fragment;
    }

    public static CollectionItemsFragment newInstance(String message) {
        CollectionItemsFragment fragment = new CollectionItemsFragment();
        Bundle b = new Bundle();
        b.putString("message", message);
        fragment.setArguments(b);
        return fragment;
    }

    public void changeArguments(Collection collection) {
        if (getArguments().containsKey("collection")) {
            getArguments().remove("collection");
            getArguments().putParcelable("collection", collection);
        }
    }

    public void changeArguments(String message) {
        if (getArguments().containsKey("message")) {
            getArguments().remove("message");
            getArguments().putString("message", message);
        }
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
            if (getArguments().getString("message") != null) {
                message = getArguments().getString("message");
            }
            if (getArguments().getParcelable("collection") != null) {
                collection = getArguments().getParcelable("collection");
            }
        }
        setUpGridView();
    }

    private void setUpGridView() {
        if (getActivity() != null) {
            gridView = (GridView)getActivity().findViewById(R.id.collection_item_gridview);
            if (collection != null) {
                adapter = new CollectionItemAdapter(getActivity(), collection);
                gridView.setAdapter(adapter);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getActivity().getString(R.string.collection_item_select_collection));
                builder.setMessage(message);
                builder.setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity)getActivity()).popToCollectionPage();
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }
    }

        /*
    TODO: XML Resource With Gridview, populate, onItemClick inflates the item's details, Add New item, Delete item
     */
}
