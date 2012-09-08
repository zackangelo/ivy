package com.redstone.opengl.util;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class ReferenceFrameMatrix {

	public final static Matrix4f getMatrix(Vector3f up, Vector3f forward) {
		Vector3f xAxis = new Vector3f();
		Vector3f yAxis = new Vector3f(up);
		Vector3f zAxis = new Vector3f(forward);
		
		Vector3f.cross(yAxis, zAxis, xAxis);
		
		Matrix4f m = new Matrix4f();
		
		m.m00 = xAxis.x;
		m.m01 = xAxis.y; 
		m.m02 = xAxis.z; 
		m.m03 = 0.0f;
		
		m.m10 = yAxis.x; 
		m.m11 = yAxis.y; 
		m.m12 = yAxis.z; 
		m.m13 = 0.0f;
		
		m.m20 = zAxis.x; 
		m.m21 = zAxis.y; 
		m.m22 = zAxis.z; 
		m.m23 = 0.0f;
		
		return m;
		
//		return new float[] { 
//				xAxis.x, xAxis.y, xAxis.z, 0.0f,
//				yAxis.x, yAxis.y, yAxis.z, 0.0f,
//				zAxis.x, zAxis.y, zAxis.z, 0.0f,
//				position.x, position.y, position.z, 1.0f
//		};
	}
}
