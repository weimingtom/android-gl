package edu.union.android;

import java.util.Vector;

public class Model {
    Vector<Frame> frames;
    
    public Model() {
	this.frames = new Vector<Frame>();
    }

    public Model(Frame[] frames) {
	this();
	for (int i=0;i<frames.length;i++)
	    addFrame(frames[i]);
    }

    public void addFrame(Frame f) {
	this.frames.add(f);
    }
    
    public Frame getFrame(int ix) {
	return frames.get(ix);
    }
    
    public int getFrameCount() {
	return frames.size();
    }
}