package com.htetznaing.lowcostvideo.Sites;

import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class MegaNZ {

    private static LowCostVideo.OnTaskCompleted onTaskCompleted;

    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onComplete) {
        onTaskCompleted = onComplete;
        //if (url.contains("/embed/")) url = url.replace("/embed/", "/file/");
        //else if (url.contains("/embed#!")) url = url.replace("/embed#!", "/file/");
        try
        {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        result(url);
    }

    private static void result(String result) {
        System.out.println("Fucked: " + result);
        if (result != null && !result.isEmpty()) {
            ArrayList<XModel> xModels = new ArrayList<>();
            XModel model = new XModel();
            model.setUrl(result);
            model.setQuality("Normal");
            xModels.add(model);
            onTaskCompleted.onTaskCompleted(xModels, false);
        } else onTaskCompleted.onError();
    }

}
