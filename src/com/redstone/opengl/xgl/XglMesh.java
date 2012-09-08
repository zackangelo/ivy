package com.redstone.opengl.xgl;

import java.io.Serializable;
import java.util.*;

public class XglMesh implements Serializable {
	private String id;

	public XglMesh(String id) { 
		faces = new ArrayList<XglFace>();
		xglNormals = new HashMap<Integer,XglNormal>(8192);
		vertices = new HashMap<Integer,XglVertex>(8192);
		xglMaterials = new HashMap<Integer,XglMaterial>(8192);
		
		this.id = id;
	}
	
	private Map<Integer,XglNormal> xglNormals;
	private Map<Integer,XglVertex> vertices;
	private Map<Integer,XglMaterial> xglMaterials;
	private List<XglFace> faces; 
	
	public Collection<XglFace> getFaces() { 
		return faces;
	}
	
	public void addFace(XglFace f) { 
		faces.add(f);
	}
	
	public void addPoint(XglVertex v) { 
		vertices.put(v.id,v);
	}
	
	public XglVertex getPoint(int id) { 
		return vertices.get(id);
	}
	
	public void addNormal(XglNormal n) {
		xglNormals.put(n.id,n);
	}
	
	public XglNormal getNormal(int id) {
		 return xglNormals.get(id);
	}
	
	public void addMaterial(int id,XglMaterial m) { 
		xglMaterials.put(id,m);
	}
	
	public XglMaterial getMaterial(int id) { 
		return xglMaterials.get(id);
	}
	
	public Collection<XglVertex> getVertices() {
		return vertices.values();
	}

	public String getId() {
		return id;
	}
}
