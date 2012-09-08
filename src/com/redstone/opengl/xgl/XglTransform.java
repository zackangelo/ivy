package com.redstone.opengl.xgl;

import java.io.Serializable;

import org.lwjgl.util.vector.Vector3f;

public class XglTransform implements Serializable {
	private Vector3f position;
	private Vector3f up;
	private Vector3f forward;

	public XglTransform() { }

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setUp(Vector3f up) {
		this.up = up;
	}

	public void setForward(Vector3f forward) {
		this.forward = forward;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getUp() {
		return up;
	}

	public Vector3f getForward() {
		return forward;
	}
	
	public float[] getMatrix() { 
		Vector3f upNeg = new Vector3f();
		up.negate(upNeg);
		
		Vector3f xNeg = new Vector3f();
		
		Vector3f xAxis = new Vector3f();
		Vector3f yAxis = new Vector3f(up);
		Vector3f zAxis = new Vector3f(forward);
		
		
		Vector3f.cross(yAxis, zAxis, xAxis);
		
		return new float[] { 
				xAxis.x, xAxis.y, xAxis.z, 0.0f,
				yAxis.x, yAxis.y, yAxis.z, 0.0f,
				zAxis.x, zAxis.y, zAxis.z, 0.0f,
				position.x, position.y, position.z, 1.0f
		};
	}
}
