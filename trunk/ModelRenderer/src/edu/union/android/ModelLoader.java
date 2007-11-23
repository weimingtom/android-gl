package edu.union.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ModelLoader {
	public void setFactory(MeshFactory f);
	public Model load(String file) throws IOException;
	public Model load(File f) throws IOException;
	public Model load(InputStream is) throws IOException;
	public boolean canLoad(File f);
	public boolean canLoad(String f);
}
