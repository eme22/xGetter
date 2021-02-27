package com.htetznaing.lowcostvideo.Sites;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

public class MixDrop {

    private static WebView webView;
    private static LowCostVideo.OnTaskCompleted onTaskCompleted;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public static void fetch(Context context, String url, final LowCostVideo.OnTaskCompleted onDone ) {
        onTaskCompleted = onDone;

        try
        {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.74");
        webView.addJavascriptInterface(new MyInterface(),"xGetter");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(LowCostVideo.TAG, "FIND");
                findMe();
            }
        });



        webView.setDownloadListener((url1, userAgent, contentDisposition, mimetype, contentLength) ->
        {
            Log.d(LowCostVideo.TAG, "START DOWNLOAD");
            destroyWebView();
            result(url1);
        });


        String finalUrl = url;
        webView.loadUrl(url,new HashMap<String, String>() {{
            put("Referer", finalUrl);
            put("Origin", finalUrl);
        }});
    }

    private static String decodeBase64(String coded){
        try {
            return new String(Base64.decode(coded.getBytes("UTF-8"), Base64.DEFAULT));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void findMe() {
        if (webView != null) {
            String url = "javascript: (function() {" + decodeBase64(getJs()) + "})()";
            //String url = decodeBase64(getJs());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript(url, null);
            } else {
                webView.loadUrl(url);
            }

        }
    }

    private static final String getJs() {
        return "dmFyIGludGVydmFsID0gc2V0SW50ZXJ2YWwoZnVuY3Rpb24oKSB7CiAgICB2YXIgZWxlbSA9IGRvY3VtZW50LmdldEVsZW1lbnRzQnlDbGFzc05hbWUoInZqcy1iaWctcGxheS1idXR0b24iKVswXTsKCWlmICh0eXBlb2YgZWxlbSA9PSAndW5kZWZpbmVkJykgcmV0dXJuOwogICAgY2xlYXJJbnRlcnZhbChpbnRlcnZhbCk7CgllbGVtLmNsaWNrKCk7Cgl2YXIgaW50ZXJ2YWwyID0gc2V0SW50ZXJ2YWwoZnVuY3Rpb24oKSB7CgkJdmFyIHZpZGVvID0gZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoJ3ZpZGVvanNfaHRtbDVfYXBpJyk7CgkJaWYgKHR5cGVvZiB2aWRlby5zcmMgPT0gJ3VuZGVmaW5lZCcpIHJldHVybjsKCQljbGVhckludGVydmFsKGludGVydmFsMik7CgkJeEdldHRlci5sb2FkKHZpZGVvLnNyYykKCX0sIDEwKTsKfSwgMTApOw==";
    }

    private static void result(String result) {
        destroyWebView();
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


    private static void destroyWebView() {
        if (webView!=null) {
            webView.destroy();
        }
    }

    private static class MyInterface {
        @JavascriptInterface
        public void error(final String error) {
            new Handler(Looper.getMainLooper()).post(() ->
            {
                Log.e(LowCostVideo.TAG, "ERROR"+error);
                destroyWebView();
                result(null);
            });
        }

        @JavascriptInterface
        public void load(final String url) {
            new Handler(Looper.getMainLooper()).post(() ->
            {
                Log.d(LowCostVideo.TAG, "START DOWNLOAD");
                destroyWebView();
                result(url);
            });
        }
    }
}
