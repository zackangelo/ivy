
import org.apache.log4j.BasicConfigurator;

import org.apache.log4j.Logger;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;

import com.redstone.opengl.OrbitView;
import com.redstone.opengl.xgl.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class XglViewerApp {

	public static class XglModelView extends OrbitView {
//		private XglWorld xglWorld;
		private XglWorld vb10kWorld;
//		private XglWorld loadWorld;

		float[] a1Shift = new float[] { -685.8f, -2000.0f, -2606.0f };
		float[] b1Shift = new float[] { -685.8f, -2000.0f, 0.0f };
		float[] a2Shift = new float[] { 685.8f, -2000.0f, -2606.0f };
		float[] b2Shift = new float[] { 685.8f, -2000.0f, 0.0f };
		
		float[] yLineDisplace = new float[] { 8000, 8000, 10000, 10000 }; //A1, A2, B1, B2
		
		
		public double getModelMaxX() { return vb10kWorld.getMaxX(); }
		public double getModelMaxY() { return vb10kWorld.getMaxY(); }
		public double getModelMaxZ() { return vb10kWorld.getMaxZ(); }
		public double getModelMinX() { return vb10kWorld.getMinX(); }
		public double getModelMinY() { return vb10kWorld.getMinY(); }
		public double getModelMinZ() { return vb10kWorld.getMinZ(); }
		
//		public double getLoadModelMaxX() { return loadWorld.getMaxX(); }
//		public double getLoadModelMaxY() { return loadWorld.getMaxY(); }
//		public double getLoadModelMaxZ() { return loadWorld.getMaxZ(); }
//		public double getLoadModelMinX() { return loadWorld.getMinX(); }
//		public double getLoadModelMinY() { return loadWorld.getMinY(); }
//		public double getLoadModelMinZ() { return loadWorld.getMinZ(); }
		
		private Map<String,Integer> meshMap;
		private Map<String, Integer> loadMeshMap;
				
		private void createList(){
			meshMap = new HashMap<String,Integer>();
			Collection<XglMesh> vb10kMeshes = vb10kWorld.getAllMeshes();
    		for(XglMesh mesh:vb10kMeshes) {
    			int displayListID;
    			displayListID=GL11.glGenLists(1); //Specify we want to build 1 list
            	GL11.glNewList(displayListID,GL11.GL_COMPILE); //Build the list
            		drawMesh(mesh);
            	GL11.glEndList();
            	meshMap.put(mesh.getId(), displayListID);
          	}
    		
//    		loadMeshMap = new HashMap<String, Integer>();
//    		Collection<XglMesh> loadMeshes = loadWorld.getAllMeshes();
//    		for(XglMesh mesh:loadMeshes){
//    			int displayListID;
//    			displayListID=GL11.glGenLists(1); //Specify we want to build 1 list
//            	GL11.glNewList(displayListID,GL11.GL_COMPILE); //Build the list
//            		drawMesh(mesh);
//            	GL11.glEndList();
//            	loadMeshMap.put(mesh.getId(), displayListID);
//    			
//    		}
    		
		}
		
		
		private float[][] colorGradient(float[] startColor, float[] endColor, int steps) { 
			float[][] gradient = new float[steps][3];		
			
			float[] mod = new float[3];
			mod[0] = 0.0f;
			mod[1] = 0.0f;
			mod[2] = 0.0f;
			for(int i = 0; i < steps; i++){
				
				for(int j = 0; j < 3; j++){
					gradient[i][j] = startColor[j] - mod[j];
					if (j == 0) mod[j] += 0.05f;
					else if(j == 1) mod[j] += 0.05f;
					else if(j == 2) mod[j] += 0.025f;
				}

			}
			return gradient;
		}
		
		private final static float[] skyStartColor = new float[] { 0.23f, 0.31f, 0.39f };
		private final static float[] skyEndColor = new float[] { 0.12f, 0.20f, 0.35f };
		
		private void skyDome(){
			GL11.glPushMatrix();
	        int radius = 1060000;
	        double x, y, z;
	        
			FloatBuffer diffuse = FloatBuffer.wrap(new float[]{0.0f, 1.0f, 0.0f, 0.25f});
			FloatBuffer ambience = FloatBuffer.wrap(new float[]{0.0f, 1.0f, 0.0f, 0.25f});
			FloatBuffer specular = FloatBuffer.wrap(new float[]{0.0f, 1.0f, 0.0f, 0.25f});
			
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, diffuse);
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, ambience);
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, specular);
	        
			GL11.glDisable(GL11.GL_LIGHTING);
			float alpha = 1f;
			float[][] g = colorGradient(skyStartColor, skyEndColor, 10);
			int step = 0;
			for (double phi = 0.0; phi <= 80.0; phi += 10.0) {
	        	GL11.glBegin(GL11.GL_QUAD_STRIP);
                for (double theta = -180.0; theta <= 180.0; theta += 10.0) {
                	
                     x = radius * Math.sin(Math.PI/180 * theta) * Math.cos(Math.PI/180 * phi);
                     y = radius * Math.sin(Math.PI/180 * phi);
                     z = radius * Math.cos(Math.PI/180 * theta) * Math.cos(Math.PI/180 * phi);

                     GL11.glColor4f( g[step][0], g[step][1], g[step][2], alpha );
                     GL11.glVertex3d (x,y,z);	
                     
                     x = radius * Math.sin(Math.PI/180 * theta) * Math.cos(Math.PI/180 * (phi + 10.0));
                     y = radius * Math.sin(Math.PI/180 * (phi + 10.0));
                     z = radius * Math.cos(Math.PI/180 * theta) * Math.cos(Math.PI/180 * (phi + 10.0));

                     GL11.glColor4f( g[step + 1][0], g[step + 1][1], g[step + 1][2], alpha );
                     GL11.glVertex3d (x,y,z);
                     
                }
                step++;
            GL11.glEnd();
	      }
			
		  GL11.glEnable(GL11.GL_LIGHTING);
	      GL11.glPopMatrix();
		}

		
				
				
		private void drawWaterLevel(){
			
			GL11.glTranslatef(0, -7500, 0);
//			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glPushMatrix();
			
			FloatBuffer diffuse = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 1.0f, 0.25f});
			FloatBuffer ambience = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 1.0f, 0.25f});
			FloatBuffer specular = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 1.0f, 0.25f});
			
			GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_DIFFUSE, diffuse);
			GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT, ambience);
			GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_SPECULAR, specular);
			GL11.glColor4f(0.0f, 0.0f, 1.0f, 0.25f);

			double x, y, z;
			int radius = (int) (1060000*(5.8));
			double phi = 80;
			
			for (int i = 0; i < 2; i++) { //Draws the cylindar wrapped around the edge
	        	GL11.glBegin(GL11.GL_QUAD_STRIP);
                for (double theta = -180.0; theta <= 180.0; theta += 10.0) {
                	
                     x = radius * Math.sin(Math.PI/180 * theta) * Math.cos(Math.PI/180 * -phi);
                     y = (radius/10) * Math.sin(Math.PI/180 * -phi);
                     z = radius * Math.cos(Math.PI/180 * theta) * Math.cos(Math.PI/180 * -phi);

                     GL11.glVertex3d (x,y,z);	
                     
                     x = radius * Math.sin(Math.PI/180 * theta) * Math.cos(Math.PI/180 * phi);
                     y = Math.sin(Math.PI/180 * (-phi + 10.0));
                     z = radius * Math.cos(Math.PI/180 * theta) * Math.cos(Math.PI/180 * phi);

                     GL11.glVertex3d (x,y,z);
                     
                     
                }
                
            GL11.glEnd();
	      }
			
			int shift = 0;
			for (int i = 0; i < 2; i++){ //draws top and bottom surfaces
				GL11.glBegin(GL11.GL_QUAD_STRIP);
				for (double theta = -180.0; theta <= 180.0; theta += 10.0){
					
					if(i == 0){
	                     x = radius * Math.sin(Math.PI/180 * theta) * Math.cos(Math.PI/180 * (phi + 10.0));
	                     y = Math.sin(Math.PI/180 * (phi + 10.0) - shift);
	                     z = radius * Math.cos(Math.PI/180 * theta) * Math.cos(Math.PI/180 * (phi + 10.0));

	                     GL11.glVertex3d (x,y,z);					
						 x = radius * Math.sin(Math.PI/180 * theta) * Math.cos(Math.PI/180 * phi);
	                     y = Math.sin((Math.PI/180 * phi) - shift);
	                     z = radius * Math.cos(Math.PI/180 * theta) * Math.cos(Math.PI/180 * phi);

	                     GL11.glVertex3d (x,y,z);	
					}
					else{
						
						 x = radius * Math.sin(Math.PI/180 * theta) * Math.cos(Math.PI/180 * phi);
	                     y = Math.sin((Math.PI/180 * phi) - 150000);
	                     z = radius * Math.cos(Math.PI/180 * theta) * Math.cos(Math.PI/180 * phi);

	                     GL11.glVertex3d (x,y,z);	
						
	                     x = radius * Math.sin(Math.PI/180 * theta) * Math.cos(Math.PI/180 * (phi + 10.0));
	                     y = Math.sin(Math.PI/180 * (phi + 10.0) - 150000);
	                     z = radius * Math.cos(Math.PI/180 * theta) * Math.cos(Math.PI/180 * (phi + 10.0));

	                     GL11.glVertex3d (x,y,z);
						
					}

	                     
				}
				GL11.glEnd();
				
			}
			
		GL11.glPopMatrix();
//		GL11.glEnable(GL11.GL_CULL_FACE);
			
			
			
//			GL11.glBegin(GL11.GL_QUAD_STRIP);
			
//				//Top surface
//				GL11.glVertex3f( 30000.0f, -7800.0f,-30000.0f);
//				GL11.glVertex3f(-30000.0f, -7800.0f,-30000.0f);
//				GL11.glVertex3f(-30000.0f, -7800.0f, 30000.0f);
//				GL11.glVertex3f( 30000.0f, -7800.0f, 30000.0f);
//				
//				//Bottom surface
//				GL11.glVertex3f(-30000.0f, -10000.0f,-30000.0f);
//				GL11.glVertex3f( 30000.0f, -10000.0f,-30000.0f);
//				GL11.glVertex3f( 30000.0f, -10000.0f, 30000.0f);
//				GL11.glVertex3f(-30000.0f, -10000.0f, 30000.0f);
//				
//				//Back
//				GL11.glVertex3f(-30000, -7800, -30000);
//				GL11.glVertex3f(30000, -7800, -30000);
//				GL11.glVertex3f(30000, -10000, -30000);
//				GL11.glVertex3f(-30000, -10000, -30000);
//				
//				//Right
//				GL11.glVertex3f(30000, -10000, -30000);
//				GL11.glVertex3f(30000, -7800, -30000);
//				GL11.glVertex3f(30000,	-7800,	30000);
//				GL11.glVertex3f(30000, -10000, 30000);
//				
//				//Left
//				GL11.glVertex3f(-30000, -7800, -30000);
//				GL11.glVertex3f(-30000, -10000, -30000);
//				GL11.glVertex3f(-30000, -10000, 30000);
//				GL11.glVertex3f(-30000, -7800, 30000);
//				
//				//Front
//				GL11.glVertex3f(-30000, -7800, 30000);
//				GL11.glVertex3f(-30000, -10000, 30000);
//				GL11.glVertex3f(30000, -10000, 30000);
//				GL11.glVertex3f(30000, -7800, 30000);
			
//			
//			GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
//			GL11.glBegin(GL11.GL_LINES);
//			//Around the top face
//				GL11.glVertex3f(30000, -7800, 30000);
//				GL11.glVertex3f(30000, -7800, -30000);
//				
//				GL11.glVertex3f(-30000, -7800, 30000);
//				GL11.glVertex3f(30000, -7800, 30000);
//				
//				GL11.glVertex3f(-30000, -7800, -30000);
//				GL11.glVertex3f(-30000, -7800, 30000);
//				
//				GL11.glVertex3f(-30000, -7800, -30000);
//				GL11.glVertex3f(30000, -7800, -30000);
//				
//			//Around front edge
//				GL11.glVertex3f(30000, -7800, 30000);
//				GL11.glVertex3f(30000, -10000, 30000);
//				
//				GL11.glVertex3f(-30000, -7800, 30000);
//				GL11.glVertex3f(-30000, -10000, 30000);
//				
//				GL11.glVertex3f(-30000, -10000, 30000);
//				GL11.glVertex3f(30000, -10000, 30000);
//				
//			//Around right edge
//				GL11.glVertex3f(30000, -10000, 30000);
//				GL11.glVertex3f(30000, -10000, -30000);
//				
//				GL11.glVertex3f(30000, -7800, -30000);
//				GL11.glVertex3f(30000, -10000, -30000);
//				
//			//Around the left edge
//				GL11.glVertex3f(-30000, -10000, 30000);
//				GL11.glVertex3f(-30000, -10000, -30000);
//				
//				GL11.glVertex3f(-30000, -7800, -30000);
//				GL11.glVertex3f(-30000, -10000, -30000);
//				
//			//Around the back edge
//				GL11.glVertex3f(-30000, -10000, -30000);
//				GL11.glVertex3f(30000, -10000, -30000);
//				
//				GL11.glVertex3f(30000, -10000, -30000);
//				GL11.glVertex3f(30000, -7800, -30000);
//				
//			GL11.glEnd();
//			GL11.glPopMatrix();		
	
		}
		
		private void drawLineDisplace(float bOneDisp, float bTwoDisp, float aOneDisp, float aTwoDisp){
			
			//B1 Line
			GL11.glPushMatrix();
			GL11.glTranslatef(b1Shift[0], b1Shift[1], b1Shift[2]);
			GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3f(0.0f, 0.0f, 0.0f);
				GL11.glVertex3f(0.0f, -bOneDisp, 0.0f);
			GL11.glEnd();
			GL11.glPopMatrix();
			
			//B2 Line
			GL11.glPushMatrix();
			GL11.glTranslatef(b2Shift[0], b2Shift[1], b2Shift[2]);
			GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3f(0.0f, 0.0f, 0.0f);
				GL11.glVertex3f(0.0f, -bTwoDisp, 0.0f);
			GL11.glEnd();
			GL11.glPopMatrix();
			
			//A1 Line
			GL11.glPushMatrix();
			GL11.glTranslatef(a1Shift[0], a1Shift[1], a1Shift[2]);
			GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3f(0.0f, 0.0f, 0.0f);
				GL11.glVertex3f(0.0f, -aOneDisp, 0.0f);
			GL11.glEnd();
			GL11.glPopMatrix();
			
			//A2 Line
			GL11.glPushMatrix();
			GL11.glTranslatef(a2Shift[0], a2Shift[1], a2Shift[2]);
			GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3f(0.0f, 0.0f, 0.0f);
				GL11.glVertex3f(0.0f, -aTwoDisp, 0.0f);
			GL11.glEnd();
			GL11.glPopMatrix();
			
			//Draw Lift Plane
			GL11.glPushMatrix();
			GL11.glBegin(GL11.GL_QUADS);
			
				//Top of the plane
				GL11.glVertex3f(b1Shift[0], b1Shift[1] - bOneDisp, b1Shift[2]);
				GL11.glVertex3f(b2Shift[0], b2Shift[1] - bTwoDisp, b2Shift[2]);	
				GL11.glVertex3f(a2Shift[0], a2Shift[1] - aTwoDisp, a2Shift[2]);
				GL11.glVertex3f(a1Shift[0], a1Shift[1] - aOneDisp, a1Shift[2]);
				
				
					
								
				//Bottom of the plane
				GL11.glVertex3f(b2Shift[0], b2Shift[1] - bTwoDisp, b2Shift[2]);	
				GL11.glVertex3f(b1Shift[0], b1Shift[1] - bOneDisp, b1Shift[2]);
				GL11.glVertex3f(a1Shift[0], a1Shift[1] - aOneDisp, a1Shift[2]);
				GL11.glVertex3f(a2Shift[0], a2Shift[1] - aTwoDisp, a2Shift[2]);
				
				
				
			GL11.glEnd();
			GL11.glPopMatrix();
			
			
		}
		
		

		private void drawLiftingBlocks(){
			
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex3f(100.0f, 100.0f, 100.0f);
				GL11.glVertex3f(100.0f, 100.0f, -100.0f);
				GL11.glVertex3f(-100.0f, 100.0f, -100.0f);
				GL11.glVertex3f(-100.0f, 100.0f, 100.0f);
				
				GL11.glColor3f(1.0f,0.5f,0.0f);	
				GL11.glVertex3f( 100.0f,-100.0f, 100.0f);
				GL11.glVertex3f(-100.0f,-100.0f, 100.0f);
				GL11.glVertex3f(-100.0f,-100.0f,-100.0f);
				GL11.glVertex3f( 100.0f,-100.0f,-100.0f);	

				GL11.glColor3f(1.0f,0.0f,0.0f);
				GL11.glVertex3f( 100.0f, 100.0f, 100.0f);
				GL11.glVertex3f(-100.0f, 100.0f, 100.0f);
				GL11.glVertex3f(-100.0f,-100.0f, 100.0f);
				GL11.glVertex3f( 100.0f,-100.0f, 100.0f);	
				
				GL11.glColor3f(1.0f,1.0f,0.0f);
				GL11.glVertex3f( 100.0f,-100.0f,-100.0f);
				GL11.glVertex3f(-100.0f,-100.0f,-100.0f);
				GL11.glVertex3f(-100.0f, 100.0f,-100.0f);
				GL11.glVertex3f( 100.0f, 100.0f,-100.0f);	
				
				GL11.glColor3f(0.0f,0.0f,1.0f);
				GL11.glVertex3f(-100.0f, 100.0f, 100.0f);
				GL11.glVertex3f(-100.0f, 100.0f,-100.0f);
				GL11.glVertex3f(-100.0f,-100.0f,-100.0f);
				GL11.glVertex3f(-100.0f,-100.0f, 100.0f);	
				
				GL11.glColor3f(1.0f,0.0f,1.0f);
				GL11.glVertex3f( 100.0f, 100.0f,-100.0f);
				GL11.glVertex3f( 100.0f, 100.0f, 100.0f);
				GL11.glVertex3f( 100.0f,-100.0f, 100.0f);
				GL11.glVertex3f( 100.0f,-100.0f,-100.0f);
			GL11.glEnd();
		}
		
		private void renderObject(XglObject x, Map<String, Integer> world){
			GL11.glPushMatrix();
			
			
			//FIXME reallocating buffer each render
			float[] matrix = x.getTransform().getMatrix();
        	FloatBuffer matrixBuf = BufferUtils.createFloatBuffer(matrix.length); //FloatBuffer.wrap(x.getTransform().getMatrix());
        	matrixBuf.put(matrix);
        	matrixBuf.rewind();
        	
        	GL11.glMultMatrix(matrixBuf);
        	
        	XglMesh mesh = x.getMesh();
        	if(mesh != null){
        		GL11.glCallList(world.get(mesh.getId()));
        	}
        	
        	for(XglObject sub:x.getSubObjects()){
        		renderObject(sub, world);
        	}
        	GL11.glPopMatrix();
        	
        	
		}
		
		private void drawMesh(XglMesh mesh) {
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
		
	    public static FloatBuffer allocFloats(float[] floatarray) {
	    	FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	    	fb.put(floatarray).flip();
	    	return fb;
	    }

		
		public void render(){
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
			GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			GL11.glPushMatrix();
				GL11.glLoadIdentity();
				
	    		float LightAmbient[]= { 0.5f, 0.5f, 0.5f, 0.0f }; 
		        float LightDiffuse[]= { 1.0f, 1.0f, 1.0f, 1.0f };
		        float LightPosition[]= { 4000.0f, 400.0f, 2000.0f, 0.0f };	
	
		        FloatBuffer ltDiffuse = allocFloats(LightDiffuse);
		        FloatBuffer ltAmbient = allocFloats(LightAmbient);
		        FloatBuffer ltPosition = allocFloats(LightPosition);
		        
		        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, ltDiffuse); 
		        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, ltAmbient);
		        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, ltPosition);
		        GL11.glLightf(GL11.GL_LIGHT1, GL11.GL_SPOT_CUTOFF, 180.0f);
		        
		        GL11.glEnable(GL11.GL_LIGHTING);
		        GL11.glEnable(GL11.GL_LIGHT1);
		        
//				skyDome();
	        GL11.glPopMatrix();
	        
	        
			Vector3f A = new Vector3f(a1Shift[0], a1Shift[1] - yLineDisplace[0], a1Shift[2]); //A1
			Vector3f B = new Vector3f(b1Shift[0], b1Shift[1] - yLineDisplace[2], b1Shift[2]); //B1
			Vector3f C = new Vector3f(b2Shift[0], b2Shift[1] - yLineDisplace[3], b2Shift[2]); //B2
			ThreeDimensionalPlane x = new ThreeDimensionalPlane(A, B, C);
	        double angleYZ = ThreeDimensionalPlane.yzRot(x);
	        double angleXZ = 90 - ThreeDimensionalPlane.xzRot(x);
	        
			for(XglObject rend:vb10kWorld.getObjects()){
				renderObject(rend, meshMap);
			}
		
			
			System.out.println("x-z Rotation: " + angleXZ + "\ny-z Rotation: " + angleYZ + "\n");
			GL11.glPushMatrix();
			GL11.glTranslatef(-600, (float) A.y - 1900, -3000); //shift down to correct level
			
			GL11.glTranslatef(-50, 1500, 3000);
			
			if(yLineDisplace[0] < yLineDisplace[2]){
				GL11.glRotatef((float) -angleYZ, 1, 0, 0); //Rotate to correct orientation 
			}											   //Conditions to handle negative rotations	
			else{
				GL11.glRotatef((float) angleYZ, 1, 0, 0);
			}
			
			if(yLineDisplace[0] < yLineDisplace[3]){
				
				GL11.glRotatef((float) -angleXZ, 0, 0, 1);
			}
			else{
				GL11.glRotatef((float) -angleXZ, 0, 0, 1); 
			}
			
			
			GL11.glTranslatef(50, -1500, -3000);
			
			
			GL11.glRotatef(90, 0, 1, 0); //Rotate to correct initial orientation
			GL11.glRotatef(-90, 1, 0, 0);			
			//Since the origin of the model is not in the center, we try to shift it to the correct position
			
//			for(XglObject rend:loadWorld.getObjects()){
//				renderObject(rend, loadMeshMap);
//			}
			GL11.glPopMatrix();
			
			
			//B1
			GL11.glPushMatrix();
			GL11.glTranslatef( b1Shift[0], b1Shift[1], b1Shift[2]);
			drawLiftingBlocks();
			GL11.glPopMatrix();
			
			//B2
			GL11.glPushMatrix();
			GL11.glTranslatef(b2Shift[0], b2Shift[1], b2Shift[2]);
			drawLiftingBlocks();
			GL11.glPopMatrix();
			
			//A1
			GL11.glPushMatrix();
			GL11.glTranslatef(a1Shift[0], a1Shift[1], a1Shift[2]);
			drawLiftingBlocks();
			GL11.glPopMatrix();
			
			//A2
			GL11.glPushMatrix();
			GL11.glTranslatef(a2Shift[0], a2Shift[1], a2Shift[2]);
			drawLiftingBlocks();
			GL11.glPopMatrix();
			
			GL11.glPopMatrix();
			
			drawLineDisplace(yLineDisplace[0], yLineDisplace[1], yLineDisplace[2], yLineDisplace[3]);
//			drawWaterLevel();
			
//			skyDome();
			
		}
		
		public XglModelView(GLCanvas glCanvas,XglWorld vb10kWorld) {
			super(glCanvas);
			this.vb10kWorld = vb10kWorld;
//			this.loadWorld = loadWorld;
			
			System.out.println("VB10K MAX: " + getModelMaxX() + "," + getModelMaxY() + "," + getModelMaxZ());
			System.out.println("VB10K MIN: " + getModelMinX() + "," + getModelMinY() + "," + getModelMinZ());
			
//			System.out.println("Load MAX: " + getLoadModelMaxX() + "," + getLoadModelMaxY() + "," + getLoadModelMaxZ());
//			System.out.println("Load MIN: " + getLoadModelMinX() + "," + getLoadModelMinY() + "," + getLoadModelMinZ());
			
		}
		
		public void initModel() { 
//			GL11.glEnable(GL11.GL_NORMALIZE);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//			GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT,GL11.GL_NICEST);
//			GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
			createList();
		}
	}
	
	public static class XglViewerWindow extends SimpleOpenGLWindow {
		private XglModelView modelView;
		private XglWorld vb10kWorld;
		private XglWorld loadWorld;
		
		public XglViewerWindow(XglWorld vb10kWorld, XglWorld loadWorld) {
			this.vb10kWorld = vb10kWorld;
			this.loadWorld = loadWorld;
		}
		
		@Override
		protected OrbitView getModelView(GLCanvas glCanvas) {
			if(modelView == null) {
				modelView = new XglModelView(glCanvas, vb10kWorld);
//				modelView = new XglModelView(glCanvas, loadWorld);
			}
			
			return modelView;
		}
		
		public void initWindow() {
			modelView.initModel();
		}
		
	}
	
	private final static Logger log = Logger.getLogger(XglViewerApp.class);
	
	
	public static XglWorld loadSerializedWorld(String fileName) throws IOException, ClassNotFoundException {
		log.debug("Reading serialized model from " + fileName + "...");
		FileInputStream fis = new FileInputStream(fileName);
		BufferedInputStream bis = new BufferedInputStream(fis,512*1024);
		GZIPInputStream gzis = new GZIPInputStream(bis);
		ObjectInputStream ois = new ObjectInputStream(gzis);
		
		XglWorld world = (XglWorld) ois.readObject();
		
		return world;
	}
	
	public static void main(String[] args) throws XglException, IOException, ClassNotFoundException {
		BasicConfigurator.configure();
		
		long startMillis = System.currentTimeMillis();

		log.debug("Parsing model...");
		XglParser parser 	= new XglParser();
//		XglWorld vb10kWorld 	= parser.parse(new FileInputStream("etc\\models\\vb10k.xgl"));
//		XglWorld loadWorld = parser.parse(new FileInputStream("etc\\models\\E.I.-Blk296-B.xgl"));
//		XglWorld xglWorld = loadSerializedWorld("etc\\models\\vb10k.jsz");
		
		XglWorld shackleModel = parser.parse(new FileInputStream("etc/models/shackle.xml"));
		
		log.debug("Done! (" + (System.currentTimeMillis() - startMillis) + "ms)");
		
		ApplicationWindow mainWindow = new XglViewerWindow(shackleModel, null);
		mainWindow.setBlockOnOpen(true);
		mainWindow.open();
		
		Display.getCurrent().dispose();	
	}

}
