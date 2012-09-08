package com.redstone.opengl.xgl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.FloatBuffer;

import org.apache.log4j.BasicConfigurator;
import org.lwjgl.opengl.GL11;

import com.redstone.opengl.xgl.XglException;
import com.redstone.opengl.xgl.XglFace;
import com.redstone.opengl.xgl.XglMaterial;
import com.redstone.opengl.xgl.XglMesh;
import com.redstone.opengl.xgl.XglObject;
import com.redstone.opengl.xgl.XglParser;
import com.redstone.opengl.xgl.XglWorld;

public class XglCodeGen {

	private static String floatArrToStr(float[] in) { 
		StringBuilder b = new StringBuilder();
		
		b.append("new float[] {"); 
		
		for(int i=0;i<in.length;i++) { 
			b.append(in[i]);
			b.append("f");
			if(i<in.length-1) b.append(",");
		}
		
		b.append("}");
		
		return b.toString();
	}
	
	private static String floatArrToStr(FloatBuffer in) { 
		StringBuilder b = new StringBuilder();
		
		in.flip();
		in.limit(in.capacity());
		
		if(in.remaining() < 1) {
			throw new IllegalArgumentException("Buffer is empty! ("+in.toString()+")");
		}
		
		b.append("new float[] {"); 
		
		while(in.remaining() > 0) { 
			b.append(in.get());
			b.append("f");
			if(in.remaining() > 0) b.append(",");
		}
		
		b.append("}");
		
		return b.toString();
	}
	
	private final static int FACES_PER_METHOD = 720;
	
	private static void meshRenderMethods(XglWorld w,PrintWriter pw) {
		for(XglMesh mesh:w.getAllMeshes()) {
			XglMaterial lastMaterial = null;
			
			int faceIndex = 0;
			int methodIndex = 0;
			
			for(XglFace f:mesh.getFaces()) { 
				if(faceIndex % FACES_PER_METHOD == 0) {
					if(faceIndex != 0) {
						pw.println("GL11.glEnd();");
						pw.println("}");
						pw.println("}");
					}
					
					pw.println("private final static class mesh_"+mesh.getId()+"_"+methodIndex + " {");
					pw.println("private final static void glFaces() {");
					pw.println("GL11.glBegin(GL11.GL_TRIANGLES);");
					
					methodIndex++;
				}
				
				if(f.material != lastMaterial) {
					if(f.material != null) {
						pw.println("GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, (FloatBuffer) FloatBuffer.wrap("+floatArrToStr(f.material.diffuse)+"));");
						pw.println("GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, (FloatBuffer) FloatBuffer.wrap("+floatArrToStr(f.material.ambient)+"));");
						pw.println("GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, (FloatBuffer) FloatBuffer.wrap("+floatArrToStr(f.material.specular)+"));");
					}
					lastMaterial = f.material;
				}
				
				pw.println("GL11.glNormal3f("+f.n[0].x+"f,"+f.n[0].y+"f,"+f.n[0].z+"f);");
				pw.println("GL11.glVertex3f("+f.v[0].x+"f,"+f.v[0].y+"f,"+f.v[0].z+"f);");
				
				pw.println("GL11.glNormal3f("+f.n[1].x+"f,"+f.n[1].y+"f,"+f.n[1].z+"f);");
				pw.println("GL11.glVertex3f("+f.v[1].x+"f,"+f.v[1].y+"f,"+f.v[1].z+"f);");
				
				pw.println("GL11.glNormal3f("+f.n[2].x+"f,"+f.n[2].y+"f,"+f.n[2].z+"f);");
				pw.println("GL11.glVertex3f("+f.v[2].x+"f,"+f.v[2].y+"f,"+f.v[2].z+"f);");
				
				faceIndex++;
			}
			
			if(faceIndex % FACES_PER_METHOD != 0) {
				pw.println("GL11.glEnd();");
				pw.println("}");
				pw.println("}");
			}
			
			pw.println("private void mesh_"+mesh.getId()+"() {");
			for(int i=0;i<methodIndex;i++) {
				pw.println("mesh_"+mesh.getId()+"_"+i+".glFaces();");
				
			}
			pw.println("}");
			
			System.out.println("Split mesh " + mesh.getId() + " into " + methodIndex + 
					" inner classes with " + FACES_PER_METHOD + " faces each (" + 
					mesh.getFaces().size() + " total).");
		}
	}
	
	/**

	 * @param o
	 * @param pw
	 */
	private static void glCmdsForObject(XglObject o,PrintWriter pw) { 
		pw.println(
				"//BEGIN OBJECT \n" + 
				"GL11.glPushMatrix(); \n" +
				"GL11.glMultMatrix(FloatBuffer.wrap("+floatArrToStr(o.getTransform().getMatrix())+"));");
				
		XglMesh mesh = o.getMesh();
		
		if(o.getMesh() != null) {
			pw.println("mesh_"+mesh.getId()+"();");
		}
		for(XglObject sub:o.getSubObjects()) {
			glCmdsForObject(sub,pw);
		}
		
		pw.println("GL11.glPopMatrix();");
	}
	
	private static void glCmdsForWorld(XglWorld w,PrintWriter pw) { 
		for(XglObject o:w.getObjects()){
			glCmdsForObject(o,pw);
		}
	}
	
	/**
	 * Syntax: XglCodeGen [xgl file] [code file]
	 * @param args
	 * @throws XglException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XglException, IOException { 
		BasicConfigurator.configure();
		
		System.out.println("XGL Code Gen Utility\n");
		
//		if(args.length < 2) { 
//			System.out.println("Invalid number of arguments, Usage: XglSerializer [inputFile] [outputFile]");
//		}
		
		String outClsName = "VB10GLModel";
		
		String inputFile = "etc/models/vb10k.xgl";//args[0];
		String outputFile = "etc/models/classes/"+outClsName+".java"; //args[1];
		
		System.out.println("Input: " + inputFile);
		System.out.println("Output: " + outputFile);
		
		XglParser parser = new XglParser();
		
		System.out.println("Parsing " + inputFile + "...");
		
		XglWorld w = parser.parse(inputFile);

		System.out.println("Generating code...");
		
		FileOutputStream fos = new FileOutputStream(outputFile);
		PrintWriter pw = new PrintWriter(fos);
		
//		pw.println("package com.vbar.burgundy.models;");
//		pw.println("");
		pw.println("import org.lwjgl.opengl.GL11;");
		pw.println("import org.lwjgl.opengl.GLContext;");
		pw.println("import com.vbar.opengl.OpenGLClassModel;");
		pw.println("import java.nio.FloatBuffer;");
		pw.println("");
		pw.println("public class " + outClsName + " implements OpenGLClassModel {");
			pw.println("\tprivate GLContext cx;");
			pw.println("\tprivate int displayListId;");
			
			meshRenderMethods(w,pw);
			
			pw.println("\tpublic void init() {");
			pw.println("\t\tdisplayListId = GL11.glGenLists(1);");
			pw.println("\t\tGL11.glNewList(displayListId,GL11.GL_COMPILE);");

			glCmdsForWorld(w, pw);
			
			pw.println("\t\tGL11.glEndList();");
			pw.println("\t}");
			
			pw.println("\tpublic void render() {");
				pw.println("GL11.glCallList(displayListId);");
			pw.println("\t}");
			
			pw.println("public double getModelMaxX() { return " + w.getMaxX() + "; } " +
					"public double getModelMaxY() { return " + w.getMaxY() +"; } " +
					"public double getModelMaxZ() { return " + w.getMaxZ() +"; } " +
					"public double getModelMinX() { return " + w.getMinX() +"; } " +
					"public double getModelMinY() { return " + w.getMinY() +"; } " +
					"public double getModelMinZ() { return " + w.getMinZ() +"; } ");
		pw.println("}");
		
		
		pw.flush();
		
		fos.close();
		
		System.out.println("Done!");
	}
}
