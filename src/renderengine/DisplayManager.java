package renderengine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS_CAP = 120; //limited anyway
	private static int fps = 0;
	
	private static long timeDifference = 0;
	private static long lastFrameTime;
	private static long delta;
	public static void createDisplay(){
		
		ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true);
		
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			//Display.setFullscreen(true);
			Display.create(new PixelFormat(), attribs);
			
			Display.setTitle("The Display Title");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		//Tell openGL to use whole display
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
		
	}
	
	public static void updateDisplay(){
		
		//Tell game to run a steady fps
		Display.sync(FPS_CAP);
		
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime);
		lastFrameTime = currentFrameTime;
		//Update FPS title
		timeDifference += delta;
		if(timeDifference > 1000){
			timeDifference -= 1000;
			fps = (int) (1000.0/delta);
			Display.setTitle("FPS: " + Integer.toString(fps));
		}
		//System.out.println(delta);
	}
	
	
	public static void closeDisplay(){
		Display.destroy();
	}

	public static long getCurrentTime(){
		return Sys.getTime();///Sys.getTimerResolution();
	}
	
	//not way to do this but working for visual purposes
	public static float getFrameTimeSeconds() {
		//yeah this is weird, changing 1/60 to 1/120 introduces weird flickering for moving boat...
		//return 1f/40f;
		return (delta/1000.0f);
	}
}
