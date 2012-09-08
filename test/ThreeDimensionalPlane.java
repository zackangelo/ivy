//import javax.vecmath.*;

import org.lwjgl.util.vector.*;

public class ThreeDimensionalPlane{
	
	double xzRot;
	double yzRot;
	double Rot;
	
	public ThreeDimensionalPlane(Vector3f a, Vector3f b, Vector3f c){
		
		//Get the two vectors on the plane made up of AB and AC
		
		Vector3f AB = new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z);
	    Vector3f AC = new Vector3f(c.x - a.x, c.y - a.y, c.z - a.z);
		
	    //Normal vector of rotated plane.
	    Vector3f normalRotated = new Vector3f();
	    Vector3f.cross(AB, AC, normalRotated);
	    
	    //Normal vector with respect to the origin
	    Vector3f Vec = new Vector3f(normalRotated.x - a.x, normalRotated.y - a.y, normalRotated.z - a.z);
	  		
		//Vector of Normal Rotated vector in the x-z plane
	    Vector3f xzVec = new Vector3f(normalRotated.x - a.x, normalRotated.y - a.y, 0);
				
		//Vector of Normal Rotated vector in the y-z plane
	    Vector3f yzVec = new Vector3f(0, normalRotated.y - a.y , normalRotated.z - a.z);
		    
	    //Reference vector
	    Vector3f refVecx = new Vector3f(2,0,0);
	    Vector3f refVecy = new Vector3f(0,2,0);
	    
//	    Vector3f refVec = new Vector3f(new double[] {0,2,0});
	    
	    //Angle between two vectors 
	    float xTheta = Vector3f.angle(refVecx,xzVec);
		float yTheta = Vector3f.angle(refVecy,yzVec);
		
		xzRot = xTheta*(180/Math.PI);
		yzRot = yTheta*(180/Math.PI);
		
//		System.out.println("x-z Rotation: " + xzRot + "\ny-z Rotation: " + yzRot + "\n");
//		Matrix3f rotationMatrixX = new Matrix3f();
//		rotationMatrixX.m00 = (float) Math.cos(xTheta);
//		rotationMatrixX.m01 = 0;
//		rotationMatrixX.m01 = (float) Math.sin(xTheta);
//		rotationMatrixX.m10 = 0;
//		rotationMatrixX.m11 = 1;
//		rotationMatrixX.m12 = 0;
//		rotationMatrixX.m20 = (float) -Math.sin(xTheta);
//		rotationMatrixX.m21 = 0;
//		rotationMatrixX.m22 = (float) Math.cos(xTheta);
//				
//		Matrix3f rotationMatrixY = new Matrix3f();
//		rotationMatrixY.m00 = 1;
//		rotationMatrixY.m01 = 0;
//		rotationMatrixY.m02 = 0;
//		rotationMatrixY.m10 = 0;
//		rotationMatrixY.m11 = (float) Math.cos(xTheta);
//		rotationMatrixY.m12 = (float) -Math.sin(xTheta);
//		rotationMatrixY.m20 = 0;
//		rotationMatrixY.m21 = (float) Math.sin(xTheta);
//		rotationMatrixY.m22 = (float) Math.cos(xTheta);
//		rotationMatrixY.invert();
		
		Matrix4f rotationMatrix = new Matrix4f();
		
		rotationMatrix.rotate(xTheta,new Vector3f(0,0,1));
		rotationMatrix.rotate(yTheta,new Vector3f(1,0,0));
		
//		Matrix3f.mul(rotationMatrixX, rotationMatrixY,rotationMatrix);
		System.out.println(rotationMatrix + "\n");
	}
	

	public static double xzRot (ThreeDimensionalPlane x){
		
		return x.xzRot;
		
		
	}
	
	public static double yzRot (ThreeDimensionalPlane x){
		
		return x.yzRot;
		
	}
	
	public static double Rot (ThreeDimensionalPlane x){
		
		return x.Rot;
		
	}



public static void main(String [] args){
	
	Vector3f A = new Vector3f(-685.8f, -12000.0f, -2606.0f);
	Vector3f B = new Vector3f(-685.8f, -10000.0f, 0.0f);
	Vector3f C = new Vector3f(685.8f, -12000.0f, 0.0f);
	
	ThreeDimensionalPlane x = new ThreeDimensionalPlane(A,B,C);
	
	System.out.println("x-z Rotation: " + x.xzRot + "\ny-z Rotation: " + x.yzRot + "\n");
	System.out.println("Vec: " + x.Rot);
	
	
}

}