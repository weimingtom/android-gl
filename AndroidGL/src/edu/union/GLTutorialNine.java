package edu.union;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;

/**
 * http://www.zeuscmd.com/tutorials/opengles/20-Transparency.php
 * @author bburns
 *
 */
public class GLTutorialNine extends GLTutorialBase {
	float lightAmbient[] = new float[] { 0.3f, 0.3f, 0.3f, 1.0f };
	float lightDiffuse[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	float[] lightPos = new float[] {0,0,3,1};
	
	float matAmbient[] = new float[] { 1f, 1f, 1f, 1.0f };
	float matDiffuse[] = new float[] { 1f, 1f, 1f, 1.0f };
	
	int tex;
	Bitmap bmp;
	
	float box[] = new float[] {
			// FRONT
			-0.5f, -0.5f,  0.5f,
			 0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			// BACK
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			 0.5f, -0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			// LEFT
			-0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			// RIGHT
			 0.5f, -0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			 0.5f, -0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			// TOP
			-0.5f,  0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			 -0.5f,  0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			// BOTTOM
			-0.5f, -0.5f,  0.5f,
			-0.5f, -0.5f, -0.5f,
			 0.5f, -0.5f,  0.5f,
			 0.5f, -0.5f, -0.5f,
		};
	
	float texCoords[] = new float[] {
			// FRONT
			 0.0f, 0.0f,
			 1.0f, 0.0f,
			 0.0f, 1.0f,
			 1.0f, 1.0f,
			// BACK
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// LEFT
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// RIGHT
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// TOP
			 0.0f, 0.0f,
			 1.0f, 0.0f,
			 0.0f, 1.0f,
			 1.0f, 1.0f,
			// BOTTOM
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f
		};

	FloatBuffer cubeBuff;
	FloatBuffer texBuff;
	
	public GLTutorialNine(Context c) {
		super(c, 20);
		
		cubeBuff = makeFloatBuffer(box);
		texBuff = makeFloatBuffer(texCoords);
		
		bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.icon);

		setFocusable(true);
	}
	
	protected void init(GL10 gl) {
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepthf(1.0f);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuff);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		tex = loadTexture(gl, bmp);
	}
	
	float xrot = 0.0f;
	float yrot = 0.0f;
	
	protected void drawFrame(GL10 gl) {		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);
	
		gl.glRotatef(xrot, 1, 0, 0);
		gl.glRotatef(yrot, 0, 1, 0);
	
		gl.glColor4f(1.0f, 1, 1, 1.0f);
		gl.glNormal3f(0,0,1);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glNormal3f(0,0,-1);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
	
		gl.glColor4f(1, 1.0f, 1, 1.0f);
		gl.glNormal3f(-1,0,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
		gl.glNormal3f(1,0,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
		
		gl.glColor4f(1, 1, 1.0f, 1.0f);
		gl.glNormal3f(0,1,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
		gl.glNormal3f(0,-1,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
	
		xrot += 1.0f;
		yrot += 0.5f;
	}
}