package core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Shader {
	
	private final int programID;
	private Map<String, Integer> locationCache = new HashMap<>();
	
	public Shader(String vertPath, String fragPath) {
		programID = load(vertPath, fragPath);
	}
	
	public void enable() {
		glUseProgram(programID);
	}
	
	public void disable() {
		glUseProgram(0);
	}
	
	public int getUniform(String name) {
		if (locationCache.containsKey(name)) {
			return locationCache.get(name);
		}
		
		int location = glGetUniformLocation(programID, name);
		
		if (location == -1) {
			System.out.println("Invalid uniform " + name);
		} else {
			locationCache.put(name, location);
		}
		
		return location;
	}
	
	public void setUniform1f(String name, float value) {
		glUniform1f(getUniform(name), value);
	}
	
	public void setUniform2f(String name, float x, float y) {
		glUniform2f(getUniform(name), x, y);
	}
	
	private int load(String vertPath, String fragPath) {		
		
		int vertShader = glCreateShader(GL_VERTEX_SHADER);
		int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
		
		String vertSource = FileIO.readFile(vertPath);
		String fragSource = FileIO.readFile(fragPath);
		
		glShaderSource(vertShader, vertSource);
		glShaderSource(fragShader, fragSource);
		
		glCompileShader(vertShader);
		glCompileShader(fragShader);
		
		if (glGetShaderi(vertShader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.out.println("Failed to compile vertex shader");
			System.out.println(glGetShaderInfoLog(vertShader));
			return -1;
		}
		
		if (glGetShaderi(fragShader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.out.println("Failed to compile fragment shader");
			System.out.println(glGetShaderInfoLog(fragShader));
			return -1;
		}
		
		int program = glCreateProgram(); 
		
		glAttachShader(program, vertShader);
		glAttachShader(program, fragShader);
		glLinkProgram(program);
		glValidateProgram(program);
		
		glDeleteShader(vertShader);
		glDeleteShader(fragShader);
		
		return program;
		
	}

}