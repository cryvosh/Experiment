package engine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.*;

import org.joml.*;

import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.math.BigInteger;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Camera3D implements Camera {
	
	private float pitch, yaw;
	private float nearClip;
	
	public Vector3f pos = new Vector3f(0.0f, 0.0f, 0.0f);
	private Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
	
	private Vector3f x = new Vector3f(1.0f, 0.0f, 0.0f);
	private Vector3f y = new Vector3f(0.0f, 1.0f, 0.0f);
	private Vector3f z = new Vector3f(0.0f, 0.0f, 1.0f);
	
	private float baseSpeed = 1.5f;
	private float ctrlMultiplier = 0.1f;
	private float shiftMultiplier = 20.0f;
	private float currSpeed;
	
	private Shader activeShader;
	
	public Camera3D (Shader shader, float fov) {
		
		activeShader = shader;
		activeShader.setUniform1f("iVerticalFOV", fov);
	}
	
	public void update () {
		rotate(Cursor.dy(), Cursor.dx());
		modifySpeed(Scroll.dy());
		modifyNearClip();
		setFront();
		
		currSpeed = (float) (baseSpeed * Window.getDT());
		currSpeed *= multiplySpeed(ctrlMultiplier, shiftMultiplier);
		
		// Local space movement
		if (Keyboard.isKeyPressed(GLFW_KEY_W)) {
			Vector3f frontClone = new Vector3f(front);
			pos.add(frontClone.mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_S)) {
			Vector3f frontClone = new Vector3f(front);
			pos.sub(frontClone.mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_A)) {
			Vector3f frontClone = new Vector3f(front);
			Vector3f global_y = new Vector3f(y);
			pos.sub(((frontClone.cross(global_y)).normalize()).mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_D)) {
			Vector3f frontClone = new Vector3f(front);
			Vector3f global_y = new Vector3f(y);
			pos.add(((frontClone.cross(global_y)).normalize()).mul(currSpeed));
		}
		
		// Global space movement
		if (Keyboard.isKeyPressed(GLFW_KEY_H)) {
			Vector3f global_x = new Vector3f(x);
			pos.add(global_x.mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_K)) {
			Vector3f global_x = new Vector3f(x);
			pos.sub(global_x.mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_Y)) {
			Vector3f global_y = new Vector3f(y);
			pos.add(global_y.mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_I)) {
			Vector3f global_y = new Vector3f(y);
			pos.sub(global_y.mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_U)) {
			Vector3f global_z = new Vector3f(z);
			pos.add(global_z.mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_J)) {
			Vector3f gloabl_z = new Vector3f(z);
			pos.sub(gloabl_z.mul(currSpeed));
		}
		
		if (Keyboard.isKeyPressed(GLFW_KEY_LEFT_ALT)) {
			System.out.println("POS: " + (int)pos.x + " " + (int)pos.y + " " + (int)pos.z);
		}
		
		Matrix4f view = new Matrix4f().lookAt(pos, front.add(pos), y);
		activeShader.setUniformMatrix4f("iViewMatrix", view);
		activeShader.setUniform3f("iPosition", pos.x, pos.y, pos.z);
	}
	
	private void rotate(double dPitch, double dYaw) {
		pitch += dPitch;
		yaw += dYaw;
		
		pitch = Math.max(-89.9f, Math.min(89.9f, pitch));
	}
	
	private void setFront() {
		front.x = (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
		front.y = (float) (Math.sin(Math.toRadians(pitch)));
		front.z = (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
		front.normalize();
	}
	
	private void modifySpeed(double dy) {
		if(dy > 0) {
			baseSpeed *= 1.2;
		} else if (dy < 0) {
			baseSpeed *= 0.8;
		}
	}
	
	private void modifyNearClip() {
		if (Keyboard.isKeyPressed(GLFW_KEY_RIGHT_BRACKET)) {
			nearClip += 10 * Window.getDT();
			activeShader.setUniform1f("iNearClip", nearClip);
		} else if (Keyboard.isKeyPressed(GLFW_KEY_LEFT_BRACKET)) {
			nearClip = Math.max(0, nearClip - 10 * (float)Window.getDT());
			activeShader.setUniform1f("iNearClip", nearClip);
		}
	}
}