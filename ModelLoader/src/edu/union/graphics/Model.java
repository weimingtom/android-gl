package edu.union.graphics;

import java.util.Vector;

/**
 * A Model is a collection of one or more animation frames.
 * @author bburns
 */
public class Model {
	Vector<Frame> frames;

	// Constructor
	public Model() {
		this.frames = new Vector<Frame>();
	}
	
	/**
	 * Constructor
	 * @param m The single mesh for a static model
	 */
	public Model(Mesh m) {
		this(new Mesh[] {m});
	}
	
	/**
	 * Constructor
	 * @param m An array of Meshes to turn into Frames
	 */
	public Model(Mesh[] m) {
		this();
		for (int i=0;i<m.length;i++)
			addFrame(new Frame("frame"+i, m[i]));
	}

	/**
	 * Constructor
	 * @param frames The frames for this Model.
	 */
	public Model(Frame[] frames) {
		this();
		for (int i=0;i<frames.length;i++)
			addFrame(frames[i]);
	}

	/**
	 * Add a Frame to this model.
	 * @param f The Frame to add.
	 */
	public void addFrame(Frame f) {
		this.frames.add(f);
	}

	/**
	 * Get a Frame from this model.
	 * @param ix The index of the Frame to get.
	 * @return The specified Frame
	 */
	public Frame getFrame(int ix) {
		return frames.get(ix);
	}

	/**
	 * Get the number of frames in this model
	 * @return The number of frames.
	 */
	public int getFrameCount() {
		return frames.size();
	}
}