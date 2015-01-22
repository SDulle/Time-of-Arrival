package de.luh.hci.toa.applications.webapp;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WebViewport extends WebView {
	
	public WebViewport(Context context) {
		super(context);
		
		getSettings().setJavaScriptEnabled(true);
		
		System.out.println(getClass().getResource("test"));
		loadUrl("file:///android_asset/webContent/index.html");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(i<5) {
					right();
					++i;
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	int i=0;
	
	public void left() {
		loadUrl("javascript:prev()");
	}
	
	public void right() {
		loadUrl("javascript:next()");
	}
}
