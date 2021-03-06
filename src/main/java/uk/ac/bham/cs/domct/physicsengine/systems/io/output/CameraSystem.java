package uk.ac.bham.cs.domct.physicsengine.systems.io.output;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

import uk.ac.bham.cs.domct.physicsengine.component.Controllable;
import uk.ac.bham.cs.domct.physicsengine.component.Movable;
import uk.ac.bham.cs.domct.physicsengine.component.State;
import uk.ac.bham.cs.domct.physicsengine.component.View;
import uk.ac.bham.cs.domct.physicsengine.entity.Camera;
import uk.ac.bham.cs.domct.physicsengine.systems.EngineSystem;
import uk.ac.bham.cs.domct.physicsengine.systems.io.input.*;

/**
 * System that controls the camera, containing a static 
 * method to move a camera object.
 * 
 * @author Dominic Cogan-Tucker
 *
 */
public final class CameraSystem extends EngineSystem
{
	/**
	 * The mouse sensitivity.
	 */
	private float mouseSens = 0.1f;
	
	/**
	 * The Mouse mouse position for this frame and the previous.
	 */
	private double oldMouseX = 0, oldMouseY = 0, newMouseX, newMouseY;
	
	/**
	 * The state of the system camera.
	 */
	private State cameraState;
	
	/**
	 * The movable component of the system camera.
	 */
	private Movable cameraMovable;
	
	/**
	 * The controllable component of the system camera.
	 */
	
	private Controllable cameraControlable;
	
	/**
	 * The view of the camera of the system
	 */
	private View cameraView;
	
	/**
	 * Constructs a camera system, taking the camera to control as the input.
	 * 
	 * @param cam The camera for the system to control.
	 */
	public CameraSystem(Camera cam)
	{
		cameraState = cam.getComponent(State.class);
		cameraMovable = cam.getComponent(Movable.class);
		cameraControlable = cam.getComponent(Controllable.class);
		cameraView = cam.getComponent(View.class);
	}
	
	/**
	 * Calls the cameras move method, to check if any movement is required each frame.
	 */
	@Override
	public void update()
	{
		move();
	}
	
	/**
	 * Updates the velocity through key actions of a given camera, moving it in the
	 * scene respectively.
	 */
	private void move()
	{
		float x = (float) Math.sin(Math.toRadians(-cameraState.rotation.y));
		float z = (float) Math.cos(Math.toRadians(cameraState.rotation.y));

		Vector3f direction = new Vector3f();

		if (Keyboard.isPressed(GLFW_KEY_A))
		{
			direction.add(new Vector3f(-z, 0, x));
		} else if (Keyboard.isPressed(GLFW_KEY_D))
		{
			direction.add(new Vector3f(z, 0, -x));
		}

		if (Keyboard.isPressed(GLFW_KEY_W))
		{
			direction.add(new Vector3f(-x, 0, -z));
		} else if (Keyboard.isPressed(GLFW_KEY_S))
		{
			direction.add(new Vector3f(x, 0, z));
		}

		if (Keyboard.isPressed(GLFW_KEY_SPACE))
		{
			direction.add(new Vector3f(0, 1, 0));
		} else if (Keyboard.isPressed(GLFW_KEY_LEFT_CONTROL))
		{
			direction.add(new Vector3f(0, -1, 0));
		}

		if (!direction.equals(new Vector3f()))
		{
			direction.normalize();
		}

		cameraMovable.velocity = direction.mul(cameraControlable.speed);
		cameraState.position.add(cameraMovable.velocity);

		newMouseX = Mouse.getX();
		newMouseY = Mouse.getY();

		float dx = (float) (newMouseX - oldMouseX);
		float dy = (float) (newMouseY - oldMouseY);
		
		oldMouseX = newMouseX;
		oldMouseY = newMouseY;

		if (Mouse.isPressed(GLFW_MOUSE_BUTTON_RIGHT))
		{
			cameraState.rotation.add(new Vector3f(dy * mouseSens, dx * mouseSens, 0));
			cameraView.window.disableMouse(true);
		} else
		{
			cameraView.window.disableMouse(false);
		}
	}
}
