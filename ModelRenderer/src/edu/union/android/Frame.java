package edu.union.android;

public class Frame {
    String name;
    Mesh mesh;

    public Frame(String name, Mesh mesh) {
	this.name = name;
	this.mesh = mesh;
    }

    public String getName() { return name; }

    public Mesh getMesh() { return mesh; }
}