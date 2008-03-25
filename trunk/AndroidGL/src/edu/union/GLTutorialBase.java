package edu.union;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.OpenGLContext;
import android.opengl.GLU;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public abstract class GLTutorialBase extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	protected OpenGLContext glContext;
	protected ViewAnimator animator;
	protected SurfaceHolder sHolder;
	protected Thread t;
	protected boolean running;
	int width;
	int height;
	boolean resize;
	int fps;
	
	/**
	 * Make a direct NIO FloatBuffer from an array of floats
	 * @param arr The array
	 * @return The newly created FloatBuffer
	 */
	protected static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	/**
	 * Make a direct NIO IntBuffer from an array of ints
	 * @param arr The array
	 * @return The newly created IntBuffer
	 */
	protected static IntBuffer makeFloatBuffer(int[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer ib = bb.asIntBuffer();
		ib.put(arr);
		ib.position(0);
		return ib;
	}

	/**
	 * Create a texture and send it to the graphics system
	 * @param gl The GL object
	 * @param bmp The bitmap of the texture
	 * @param reverseRGB Should the RGB values be reversed?  (necessary workaround for loading .pngs...)
	 * @return The newly created identifier for the texture.
	 */
	protected static int loadTexture(GL10 gl, Bitmap bmp) {
		return loadTexture(gl, bmp, false);
	}
	
	/**
	 * Create a texture and send it to the graphics system
	 * @param gl The GL object
	 * @param bmp The bitmap of the texture
	 * @param reverseRGB Should the RGB values be reversed?  (necessary workaround for loading .pngs...)
	 * @return The newly created identifier for the texture.
	 */
	protected static int loadTexture(GL10 gl, Bitmap bmp, boolean reverseRGB) {
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.height()*bmp.width()*4);
		bb.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = bb.asIntBuffer();

		for (int y=bmp.height()-1;y>-1;y--)
			for (int x=0;x<bmp.width();x++) {
				int pix = bmp.getPixel(x,bmp.getHeight()-y-1);
				// Convert ARGB -> RGBA
				@SuppressWarnings("unused")
				byte alpha = (byte)((pix >> 24)&0xFF);
				byte red = (byte)((pix >> 16)&0xFF);
				byte green = (byte)((pix >> 8)&0xFF);
				byte blue = (byte)((pix)&0xFF);
				
				// It seems like alpha is currently broken in Android...
				ib.put(red << 24 | green << 16 | blue << 8 | 0xFF);//255-alpha);
			}
		ib.position(0);
		bb.position(0);

		int[] tmp_tex = new int[1];

		gl.glGenTextures(1, tmp_tex, 0);
		int tex = tmp_tex[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bmp.width(), bmp.height(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		return tex;
	}

	/**
	 * Constructor
	 * @param c The View's context.
	 */
	public GLTutorialBase(Context c) {
		this(c, -1);
	}

	/**
	 * Constructor for animated views
	 * @param c The View's context
	 * @param fps The frames per second for the animation.
	 */
	public GLTutorialBase(Context c, int fps) {
		super(c);
		glContext = new OpenGLContext(OpenGLContext.DEPTH_BUFFER);
		sHolder = getHolder();
		sHolder.addCallback(this);
		this.fps = fps;
	}

	@Override
	protected void onAttachedToWindow() {
		if (animator != null) {
			// If we're animated, start the animation
			animator.start();
		}
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		if (animator != null) {
			// If we're animated, stop the animation
			animator.stop();
		}
		super.onDetachedFromWindow();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		synchronized (this) {
			this.width = width;
			this.height = height;
			this.resize = true;
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		t = new Thread(this);
		t.start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		running = false;
		try {
			t.join();
		}
		catch (InterruptedException ex) {}
		t = null;
	}

	public void run() {
		OpenGLContext glc = new OpenGLContext( OpenGLContext.DEPTH_BUFFER );

		SurfaceHolder holder = sHolder;
		glc.makeCurrent(holder);

		GL10 gl = (GL10)(glc.getGL());

		init(gl);
		
		int delta = -1;
		if (fps > 0) {
			delta = 1000/fps;
		}
		long time = System.currentTimeMillis();
		
		running = true;
		while (running) {
			int w, h;
			synchronized(this) {
				w = width;
				h = height;
			}
			if (System.currentTimeMillis()-time < delta) {
				try {
					Thread.sleep(System.currentTimeMillis()-time);
				}
				catch (InterruptedException ex) {}
			}
			drawFrame(gl, w, h);
			glc.post();
			time = System.currentTimeMillis();
		}

		glc.makeCurrent((SurfaceHolder)null);
	}	

	private void drawFrame(GL10 gl, int w, int h) {
		if (resize) {
			resize(gl, w, h);
			resize = false;
		}
		drawFrame(gl);
	}
	
	protected void resize(GL10 gl, int w, int h) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,w,h);
		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, 1f, 100f);
	}
	
	protected void init(GL10 gl) {}
	
	protected abstract void drawFrame(GL10 gl);
}