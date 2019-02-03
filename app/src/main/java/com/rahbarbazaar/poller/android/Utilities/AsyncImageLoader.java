package com.rahbarbazaar.poller.android.Utilities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.lang.ref.WeakReference;

public class AsyncImageLoader extends AsyncTask<String, Void, ImageRequest> {

    /**
     * we have to define image object as weak reference for collect by garbage collector
     * NOTICE: it will be improve performance
     */
    private WeakReference<SimpleDraweeView> image;
    private int width, height;

    public AsyncImageLoader(SimpleDraweeView img, int height, int width) {
        image = new WeakReference<>(img);
        this.width = width;
        this.height = height;
    }

    @Override
    protected ImageRequest doInBackground(String... strings) {

        //create image request with url:
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(strings[0])).setResizeOptions(new ResizeOptions(width, height)).
                setImageDecodeOptions(new ImageDecodeOptions(ImageDecodeOptions.newBuilder().setBitmapConfig(Bitmap.Config.RGB_565))).
                setCacheChoice(ImageRequest.CacheChoice.SMALL);
        return builder.build();
    }

    @Override
    protected void onPostExecute(ImageRequest imageRequest) {

        SimpleDraweeView simpleDraweeView = image.get();

        if (simpleDraweeView != null) {

            if (imageRequest != null) {

                simpleDraweeView.setImageRequest(imageRequest);
            } else {

                // on error state
            }
        }
    }
}
