package edu.union.android;

import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;

public class ModelViewer extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		InputStream is = getResources().openRawResource(R.drawable.tris);
		try {
			Model model = MD2Loader.load(is, 0.1f, "skin.jpg");
			if (model.getFrameCount() > 1)
				setContentView(new ModelViewInterpolated(model, this));
			else
				setContentView(new ModelView(model, this));
		} 
		catch (java.io.IOException ex) {
			setContentView(R.layout.main);
		}
	}
}