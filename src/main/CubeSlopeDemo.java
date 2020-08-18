package main;

import org.joml.Vector2f;
import org.joml.Vector3f;

import application.Application;
import ecs.component.Weight;
import ecs.entity.Camera;
import ecs.entity.CollidableCube;
import ecs.entity.Plane;
import ecs.entity.Sphere;
import io.output.DisplayManager;
import io.output.Window;

public class CubeSlopeDemo extends Application
{

	@Override
	protected void windowSettings(Window window)
	{
		window.setDimensions(3 * DisplayManager.getScreenWidth() / 4, 3 * DisplayManager.getScreenHeight() / 4);
		window.setTitle("Slope Demo");
	}

	@Override
	protected void scene(Camera camera)
	{
		Plane slope = Plane.create(new Vector3f(0, -2.5f, -7.5f), new Vector3f(0, 0, -20), new Vector2f(5, 5));
		
		CollidableCube cube = CollidableCube.create(
				new Vector3f(-1f, 1f, -7.5f), 
				new Vector3f(0, 0, 0), 
				0.8f, 1);
		slope.getComponent(Weight.class).restitution = 0.3f;
		cube.getComponent(Weight.class).restitution = 0.3f;
		slope.getComponent(Weight.class).friction = 0.3f;
		cube.getComponent(Weight.class).friction = 0.3f;
	}
	
	public static void main(String[] args)
	{
		new CubeSlopeDemo().start();
	}
}
