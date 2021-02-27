package com.htetznaing.lowcostvideo.Sites;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;


public class Zippy {

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
            final String fileName= URLUtil.guessFileName(url1,contentDisposition,mimetype);
            if (webView.getTitle().contains(fileName)) {
                destroyWebView();
                result(url1);
            }
        });

        webView.loadUrl(url);
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
        if (webView!=null) {
            String url = "javascript: (function() {" + decodeBase64(getJs()) + "})()";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript(url, null);
            } else {
                webView.loadUrl(url);
            }

        }
    }

    private static String getJs(){
        return "dmFyIHNyYyA9IGRvY3VtZW50LmdldEVsZW1lbnRCeUlkKCdkbGJ1dHRvbicpOwpkbChzcmMpOwoKZnVuY3Rpb24gZGwodXJsKSB7CiAgICB2YXIgYW5jaG9yID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnYScpOwogICAgYW5jaG9yLnNldEF0dHJpYnV0ZSgnaHJlZicsIHVybCk7CiAgICBhbmNob3Iuc2V0QXR0cmlidXRlKCdkb3dubG9hZCcsIGRvY3VtZW50LnRpdGxlKTsKICAgIGFuY2hvci5zdHlsZS5kaXNwbGF5ID0gJ25vbmUnOwogICAgZG9jdW1lbnQuYm9keS5hcHBlbmRDaGlsZChhbmNob3IpOwogICAgYW5jaG9yLmNsaWNrKCk7CiAgICBkb2N1bWVudC5ib2R5LnJlbW92ZUNoaWxkKGFuY2hvcik7Cn0=";
    }

    private static void result(String result)
    {
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
        @SuppressWarnings("unused")
        @JavascriptInterface
        public void error(final String error) {
            new Handler(Looper.getMainLooper()).post(() ->
            {
                Log.e(LowCostVideo.TAG, "ERROR"+error);
                destroyWebView();
                result(null);
            });
        }
    }
}
