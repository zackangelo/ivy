package com.redstone.opengl.util;

public class ColorGradient {
	private final static int MAX_GRAD_STEPS = 128;
	private final static int R = 0, G = 1, B = 2;
	
	public static float[][] createRgbGradient(float[] startColor, float[] endColor, int steps) {
		if(startColor.length != 3 || endColor.length != 3) {
			throw new IllegalArgumentException("Invalid number of elements in startColor or endColor");
		}
		
		if(steps > MAX_GRAD_STEPS) {
			throw new IllegalArgumentException("Too many steps");
		}
		
		float[][] result = new float[steps][3];
		
		float rStep = (endColor[R] - startColor[R]) / steps;
		float gStep = (endColor[G] - startColor[G]) / steps;
		float bStep = (endColor[B] - startColor[B]) / steps;
		
		for(int i=0;i<steps;i++) {
			result[i][R] = startColor[R] + (rStep * i);
			result[i][G] = startColor[G] + (gStep * i);
			result[i][B] = startColor[B] + (bStep * i);
		}
		
		return result;
	}
}
