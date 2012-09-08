package com.redstone.opengl.xgl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class XglObject implements Serializable {
	private XglMesh xglMesh;
	private XglTransform xglTransform;
	private XglObject parentObject;
	
	private Collection<XglObject> subObjects;
	
	public XglObject() { 
		this(null);
	}
	
	public XglObject(XglObject parentObject) { 
		this.parentObject = parentObject;
		this.subObjects = new ArrayList<XglObject>();
	}
	
	public XglMesh getMesh() {
		return xglMesh;
	}

	public void setMesh(XglMesh xglMesh) {
		this.xglMesh = xglMesh;
	}
	
	public XglTransform getTransform() { 
		return xglTransform;
	}
	
	public void setTransform(XglTransform xglTransform) { 
		this.xglTransform = xglTransform;
	}
	
	public XglObject getParentObject() {
		return parentObject;
	}
	
	public void addSubObject(XglObject object) {
		if(object == null) { 
			throw new IllegalArgumentException("Null objects not allowed!");
		}
		
		subObjects.add(object);
	}
	
	public Collection<XglObject> getSubObjects() { 
		return subObjects;
	}
}
