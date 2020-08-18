package main;

import org.joml.Vector2f;
import org.joml.Vector3f;

import application.Application;
import ecs.component.Moveable;
import ecs.component.Weight;
import ecs.entity.Camera;
import ecs.entity.CollidableCube;
import ecs.entity.Plane;
import ecs.entity.Sphere;
import io.output.DisplayManager;
import io.output.Window;

public class CubeStackDemo extends Application
{

	@Override
	protected void windowSettings(Window window)
	{
		window.setDimensions(3 * DisplayManager.getScreenWidth() / 4, 3 * DisplayManager.getScreenHeight() / 4);
		window.setTitle("Cubestack Demo");
	}

	@Override
	protected void scene(Camera camera)
	{
		Plane plane = Plane.create(new Vector3f(0, -2.5f, -7.5f), new Vector3f(0, 0, 0), new Vector2f(5, 5));
		
		CollidableCube cube = CollidableCube.create(
				new Vector3f(0f, 0f, -7.5f), 
				new Vector3f(0, 0, 0), 
				1, 1);
		CollidableCube cube1 = CollidableCube.create(
				new Vector3f(0, 2f, -7.5f), 
				new Vector3f(0, 0, 0), 
				1, 1);
		CollidableCube cube2 = CollidableCube.create(
				new Vector3f(0, 4f, -7.5f), 
				new Vector3f(0, 0, 0), 
				1, 1);
		CollidableCube cube3 = CollidableCube.create(
				new Vector3f(0, 6f, -7.5f), 
				new Vector3f(0, 0, 0), 
				1, 1);
		
		plane.getComponent(Weight.class).restitution = 0.2f;
		cube.getComponent(Weight.class).restitution = 0.2f;
		cube1.getComponent(Weight.class).restitution = 0.2f;
		cube2.getComponent(Weight.class).restitution = 0.2f;
		cube3.getComponent(Weight.class).restitution = 0.2f;
	}

	public static void main(String[] args)
	{
		new CubeStackDemo().start();
	}
}
