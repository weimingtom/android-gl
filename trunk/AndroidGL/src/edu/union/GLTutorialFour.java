package edu.union;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLU;

/**
 * http://www.zeuscmd.com/tutorials/opengles/10-Transformations.php
 * @author bburns
 */
public class GLTutorialFour extends GLTutorialBase {
	float[] triangle = new float[] { -0.25f, -0.25f, 0.0f,
									  0.25f, -0.25f, 0.0f,
									 -0.25f,  0.25f, 0.0f };
	
	float[] square = new float[] { 	-0.25f, -0.25f, 0.0f,
			0.25f, -0.25f, 0.0f,
			-0.25f, 0.25f, 0.0f,
			0.25f, 0.25f, 0.0f };
	
	float[] colors = new float[] { 	1, 0, 0, 1,
									0, 1, 0, 1,
									0, 0, 1, 1 };

	FloatBuffer squareBuff;
	FloatBuffer triangleBuff;
	FloatBuffer colorBuff;

	float xrot = 0.0f;
	float yrot = 0.0f;
	
	public GLTutorialFour(Context c) {
		super(c, 20);

		GL10 gl = (GL10)glContext.getGL();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0.0f,1.3f,0.0f,1.0f);
	
		squareBuff = makeFloatBuffer(square);
		triangleBuff = makeFloatBuffer(triangle);
		colorBuff = makeFloatBuffer(colors);
	}
	
	protected void onDraw(Canvas canvas) {
		GL10 gl = (GL10)glContext.getGL();
	
		xrot += 1f;
		yrot += 1f;
		
		glContext.waitNative(canvas, this);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuff);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();

		gl.glTranslatef(0.25f,0.5f,0.0f);
		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		gl.glScalef(0.5f, 0.5f, 0.5f);
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);
		
		gl.glPopMatrix();
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, squareBuff);
		gl.glColor4f(0.25f, 0.25f, 0.75f, 1.0f);
		gl.glTranslatef(0.75f, 0.5f, 0.0f);
		gl.glScalef(0.5f, 0.5f, 0.5f);
		gl.glRotatef(yrot, 0, 1, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
        glContext.waitGL();
	}
}