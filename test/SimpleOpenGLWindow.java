import java.io.IOException;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.redstone.opengl.OrbitView;


public abstract class SimpleOpenGLWindow extends ApplicationWindow {

	private GLCanvas glCanvas;
	private OrbitView modelView;
	
	private Runnable renderLoop = new Runnable() { 
		public void run() { 
			if(glCanvas.isDisposed()) return;
			
			glCanvas.setCurrent();
			try { GLContext.useContext(glCanvas); } catch(LWJGLException e) { e.printStackTrace(); }
			
			render(); 
						
			glCanvas.swapBuffers();
			Display.getCurrent().timerExec(25, this);
		}
	};
	
	public SimpleOpenGLWindow() {
		super(null);
	}
	
	protected abstract OrbitView getModelView(GLCanvas glCanvas);

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		
		shell.setText("OpenGL Test");
		shell.setSize(800,800);
	}

	private void render() {
		modelView.renderView();
	}
	
	@Override
	protected Control createContents(Composite parent) {
		GLData glData = new GLData();
		glData.doubleBuffer = true;
		
		glCanvas = new GLCanvas(parent,SWT.BORDER,glData);
		
		modelView = getModelView(glCanvas);

		glCanvas.setCurrent();
		
		try {
			GLContext.useContext(glCanvas);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		glCanvas.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				updateProjectionMatrix();
			}
		});
		
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		initWindow();
		
//		renderLoop.run();
		
		return glCanvas;
	}
	
	public void updateProjectionMatrix() {
//		float zoom = modelView.getZoomFactor();
//		System.out.println("Update projection (ogl window): " + zoom);
		Rectangle bounds = glCanvas.getBounds();
//		float fAspect = (float) bounds.width / (float) bounds.height;
		glCanvas.setCurrent();
		try {
			GLContext.useContext(glCanvas);
		} catch(LWJGLException e) { e.printStackTrace(); }
		GL11.glViewport(0, 0, bounds.width, bounds.height);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
//		GLU.gluPerspective(45.0f, fAspect, 0.5f, 400.0f);
//		GL11.glOrtho(-5000, 5000, -5000, 5000, -10000, 10000);
		
//		GL11.glOrtho(
//				(-modelView.getModelWidth() * 1.5) * zoom,
//				(modelView.getModelWidth() * 1.5) * zoom ,
//				(-modelView.getModelHeight() * 1.5) * zoom,
//				(modelView.getModelHeight() * 1.5) * zoom,
//				(-modelView.getModelDepth()* 10.5) * zoom,
//				modelView.getModelDepth() * 10.5);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	public abstract void initWindow(); 
}
