package uk.ac.bham.cs.domct.physicsengine.component;

import uk.ac.bham.cs.domct.physicsengine.systems.physics.collision.detection.broadphase.BoundingBox;
import uk.ac.bham.cs.domct.physicsengine.systems.physics.collision.detection.narrowphase.ConvexHull;

/**
 * Component that holds an entity's bounding box for collision detection,
 * as well as the coefficients of restitution and friction for collision
 * response.
 * 
 * @author Dominic Cogan-Tucker
 *
 */
public class Collidable extends Component
{
	/**
	 * The entity's bounding box.
	 */
	public BoundingBox bBox;
	
	/**
	 * The entity's convex hull.
	 */
	public ConvexHull hull;
	
	/**
	 * The coefficient of restitution for the entity. 
	 */
	public float restitution = 1f;
	
	/**
	 * The coefficient of friction for the entity.
	 */
	public float friction = 0f;
}
