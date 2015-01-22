package de.luh.hci.toa.applications;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WebViewport extends WebView {
	
	
	public WebViewport(Context context) {
		super(context);
		
		getSettings().setJavaScriptEnabled(true);
		
		loadUrl("http://google.de");
		
		
	}
	
	@JavascriptInterface
	public void test(String str) {
		System.out.println("TEST: "+str);
	}
	
	

}
