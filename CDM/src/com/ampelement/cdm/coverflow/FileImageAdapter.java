package com.ampelement.cdm.coverflow;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * This class is an adapter that provides images from a fixed set of resource
 * ids. Bitmaps and ImageViews are kept as weak references so that they can be
 * cleared by garbage collection when not needed.
 * 
 */
public class FileImageAdapter extends AbstractCoverFlowImageAdapter {

    /** The Constant TAG. */
    private static final String TAG = FileImageAdapter.class.getSimpleName();

    /** The Constant DEFAULT_LIST_SIZE. */
    private static final int DEFAULT_LIST_SIZE = 20;

    /** The Constant IMAGE_RESOURCE_IDS. */
    private static final List<File> IMAGE_FILES = new ArrayList<File>(DEFAULT_LIST_SIZE);

    /** The bitmap map. */
    private final Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();

    private final Context context;

    /**
     * Creates the adapter with default set of resource images.
     * 
     * @param context
     *            context
     */
    public FileImageAdapter(final Context context, final String[] filePaths) {
        super();
        this.context = context;
        setResources(filePaths);
    }

    /**
     * Replaces resources with those specified.
     * 
     * @param resourceIds
     *            array of ids of resources.
     */
    public final synchronized void setResources(final String[] filePaths) {
        IMAGE_FILES.clear();
        for (final String filePath : filePaths) {
            IMAGE_FILES.add(context.getFileStreamPath(filePath));
        }
        notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public synchronized int getCount() {
        return IMAGE_FILES.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.polidea.coverflow.AbstractCoverFlowImageAdapter#createBitmap(int)
     */
    @Override
    protected Bitmap createBitmap(final int position) {
        Log.v(TAG, "creating item " + position);
        BitmapFactory bf = new BitmapFactory();
        final Bitmap bitmap = bf.decodeFile(IMAGE_FILES.get(position).getPath());
        bitmapMap.put(position, new WeakReference<Bitmap>(bitmap));
        return bitmap;
    }
}