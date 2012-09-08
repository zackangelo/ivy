package com.redstone.opengl.xgl;

import java.nio.FloatBuffer;
import java.util.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class XglRenderer {
	private XglWorld w;
	
	//maps meshes to display list ids
	private Map<String,Integer> meshDispListMap;
	
	public XglRenderer(XglWorld w) {
		this.w = w;
	}
	
	public float getWidth() {
		return (float)(w.getMaxX() - w.getMinX());
	}
	
	public float getHeight() {
		return (float)(w.getMaxY() - w.getMinY());
	}
	
	public float getDepth() {
		return (float)(w.getMaxZ() - w.getMinZ());
	}
	
	public Vector3f getGeometricOrigin() {
		return new Vector3f(
			(float)(Math.abs(w.getMaxX()) - Math.abs(w.getMinX())),
			(float)(Math.abs(w.getMaxY()) - Math.abs(w.getMinY())),
			(float)(Math.abs(w.getMaxZ()) - Math.abs(w.getMinZ()))
		);
	}
	
	/**
	 * Recursively renders an object with all of its subobjects
	 * 
	 * @param obj
	 */
	private void renderObject(XglObject obj){
		// don't attempt a render until we've initialized
		if(meshDispListMap != null) {
			GL11.glPushMatrix();
	    	
			//FIXME should be prewrapped in the XglObject
			FloatBuffer matrixBuf = FloatBuffer.wrap(obj.getTransform().getMatrix());
	    	GL11.glMultMatrix(matrixBuf);
	    	
	    	XglMesh mesh = obj.getMesh();
	    	
	    	if(mesh != null){
	    		GL11.glCallList(meshDispListMap.get(mesh.getId()));
	    	}
	    	
	    	for(XglObject sub:obj.getSubObjects()){
	    		renderObject(sub);
	    	}
	    	       	
	    	GL11.glPopMatrix();
		}
	}
	
	public void render() {
		for(XglObject rend:w.getObjects()){
			renderObject(rend);
		}
	}
	
	private static void renderMeshPolygons(XglMesh mesh) { 
		GL11.glBegin(GL11.GL_TRIANGLES);
		
		XglMaterial lastMaterial = null;
		
		for(XglFace f:mesh.getFaces()) {
			if(f.material != lastMaterial) { 
				if(f.material != null) {
					FloatBuffer diffuse = f.material.diffuse;
					FloatBuffer ambience = f.material.ambient;
					FloatBuffer specular = f.material.specular;
					
					GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, diffuse);
					GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, ambience);
					GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, specular);
				}
				
				lastMaterial = f.material;
			}
			
			GL11.glNormal3f(f.n[0].x,f.n[0].y,f.n[0].z);
			GL11.glVertex3f(f.v[0].x,f.v[0].y,f.v[0].z);
			
			GL11.glNormal3f(f.n[1].x,f.n[1].y,f.n[1].z);
			GL11.glVertex3f(f.v[1].x,f.v[1].y,f.v[1].z);
			
			GL11.glNormal3f(f.n[2].x,f.n[2].y,f.n[2].z);
			GL11.glVertex3f(f.v[2].x,f.v[2].y,f.v[2].z);
		}
		
		GL11.glEnd();
	}
	
	/**
	 * Creates display lists
	 */
	public void init() {
		if(meshDispListMap != null) {
			throw new IllegalStateException("This XGL renderer instance has already been initialized.");
		}
		
		meshDispListMap = new HashMap<String,Integer>();
		
		Collection<XglMesh> meshes = w.getAllMeshes();
		
		for(XglMesh mesh:meshes) {
			int meshDisplayListId ;
			meshDisplayListId = GL11.glGenLists(1); 
        	
			GL11.glNewList(meshDisplayListId ,GL11.GL_COMPILE); 
        		renderMeshPolygons(mesh);
        	GL11.glEndList();
        	
        	meshDispListMap.put(mesh.getId(), meshDisplayListId );
      	}
	}
}
