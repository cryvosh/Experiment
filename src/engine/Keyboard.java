package engine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Keyboard implements GLFWKeyCallbackI{
	
	private static boolean[] keys = new boolean[GLFW_KEY_LAST];
	private static boolean[] keysDown = new boolean[GLFW_KEY_LAST];
	private static boolean[] keysUp = new boolean[GLFW_KEY_LAST];
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key < 0) {
			return;
		}
		
		if (action != GLFW_RELEASE && keys[key] == false) {
			keysDown[key] = true;
			keysUp[key] = false;
		}
		else if (action == GLFW_RELEASE && keys[key] == true) {
			keysDown[key] = false;
			keysUp[key] = true;
		}
		
		keys[key] = (action != GLFW_RELEASE);
		
		if (Keyboard.keyPressed(GLFW_KEY_GRAVE_ACCENT)) {
			glfwTerminate();
			Main.runNextApp();
		}
	}
	
	public static boolean keyPressed(int key) {
		boolean temp = keysDown[key];
		keysDown[key] = false;
		return temp;
	}
	
	public static boolean keyReleased(int key) {
		boolean temp = keysUp[key];
		keysUp[key] = false;
		return temp;
	}
	
	public static boolean keyHeldDown(int key) {
		return keys[key];
	}
}
