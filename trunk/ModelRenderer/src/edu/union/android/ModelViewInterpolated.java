package edu.union.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.OpenGLContext;
import android.opengl.GLU;
import android.view.KeyEvent;
import android.view.View;
import edu.union.graphics.Model;
import edu.union.graphics.Mesh;

public class ModelViewInterpolated extends View {
	private Model m;
	private OpenGLContext context;
	ViewAnimator animator;

	int tex;
	int verts;
	int start_frame;
	int end_frame;
	
	float lightAmbient[] = new float[] { 0.2f, 0.3f, 0.6f, 1.0f };
	float lightDiffuse[] = new float[] { 1f, 1f, 1f, 1.0f };

	float matAmbient[] = new float[] { 1f, 1f, 1f, 1.0f };
	float matDiffuse[] = new float[] { 1f, 1f, 1f, 1.0f };

	float[] pos = new float[] {0,20,20,1};

	int current = 0;
	int next = 1;
	float mix = 0;

	private IntBuffer vertices;
	private IntBuffer normals;
	private IntBuffer texCoords;
	private ShortBuffer indices;

	protected static int makeFixed(float val) {
		return (int)(val * 65536F);
	}

	protected static int loadTexture(GL10 gl, Bitmap bmp) {
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.height()*bmp.width()*4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer ib = bb.asIntBuffer();

		for (int y=0;y<bmp.height();y++)
			for (int x=0;x<bmp.width();x++) {
				ib.put(bmp.getPixel(x,y));
			}
		ib.position(0);
		bb.position(0);

		int[] tmp_tex = new int[1];

		gl.glGenTextures(1, tmp_tex, 0);
		int tx = tmp_tex[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tx);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bmp.width(), bmp.height(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		return tx;
	}
	
	protected void nextAnimation() {
		start_frame = end_frame + 1;
		if (start_frame >= m.getFrameCount())
			start_frame = 0;
		getEndFrame();
	}
	
	protected String prefix(String str) {
		int i;
		for (i=0;i<str.length();i++) {
			if (Character.isDigit(str.charAt(i)))
				break;
		}
		return str.substring(0, i);
	}
	
	protected void getEndFrame() {
		end_frame = start_frame;
		String label = prefix(m.getFrame(start_frame).getName());
		do {
			end_frame++;
		} while (end_frame < m.getFrameCount() && prefix(m.getFrame(end_frame).getName()).equals(label));
	}
	
	public ModelViewInterpolated(Model m, Context c) {
		super(c);
		setFocusable(true);
		this.m = m;
		start_frame = 0;
		getEndFrame();
		
		context = new OpenGLContext(OpenGLContext.DEPTH_BUFFER);

		GL10 gl = (GL10)context.getGL();
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(1,1,1,1);

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glEnable(GL10.GL_NORMALIZE);


		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, pos, 0);

		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);

		// Pretty perspective
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glEnable(GL10.GL_CULL_FACE);
		
		ByteBuffer bb;

		Mesh msh = m.getFrame(0).getMesh();

		if (msh.getTextureFile() != null) {
			gl.glEnable(GL10.GL_TEXTURE_2D);
			tex = loadTexture(gl, BitmapFactory.decodeResource(c.getResources(),R.drawable.skin));
		}

		verts = msh.getFaceCount()*3;

		bb = ByteBuffer.allocateDirect(verts*3*4);
		bb.order(ByteOrder.nativeOrder());
		vertices = bb.asIntBuffer();

		bb = ByteBuffer.allocateDirect(verts*3*4);
		bb.order(ByteOrder.nativeOrder());
		normals = bb.asIntBuffer();

		bb = ByteBuffer.allocateDirect(verts*2*4);
		bb.order(ByteOrder.nativeOrder());
		texCoords = bb.asIntBuffer();

		bb = ByteBuffer.allocateDirect(verts*2);
		bb.order(ByteOrder.nativeOrder());
		indices = bb.asShortBuffer();

		short ct = 0;
		for (int i=0;i<msh.getFaceCount();i++) {
			int[] face = msh.getFace(i);
			int[] face_n = msh.getFaceNormals(i);
			int[] face_tx = msh.getFaceTextures(i);
			for (int j=0;j<3;j++) {
				int[] n = msh.getNormalx(face_n[j]);
				int[] v = msh.getVertexx(face[j]);
				for (int k=0;k<3;k++) {
					vertices.put(v[k]);
					normals.put(n[k]);
				}
				int[] tx = msh.getTextureCoordinatex(face_tx[j]);
				texCoords.put(tx[0]);
				texCoords.put(tx[1]);				
				indices.put(ct++);
			}
		}
		vertices.position(0);
		normals.position(0);
		texCoords.position(0);
		indices.position(0);

		animator = new ViewAnimator(this, 60);
		
		setFocusable(true);
	}

	protected void interpolate(float mix) {
		Mesh m1 = m.getFrame(current).getMesh();
		Mesh m2 = m.getFrame(next).getMesh();
		int ct = 0;
		for (int i=0;i<m1.getFaceCount();i++) {
			int[] face = m1.getFace(i);
			int[] face_n = m1.getFaceNormals(i);
			for (int j=0;j<3;j++) {
				float[] n1 = m1.getNormalf(face_n[j]);
				float[] v1 = m1.getVertexf(face[j]);
				float[] n2 = m2.getNormalf(face_n[j]);
				float[] v2 = m2.getVertexf(face[j]);

				for (int k=0;k<3;k++) {
					vertices.put(ct, makeFixed(v2[k]*mix+(1-mix)*v1[k]));
					normals.put(ct, makeFixed(n2[k]*mix+(1-mix)*n1[k]));
					ct++;
				}
			}
		}
	}

	@Override
	protected void onAttachedToWindow() {
		animator.start();
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		animator.stop();
		super.onDetachedFromWindow();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		GL11 gl = (GL11)context.getGL();
		int w = getWidth();
		int h = getHeight();

		context.waitNative(canvas, this);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,w,h);
		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, 1f, 100f);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 0, 0, 5, 0, 0, 0, 0, 1, 0);
		gl.glTranslatef(0,0,-5);
		//gl.glRotatef(30.0f, 1, 0, 0);
		//gl.glRotatef(40.0f, 0, 1, 0);

		interpolate(mix);

		gl.glVertexPointer(3,GL10.GL_FIXED, 0, vertices);
		gl.glNormalPointer(GL10.GL_FIXED,0, normals);
		gl.glTexCoordPointer(2,GL10.GL_FIXED,0,texCoords);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		//gl.glColor4f(1,0,0,1);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
		//gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		gl.glDrawElements(GL10.GL_TRIANGLES, verts, GL10.GL_UNSIGNED_SHORT, indices);

		mix += 0.25f;
		if (mix >= 1) {
			current = next;
			next++;
			if (next > end_frame)
				next = start_frame;
			mix = 0.0f;
		}
		context.waitGL();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			nextAnimation();
			break;
		}	
		return super.onKeyDown(keyCode, event);
	}
	
	
}
