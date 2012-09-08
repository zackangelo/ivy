package com.redstone.opengl;

import java.nio.FloatBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;

/**
 * View that provides three dimensional 
 * zoom, pan, rotate functionality.
 * 
 * @author Administrator
 *
 */
public abstract class OrbitView implements OpenGLView,MouseMoveListener,MouseWheelListener,MouseListener,Listener {
	private GLCanvas glCanvas;
	private final static float PIOVER180 = (float) (Math.PI / 180);

	public OrbitView(GLCanvas glCanvas) {
		this.glCanvas = glCanvas;
		
		this.glCanvas.addMouseMoveListener(this);
		this.glCanvas.addMouseListener(this);
		this.glCanvas.addMouseWheelListener(this);
		
//		this.glCanvas.addKeyListener(this);
		Display.getCurrent().addFilter(SWT.KeyDown, this);
		Display.getCurrent().addFilter(SWT.KeyUp,this);
		
		setDrawAxis(false);
		
		this.lastMouseX = 0;
		this.lastMouseY = 0;
		
		q = new Quaternion();
		Quaternion.setIdentity(q);
	}
	
	public GLCanvas getGLCanvas() {
		return glCanvas;
	}
	
	public void setGLCanvas(GLCanvas glCanvas) {
		this.glCanvas = glCanvas;
	}
	
	// VIEW RENDERING
	protected abstract void render();
	
	// DIMENSION INFORMATION FOR CENTERING/ROTATION
	public abstract double getModelMaxX();	//X
	public abstract double getModelMaxY();	//Y
	public abstract double getModelMaxZ();	//Z
	
	public abstract double getModelMinX();
	public abstract double getModelMinY();
	public abstract double getModelMinZ();


	private Runnable renderer = new Runnable() { 
		public void run() {
			if(glCanvas.isDisposed()) return;
			
			glCanvas.setCurrent();
			try { GLContext.useContext(glCanvas); } catch(LWJGLException e) { e.printStackTrace(); }
			
			renderView(); 
						
			glCanvas.swapBuffers();
		}
	};
	
	public void queueRedraw() { 
		Display.getCurrent().asyncExec(renderer);
	}
	
	
	public final void renderView() {
		float[] rotMatrix = matrixFromQuaternion(q);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
//		FIXME don't reallocate each frame
		FloatBuffer rotMatrixBuf = BufferUtils.createFloatBuffer(rotMatrix.length);
		rotMatrixBuf.put(rotMatrix);
		rotMatrixBuf.rewind();
		
//		FloatBuffer rotMatrixBuf = FloatBuffer.wrap(rotMatrix);
	
		double modelWidth = getModelWidth();
		double modelHeight = getModelHeight();
		double modelDepth = getModelDepth();
		
		GL11.glLoadIdentity();
		
		//center on screen
//		GL11.glTranslated(-modelWidth/2, -modelHeight/2, 0d);
		
		//rotate about point (translate,rotate,translate back)
//		GL11.glTranslated(modelWidth/2, modelHeight/2, modelDepth/2);
		GL11.glMultMatrix(rotMatrixBuf);
//		GL11.glTranslated(-modelWidth/2, -modelHeight/2, -modelDepth/2);
		GL11.glScalef(zoom, zoom, zoom);
		GL11.glTranslatef(panX, panY, 0.0f);

		render();
	}

	public double getModelDepth() {
		return getModelMaxZ() - getModelMinZ();
	}

	public double getModelHeight() {
		return getModelMaxY() - getModelMinY();
	}

	public double getModelWidth() {
		return getModelMaxX() - getModelMinX();
	}
	
	// KEYBOARD/MOUSE HANDLING
	private boolean rightMouseButtonDown;
	private boolean leftMouseButtonDown;
	private boolean shiftDown;
	private boolean ctrlDown;
	private int lastMouseX;
	private int lastMouseY;
	private Quaternion q;
	protected float zoom = 0.5f;
	private float panX = 0.0f;
	private float panY = 0.0f;
	
	
	public void resetView() { 
		zoom = 0.5f;
		panX = 0.0f;
		panY = 0.0f;
		
		Quaternion.setIdentity(q);
	}
	
	private static float[] matrixFromQuaternion(Quaternion q) { 
		float 	x = q.x, 
				y = q.y, 
				z = q.z, 
				w = q.w;
		
		float x2 = x * x;
		float y2 = y * y;
		float z2 = z * z;
		float xy = x * y;
		float xz = x * z;
		float yz = y * z;
		float wx = w * x;
		float wy = w * y;
		float wz = w * z;

		return new float[] { 
				1.0f - 2.0f * (y2 + z2), 2.0f * (xy - wz), 2.0f * (xz + wy), 0.0f,
				2.0f * (xy + wz), 1.0f - 2.0f * (x2 + z2), 2.0f * (yz - wx), 0.0f,
				2.0f * (xz - wy), 2.0f * (yz + wx), 1.0f - 2.0f * (x2 + y2), 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f 
		};
	}

	public void handleEvent(Event e) {
		if(e.type == SWT.KeyDown) {
			if((e.keyCode & SWT.SHIFT) != 0) shiftDown = true;
			if((e.keyCode & SWT.CONTROL) != 0) ctrlDown = true;
		} else if(e.type == SWT.KeyUp) {
			if((e.keyCode & SWT.SHIFT) != 0) shiftDown = false;
			if((e.keyCode & SWT.CONTROL) != 0) {
				ctrlDown = false;
			}
		}
		
		if(e.character == 'r') {
			System.out.println("Resetting view...");
			resetView();
			queueRedraw();
		}
	}

	@Override
	public void mouseScrolled(MouseEvent e) {
		if (e.count > 0){
			zoom *= 2;
		}
		
		else if (e.count < 0){
			zoom /= 2;
		}
		
		queueRedraw();
	}
	
	public void mouseMove(MouseEvent e) {
		//only process if mouse button down (i.e., drag)
		if(leftMouseButtonDown || rightMouseButtonDown) {
			
			//also only process if this isn't the first event of the drag
			if((lastMouseX != 0) && (lastMouseY != 0)) {
				int deltaX = e.x - lastMouseX;
				int deltaY = e.y - lastMouseY;
				
				if(rightMouseButtonDown){
					panX += deltaX*20;
					panY -= deltaY*20;
				} else if(leftMouseButtonDown) {
					Matrix4f localRot = new Matrix4f();
					localRot.load(FloatBuffer.wrap(matrixFromQuaternion(q)));
					
					Matrix4f localToGlobalRot = new Matrix4f();
					Matrix4f.invert(localRot, localToGlobalRot);
					
					Vector4f localRotAxis;
					Vector4f globalRotAxis;
					
					if(shiftDown) {
						globalRotAxis = new Vector4f(0,0,1,0);
						globalRotAxis.w = (float) 
							((e.x > glCanvas.getSize().x/2 ? -1 : 1) * 
									(.25*Math.sqrt(deltaX*deltaX+deltaY*deltaY)*PIOVER180));
					} else {
						globalRotAxis = new Vector4f(deltaY,deltaX,0,0).normalise(null);
						//.w is interpreted as the rotation angle in .setFromAxisAngle()
						globalRotAxis.w = (float) -(.25*Math.sqrt(deltaX*deltaX+deltaY*deltaY)*PIOVER180);
	
					}
					
					Quaternion quatRot = new Quaternion();
					quatRot.setFromAxisAngle(globalRotAxis);
					
					q = Quaternion.mul(q, quatRot, null);
				}
			}
				
			lastMouseX = e.x;
			lastMouseY = e.y;
			
			queueRedraw();
		} else {
			lastMouseX = 0;
			lastMouseY = 0;
		}
	}
		
		
	

	public void mouseDoubleClick(MouseEvent e) { }

	public void mouseDown(MouseEvent e) {
		if(e.button == 1) { 
			leftMouseButtonDown = true;
		} else if(e.button == 3) {
			rightMouseButtonDown = true;
		}
	}

	public void mouseUp(MouseEvent e) {
		if(e.button == 1) { 
			leftMouseButtonDown = false;
		} else if(e.button == 3){
			rightMouseButtonDown = false;
		}
	}
	

	// AXIS INDICATOR RENDERING 
	private boolean drawAxis;
	
	public void setDrawAxis(boolean drawAxis) {
		this.drawAxis = drawAxis;
	}
	
	public boolean isDrawAxis() { 
		return drawAxis;
	}
}

