import org.eclipse.swt.opengl.GLCanvas;
import org.lwjgl.opengl.GL11;

import com.redstone.opengl.OrbitView;

public class TriPolyTestWindow extends SimpleOpenGLWindow {
	public static class TriPolyModelView extends OrbitView {
		public TriPolyModelView(GLCanvas glCanvas) {
			super(glCanvas);
		}

		public double getModelMaxX() { return 1; }
		public double getModelMaxY() { return 1; }
		public double getModelMaxZ() { return 1; }
		public double getModelMinX() { return -1; }
		public double getModelMinY() { return -1; }
		public double getModelMinZ() {	return -1; }

		@Override
		protected void render() {
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
				GL11.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
				GL11.glColor3f(1.0f, 0.0f, 0.0f);
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glVertex3f( -1.0f, 1.0f, 0.0f);
					GL11.glVertex3f(-1.0f,-1.0f, 0.0f);
					GL11.glVertex3f( 1.0f,-1.0f, 0.0f);
					GL11.glVertex3f( 1.0f,1.0f, 0.0f);	
				GL11.glEnd();
			GL11.glPopMatrix();
		}
	}
	
	private TriPolyModelView modelView;

	@Override
	protected OrbitView getModelView(GLCanvas glCanvas) {
		if(modelView == null) {
			modelView = new TriPolyModelView(glCanvas);
		}
		
		return modelView;
	}
	
	public void initWindow() { }

}
