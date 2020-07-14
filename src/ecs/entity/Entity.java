package ecs.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import ecs.component.Component;

/**
 * An entity is an object with a unique id containing a map of all it's components.
 * @author Dominic Cogan-Tucker
 *
 */
public class Entity
{
	private UUID id;
	private HashMap<Class<? extends Component>, Component> components = new HashMap<>();

	/**
	 * Constructs and entity assigning a unique id to it.
	 */
	public Entity()
	{
		this.id = UUID.randomUUID();
	}

	/**
	 * Adds the given component to this entity's component map if a component of that class
	 * type did not already exist.
	 * 
	 * @param <T> The type of class, which extends component.
	 * @param component The component to add.
	 */
	public <T extends Component> void addComponent(T component)
	{
		if (components.get(component.getClass()) == null)
		{
			components.put(component.getClass(), component);
		}
		
		if (Component.componentMap.get(component.getClass()) == null)
		{
			HashMap<Entity, Component> ec = new HashMap<>();
			ec.put(this, component);
			Component.componentMap.put(component.getClass(), ec);
		}
		else if (Component.componentMap.get(component.getClass()).get(this) == null)
		{
			Component.componentMap.get(component.getClass()).put(this, component);
		}
	}

	/**
	 * Removes the component of the corresponding class from this entity's component map.
	 * 
	 * @param <T> Type of class.
	 * @param component The class of the component to remove.
	 */
	public <T extends Component> void removeComponent(Class<T> component)
	{
		components.remove(component);
		Component.componentMap.get(component).remove(this);
	}

	public boolean hasComponent(Class<? extends Component> component)
	{
		return components.get(component) != null ? true : false;
	}

	/**
	 * Gets the component of the corresponding class from this entity's component map.
	 * 
	 * @param <T> Type of class.
	 * @param component The class which extends component of the component to look for.
	 * @throws IllegalArgumentException when the entity does not have contain the
	 *                                  component of the given class.
	 * @return The component corresponding to the given class.
	 */
	public <T extends Component> Component getComponent(Class<T> component)
	{
		Component c = components.get(component);
		if (c == null)
		{
			throw new IllegalArgumentException(
					"Entity does not contain the " + component.getSimpleName() + " component.");
		}
		return c;
	}
	
	/**
	 * Removes all components from this entity.
	 * 
	 */
	public void removeAllComponents()
	{
		if (components != null)
		{
			for (Iterator<Entry<Class<? extends Component>, Component>> iter = components.entrySet().iterator(); iter.hasNext();)
			{
				iter.remove();
			}
		}	
	}

	/**
	 * Gets the UUID of this entity;
	 * 
	 * @return
	 */
	public UUID getID()
	{
		return id;
	}
}
