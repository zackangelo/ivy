package com.redstone.opengl.xgl;

import java.io.Serializable;

public class XglFace implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public XglFace() { 
		v = new XglVertex[3];
		n = new XglNormal[3];
	}
	public XglVertex v[];
	public XglNormal n[];
	public XglMaterial material;
}
