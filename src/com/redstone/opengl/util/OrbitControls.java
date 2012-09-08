package com.redstone.opengl.util;

import java.nio.FloatBuffer;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;

public class OrbitControls 
	implements MouseMoveListener,MouseWheelListener,MouseListener,Listener {

	private GLCanvas glCanvas;
	private Quaternion q;
	private boolean rightMouseButtonDown;
	private boolean leftMouseButtonDown;
	private boolean shiftDown;
	private int lastMouseX;
	private int lastMouseY;
	
	private OrbitUpdateListener listener;
	
	private final static float PIOVER180 = (float) (Math.PI / 180);

	public static interface OrbitUpdateListener { 
		public void onOrbitControls();
	}
	
	public OrbitControls(GLCanvas glCanvas,OrbitUpdateListener listener) {
		this.glCanvas = glCanvas;
		this.glCanvas.addMouseMoveListener(this);
		this.glCanvas.addMouseListener(this);
		this.glCanvas.addMouseWheelListener(this);
		
		q = new Quaternion();
		Quaternion.setIdentity(q);
		
		this.listener = listener;
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
	
	public void applyOrbitXforms() { 
		float[] rotMatrix = matrixFromQuaternion(q);
		FloatBuffer rotMatrixBuf = FloatBuffer.wrap(rotMatrix);
		
		GL11.glLoadIdentity();
		GL11.glMultMatrix(rotMatrixBuf);
		
//		double modelWidth = getModelWidth();
//		double modelHeight = getModelHeight();
//		double modelDepth = getModelDepth();
	}
	
	public void resetView() { 
//		zoom = 0.5f;
//		panX = 0.0f;
//		panY = 0.0f;
		Quaternion.setIdentity(q);
	}
	
	@Override
	public void mouseScrolled(MouseEvent e) {
//		if (e.count > 0){
//			zoom *= 2;
//		}
//		
//		else if (e.count < 0){
//			zoom /= 2;
//		}
//		
//		queueRedraw();
	}
	
	public void mouseMove(MouseEvent e) {
		//only process if mouse button down (i.e., drag)
		if(leftMouseButtonDown || rightMouseButtonDown) {
			
			//also only process if this isn't the first event of the drag
			if((lastMouseX != 0) && (lastMouseY != 0)) {
				int deltaX = e.x - lastMouseX;
				int deltaY = e.y - lastMouseY;
				
				if(rightMouseButtonDown){
//					panX += deltaX*20;
//					panY -= deltaY*20;
				} else if(leftMouseButtonDown) {
					Matrix4f localRot = new Matrix4f();
					localRot.load(FloatBuffer.wrap(matrixFromQuaternion(q)));
					
					Matrix4f localToGlobalRot = new Matrix4f();
					Matrix4f.invert(localRot, localToGlobalRot);
					
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
					
					listener.onOrbitControls();
				}
			}
				
			lastMouseX = e.x;
			lastMouseY = e.y;
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

	@Override
	public void handleEvent(Event event) {
	}
}
