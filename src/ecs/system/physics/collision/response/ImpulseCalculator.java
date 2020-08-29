package ecs.system.physics.collision.response;

import org.joml.Vector3f;

import ecs.component.Moveable;
import ecs.component.State;
import ecs.component.Weight;
import ecs.entity.Entity;
import ecs.entity.MoveableCollidableBall;
import ecs.system.physics.collision.Collision;
import ecs.system.physics.collision.contactGeneration.ContactPoint;
import utils.Vector3fUtils;

public class ImpulseCalculator
{
	private static final float G = 6.67E-11f;
	private static final float R = 6.38E6f;
	private static final float M = 5.97E24f;
	
	public static final float GRAVITAIONAL_ACCELERATION = (G * M) / (R * R);
	
	private Entity a;
	private Entity b;;
	private ContactPoint contact;
	private float dt;
	
	private static Vector3f dirOfMotion = new Vector3f();
	
	private ImpulseCalculator(Collision collision, float dt)
	{
		this.a = collision.a;
		this.b = collision.b;
		this.contact = collision.contact;
		this.dt = dt;
	}
	
	public static void calculate(Collision collision, float dt)
	{
		ImpulseCalculator impc = new ImpulseCalculator(collision, dt);
		impc.resultantForce();
	}
	
	private void resultantForce()
	{
		if (a.hasComponent(Moveable.class) && b.hasComponent(Moveable.class))
		{
			generateResultantForce(0);
			generateTorque(a);
			generateResultantForce(1);
			generateTorque(b);

		}
		else if (a.hasComponent(Moveable.class))
		{
			generateResultantForce(0);
			generateTorque(a);
			generateSlidingForce(0);

		}
		else if (b.hasComponent(Moveable.class))
		{
			generateResultantForce(1);
			generateTorque(b);
			generateSlidingForce(1);
		}
	}
	
	
	private void generateResultantForce(int entity)
	{
		Entity a;
		Entity b;
		if (entity == 0)
		{
			a = this.a;
			b = this.b;
		}
		else
		{
			a = this.b;
			b = this.a;
		}
		Moveable mov = a.getComponent(Moveable.class);
		Weight weightA = a.getComponent(Weight.class);
		Weight weightB = b.getComponent(Weight.class);
		if (Math.abs(mov.momentum.length()) < 1E-2f)
		{
			mov.momentum.set(0);
		}
		dirOfMotion.set(mov.momentum).normalize();
		if (mov.momentum.equals(new Vector3f()))
		{
			dirOfMotion.set(0, -1, 0);
		}
		if (dirOfMotion.dot(contact.worldNormal) > 0)
		{
			contact.worldNormal.negate();
		}
		else if (dirOfMotion.equals(new Vector3f(0, 1, 0)) || dirOfMotion.equals(new Vector3f(0, -1, 0)) && (Math.abs(dirOfMotion.dot(contact.worldNormal)) < 0.55f))
		{
			contact.worldNormal.set(dirOfMotion).negate();
		}
		Vector3f axis = new Vector3f(dirOfMotion).cross(contact.worldNormal);
		float angle = new Vector3f(dirOfMotion).angle(contact.worldNormal);
		if (angle >= Math.PI / 2)
		{
			angle = 0;
		}
		Vector3f resultant = new Vector3f(contact.worldNormal.x * mov.momentum.x, contact.worldNormal.y * mov.momentum.y, contact.worldNormal.z * mov.momentum.z).negate()
				.add(new Vector3f(contact.worldNormal).normalize().rotateAxis(-angle, axis.x, axis.y, axis.z)
						.mul(mov.momentum.length()).mul((weightA.restitution + weightB.restitution) / 2)).div(dt);
		if (resultant.equals(new Vector3f(Float.NaN)))
		{
			resultant.set(0);
		}
		if(resultant.dot(dirOfMotion) > 0)
		{
			resultant.negate();
		}
		mov.force.set(resultant);
	}
	
	private void generateTorque(Entity a)
	{
		Moveable mov = a.getComponent(Moveable.class);
		State state = a.getComponent(State.class);
		Weight weight = a.getComponent(Weight.class);
		Vector3f torqueDir = new Vector3f(contact.worldNormal).cross(dirOfMotion);
		Vector3f entityOrientation = new Vector3f(0, 1, 0)
				.rotateX((float) Math.toRadians(state.rotation.x ))
				.rotateY((float) Math.toRadians(state.rotation.y))
				.rotateZ((float) Math.toRadians(state.rotation.z));
		Vector3f torque = new Vector3f();
		if ((torqueDir.x != 0 || torqueDir.y != 0 || torqueDir.z != 0) && contact.worldNormal.dot(entityOrientation) < 0.9985f && contact.worldNormal.dot(entityOrientation) > -0.9985f)
		{
			torque.set(torqueDir.normalize().mul(9.81f * weight.mass));
		}
		mov.torque.set(torque);
		if (a instanceof MoveableCollidableBall)
		{
			mov.torque.mul(5);
		}
	}
	
	private void generateSlidingForce(int entity)
	{
		Entity a;
		Entity b;
		if (entity == 0)
		{
			a = this.a;
			b = this.b;
		}
		else
		{
			a = this.b;
			b = this.a;
		}
		if (!b.hasComponent(Moveable.class) && contact.worldNormal.angle(new Vector3f(0, 1, 0)) > 0.1f)
		{
			State state = a.getComponent(State.class);
			Weight weightA = a.getComponent(Weight.class);
			Weight weightB = b.getComponent(Weight.class);
			Vector3f resultant = new Vector3f(contact.worldNormal).normalize().mul(weightA.mass * 9.81f).div((float) (weightA.friction * Math.sin(Math.toRadians(state.rotation.length()))  + Math.cos(Math.toRadians(state.rotation.length()))));
			Vector3f rotationAxis = new Vector3f(state.rotation).normalize();			
			Vector3f frictionDir = new Vector3f(resultant).cross(rotationAxis).normalize();
			Vector3f friction = new Vector3f(frictionDir).mul(resultant.length() * (weightA.friction + weightB.friction) / 2);
			Vector3f gravComponent = new Vector3f(frictionDir).normalize().negate().mul((float) (weightA.mass * 9.81f * Math.sin(Math.toRadians(state.rotation.length()))));
			if (!frictionDir.equals(new Vector3f(Float.NaN)))
			{
				if (gravComponent.length() > friction.length())
				{
					state.position.add(new Vector3f(friction).add(gravComponent).mul(dt).mul(weightA.inverseMass));
				}
				if (a instanceof MoveableCollidableBall && weightA.friction != 0 && weightB.friction != 0)
				{
					Vector3f torque = new Vector3f(new Vector3f(rotationAxis).mul(friction.length()));
					state.rotation.add(Vector3fUtils.toDegrees(new Vector3f(torque).mul(weightA.inverseInertia * dt * 0.1f)));
				}
			}
		}
	}

	public Entity getEntityA()
	{
		return a;
	}
	
	public Entity getEntityB()
	{
		return b;
	}
}