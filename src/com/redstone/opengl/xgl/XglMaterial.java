package com.redstone.opengl.xgl;

import java.io.Serializable;
import java.nio.FloatBuffer;

public class XglMaterial implements Serializable {
	public XglMaterial() { }
	
	public FloatBuffer ambient;
	public FloatBuffer specular;
	public FloatBuffer diffuse;
	public FloatBuffer emissive;
	
	public float shininess;
	public float alpha;
}
