import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Display;

public abstract class SimpleOpenGLApp {
	public static void main(String[] args) {
		ApplicationWindow mainWindow = new TriPolyTestWindow();
		
		mainWindow.setBlockOnOpen(true);
		mainWindow.open();
		
		Display.getCurrent().dispose();
	}
}
