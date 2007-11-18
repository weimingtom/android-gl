package edu.union;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Canvas;

public class GLTutorialOne extends GLTutorialBase {
	public GLTutorialOne(Context c) {
		super(c);
		GL10 gl = (GL10)glContext.getGL();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	protected void onDraw(Canvas canvas) {
		GL10 gl = (GL10)glContext.getGL();
		
		glContext.waitNative(canvas, this);
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}
}
