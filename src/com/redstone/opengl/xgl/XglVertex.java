package com.redstone.opengl.xgl;

import java.io.Serializable;

public class XglVertex extends XglTriplet implements Serializable {
	protected int id;

	public XglVertex(int id) {
		this.id = id;
	}
	
//	public int getId() { return id; }
}
