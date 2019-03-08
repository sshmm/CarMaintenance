package com.example.android.carmaintenance;

import java.util.ArrayList;

public class ImageUrls {

    private  ArrayList<String> urlList = new ArrayList<>();

    public ImageUrls(){
        urlList.add("https://images.pexels.com/photos/241316/pexels-photo-241316.jpeg");
        urlList.add("https://images.pexels.com/photos/337909/pexels-photo-337909.jpeg");
        urlList.add("https://images.pexels.com/photos/244206/pexels-photo-244206.jpeg");
        urlList.add("https://images.pexels.com/photos/1149137/pexels-photo-1149137.jpeg");
        urlList.add("https://images.pexels.com/photos/170811/pexels-photo-170811.jpeg");
    }


    private static int urlNum = 0;

    public String getLink(){
        int i = urlNum;
        if (urlNum < (urlList.size() -1)){
            urlNum++;
        } else{
            urlNum = 0;
        }

        return urlList.get(i);
    }
}
