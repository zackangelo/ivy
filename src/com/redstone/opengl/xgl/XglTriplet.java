package com.redstone.opengl.xgl;

import java.io.Serializable;

public class XglTriplet implements Serializable {

	public float x;
	public float y;
	public float z;

	public void setValues(float[] vals) { 
		x = vals[0];
		y = vals[1];
		z = vals[2];
	}
}
