package com.redstone.opengl.xgl;

import java.io.Serializable;
import java.util.*;

public class XglWorld implements Serializable {
	public XglWorld() { 
		meshes = new HashMap<String,XglMesh>();
		objects = new ArrayList<XglObject>();
	}
	
	Map<String,XglMesh> meshes;
	List<XglObject> objects;
	
	double minX,minY,minZ;
	double maxX,maxY,maxZ;
	
	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMinZ() {
		return minZ;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getMaxZ() {
		return maxZ;
	}

	public Collection<XglObject> getObjects() { 
		return objects;
	}
	
	public void addMesh(XglMesh m) {
		if(m.getVertices() == null) { 
			System.out.println("Mesh " + m.getId() + " has no vertices!");
		}
		
		for(XglVertex v:m.getVertices()) {
			minX = Math.min(minX, v.x);
			minY = Math.min(minY, v.y);
			minZ = Math.min(minZ, v.z);
			
			maxX = Math.max(maxX,v.x); 
			maxY = Math.max(maxY,v.y);
			maxZ = Math.max(maxZ,v.z);
		}
		
		meshes.put(m.getId(),m);
	}
	
	public void addObject(XglObject o) {
		if(o == null) { 
			throw new IllegalArgumentException("Null objects not allowed.");
		}
		objects.add(o);
	}
	
	public XglMesh getMesh(String name) { 
		return meshes.get(name);
	}
	
	public Collection<XglMesh> getAllMeshes() {
		return meshes.values();
	}
}
