
import java.nio.FloatBuffer;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Display;
import org.lwjgl.opengl.GL11;

import com.redstone.opengl.OrbitView;


public class CodeGenViewerApp {
	public static class CodeGenModelView extends OrbitView {
		private VB10GLModel vb10model;
		
		public CodeGenModelView(GLCanvas glCanvas) {
			super(glCanvas);
			
			vb10model = new VB10GLModel();
		}
		
		public double getModelMaxX() { return vb10model.getModelMaxX(); }
		public double getModelMaxY() { return vb10model.getModelMaxY(); }
		public double getModelMaxZ() { return vb10model.getModelMaxZ(); }
		public double getModelMinX() { return vb10model.getModelMinX(); }
		public double getModelMinY() { return vb10model.getModelMinY(); }
		public double getModelMinZ() { return vb10model.getModelMinZ(); }

		@Override
		protected void render() {
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
			GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//			GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
			
			GL11.glPushMatrix();
				GL11.glLoadIdentity();
				GL11.glScalef(zoom, zoom, zoom);
				
	    		float LightAmbient[]= { 0.5f, 0.5f, 0.5f, 0.0f }; 
		        float LightDiffuse[]= { 1.0f, 1.0f, 1.0f, 1.0f };
		        float LightPosition[]= { 4000.0f, 400.0f, 2000.0f, 0.0f };	
	
		        FloatBuffer ltDiffuse = FloatBuffer.wrap(LightDiffuse);
		        FloatBuffer ltAmbient = FloatBuffer.wrap(LightAmbient);
		        FloatBuffer ltPosition = FloatBuffer.wrap(LightPosition);
		        
		        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, ltDiffuse); 
		        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, ltAmbient);
		        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, ltPosition);
		        GL11.glLightf(GL11.GL_LIGHT1, GL11.GL_SPOT_CUTOFF, 180.0f);
		        
		        GL11.glEnable(GL11.GL_LIGHTING);
		        GL11.glEnable(GL11.GL_LIGHT1);
		        
	        GL11.glPopMatrix();
	        
	        vb10model.render();
			
		}

		public void initModel() {
			GL11.glEnable(GL11.GL_NORMALIZE);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			System.out.println("Creating display lists...");
			vb10model.init();
		}
	}
	
	public static class CodeGenViewerWindow extends SimpleOpenGLWindow {
		private CodeGenModelView modelView;
		
		public CodeGenViewerWindow() {
		
		}
		
		@Override
		protected OrbitView getModelView(GLCanvas glCanvas) {
			if(modelView == null) {
				modelView = new CodeGenModelView(glCanvas);
			}
			
			return modelView;
		}
		
		public void initWindow() {
			modelView.initModel();
		}
		
	}
	public static void main(String[] args) {
		System.out.println("Starting ...");
		BasicConfigurator.configure();

		ApplicationWindow mainWindow = new CodeGenViewerWindow();
		mainWindow.setBlockOnOpen(true);
		mainWindow.open();
		
		Display.getCurrent().dispose();	
	}
}
