package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import ca.useful.customcollection.R;
import data.CollectionItem;

public class HorizontalGalleryAdapter extends  RecyclerView.Adapter {
    private Context context;
    private CollectionItem item;

    public HorizontalGalleryAdapter(Context context, CollectionItem item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gridview_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ((PhotoViewHolder)holder).img.setImageResource(R.drawable.add);
        } else {
            ((PhotoViewHolder)holder).img.setImageBitmap(item.getPhotos().get(position-1).getPhotosAsBitmap());
        }
    }

    @Override
    public int getItemCount() {
        return item.getPhotos().size() + 1;
    }

    private class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.item_gridview_imageview);
        }
    }
}
