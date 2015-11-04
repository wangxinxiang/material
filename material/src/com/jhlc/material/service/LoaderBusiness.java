package com.jhlc.material.service;

import android.widget.ImageView;

import com.jhlc.material.R;
import com.jhlc.material.ZXLApplication;
import com.jhlc.material.utils.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class LoaderBusiness {
    private static LoaderBusiness loaderBusiness = new LoaderBusiness();
    public ImageLoader imageLoader;
    private DisplayImageOptions options;
    private DisplayImageOptions payoptions;
    private DisplayImageOptions PhotoOptions;
    private ImageLoadingListener animateFirstListener;

    private LoaderBusiness(){
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(ZXLApplication.getInstance()));
        animateFirstListener = new AnimateFirstDisplayListener();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.defult_user_headimg)
                .showImageForEmptyUri(R.drawable.defult_user_headimg)
                .showImageOnFail(R.drawable.defult_user_headimg)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
//              .displayer(new RoundedBitmapDisplayer(20))
                .build();

        payoptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.take_invoice_bg)
                .showImageForEmptyUri(R.drawable.take_invoice_bg)
                .showImageOnFail(R.drawable.take_invoice_bg)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
//              .displayer(new RoundedBitmapDisplayer(20))
                .build();

        PhotoOptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.takephoto_normal)
                .showImageForEmptyUri(R.drawable.takephoto_normal)
                .showImageOnFail(R.drawable.takephoto_normal)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
//              .displayer(new RoundedBitmapDisplayer(20))
                .build();
    };
    public static LoaderBusiness getInstance(){
        return loaderBusiness;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public DisplayImageOptions getOptions() {
        return options;
    }

    public DisplayImageOptions getPayoptions() {
        return payoptions;
    }

    public DisplayImageOptions getPhotoOptions() {
        return PhotoOptions;
    }

    public ImageLoadingListener getAnimateFirstListener() {
        return animateFirstListener;
    }

    public static void loadImage(String url , ImageView iv){
        DisplayImageOptions options = getInstance().getOptions();
        ImageLoadingListener animateFirstListener = getInstance().getAnimateFirstListener();
        getInstance().getImageLoader().displayImage(url, iv, options, animateFirstListener);
    }

    public static void loadPayImage(String url , ImageView iv){
        DisplayImageOptions options = getInstance().getPayoptions();
        ImageLoadingListener animateFirstListener = getInstance().getAnimateFirstListener();
        getInstance().getImageLoader().displayImage(url, iv, options, animateFirstListener);
    }

    public static void loadPhotoImage(String url , ImageView iv){
        DisplayImageOptions options = getInstance().getPhotoOptions();
        ImageLoadingListener animateFirstListener = getInstance().getAnimateFirstListener();
        getInstance().getImageLoader().displayImage(url, iv, options, animateFirstListener);
    }
}
