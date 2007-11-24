package edu.union.graphics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class AbstractModelLoader implements ModelLoader {
	@SuppressWarnings("unused")
	protected MeshFactory factory;
	
	public void setFactory(MeshFactory f) {
		factory = f;
	}
	
	public Model load(String file) throws IOException
	{
		return load(new File(file));
	}
	
	public Model load(File f) throws IOException 
	{
		return load(new FileInputStream(f));
	}
	
	public boolean canLoad(String f) {
		return canLoad(new File(f));
	}
}
