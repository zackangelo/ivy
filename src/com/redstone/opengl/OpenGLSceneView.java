package com.redstone.opengl;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public abstract class OpenGLSceneView {
	private final static Logger log = Logger.getLogger(OpenGLSceneView.class);
	
	private GLCanvas glCanvas;
	private GLCanvas shareWithCanvas;
	private Composite parent;
	
	private boolean inited;
	
	public OpenGLSceneView(Composite parent) {
		this(parent,null);
		
	}

	public OpenGLSceneView(Composite parent,GLCanvas shareWithCanvas) {
		this.parent = parent;
		this.shareWithCanvas = shareWithCanvas;
		
		initOpenGL(shareWithCanvas);
	}
	
	/**
	 * 
	 * @param parentCanvas canvas to share display list and texture namespaces with
	 */
	private void initOpenGL(GLCanvas shareWithCanvas) {
		GLData glData = new GLData();
		glData.doubleBuffer = true;
		glData.shareContext = shareWithCanvas;
		glData.sampleBuffers = 4;
		
		glCanvas = new GLCanvas(parent,SWT.NONE,glData);
		glCanvas.setCurrent();
		
		try {
			GLContext.useContext(glCanvas);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public final void render() {
		if(!inited) {
			initScene();
			inited = true;
		}
		
//		log.debug("Rendering...");
		
		if(!glCanvas.isDisposed()) {
			glCanvas.setCurrent();
			
			try {
				GLContext.useContext(glCanvas);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			
			configureOpenGL();
			configureProjection();
			
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			
			renderBackground();
			transformView();
			renderGeometry();
			renderOverlay();
			
			glCanvas.swapBuffers();
		}
	}
	
	public GLCanvas getGLCanvas() {
		return glCanvas;
	}
	
	protected GLCanvas getSharedGLCanvas() {
		return shareWithCanvas;
	}
	
	
	protected abstract void initScene();
	
	protected abstract void configureOpenGL();
	protected abstract void configureProjection();
	
	protected abstract void renderBackground();
	protected abstract void renderGeometry();
	protected abstract void transformView();
	protected abstract void renderOverlay();
}
