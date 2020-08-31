package ecs.system.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecs.component.Material;
import ecs.component.Mesh;
import ecs.component.State;
import ecs.component.View;
import ecs.entity.Camera;
import ecs.entity.Entity;
import ecs.system.EngineSystem;
import ecs.system.rendering.openGLObjects.Vao;
import ecs.system.rendering.shaders.EntityShader;
import utils.MatrixUtils;

/**
 * System that performs the rendering of all entities each frame.
 * 
 * @author Dominic Cogan-Tucker
 *
 */
public final class RenderSystem extends EngineSystem
{
	/**
	 * The camera for the scene.
	 */
	
	private Camera camera;
	/**
	 * The state of the camera.
	 */
	
	private State cameraState;
	/**
	 * The entity shader program.
	 */
	private EntityShader shader;
	
	/**
	 * The material system.
	 */
	private MaterialSystem materialSystem = new MaterialSystem();
	
	/**
	 * The mesh system.
	 */
	private MeshSystem meshSystem = new MeshSystem();
	
	/**
	 * The map of all entities to render.
	 */
	protected static Map<Mesh, Map<Material, List<Entity>>> entities = new HashMap<>();
	
	/**
	 * Constructs a render system with the given camera view and shader program. 
	 * 
	 * @param cam The camera to view the scene from.
	 * @param s The shader program to use.
	 */
	public RenderSystem(Camera cam, EntityShader s)
	{
		camera = cam;
		cameraState = camera.getComponent(State.class);
		shader = s;
	}
	
	/**
	 * Initialise the material and mesh system and bind the shader.
	 */
	@Override
	public void initialise()
	{
		materialSystem.initialise();;
		meshSystem.initialise();;
		shader.bind();
	}
	
	/**
	 * Renders every mesh, to be called each frame.
	 */
	@Override
	public void update()
	{
		renderMeshes();
	}
	
	/**
	 * Closes the rendering system, deleting all material and mesh
	 * data.
	 */
	@Override
	public void close()
	{
		materialSystem.close();
		meshSystem.close();
	}
	
	/**
	 * Renders all meshes.
	 * 
	 * @param cam The camera view of the scene.
	 * @param s The shader program to use.
	 */
	private void renderMeshes()
	{
		entities.forEach((mesh, entities) -> 
			{
				Vao vao = mesh.vao;
				vao.bind();
				glEnableVertexAttribArray(0);
				entities.forEach((material, list) -> 
					{
						if (material != null)
						{
							bindMaterial(material);
						}
						list.forEach(entity -> 
							{
								drawEntity(entity, mesh);
							});
						if (material != null)
						{
							glDisableVertexAttribArray(1);
						}
					});
				glDisableVertexAttribArray(0);
			});
	}
	
	/**
	 * Binds the given material to the active texture slot.
	 * 
	 * @param material The material to bind.
	 */
	private static void bindMaterial(Material material)
	{
		glEnableVertexAttribArray(1);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, material.textureID);
	}
	
	/**
	 * Draws the mesh at the entity's position.
	 * 
	 * @param entity The entity.
	 * @param mesh The mesh to draw.
	 */
	private void drawEntity(Entity entity, Mesh mesh)
	{
		State entityState = entity.getComponent(State.class);
		shader.setModelMatrix(MatrixUtils.transformMatrix(entityState.position, 
				entityState.rotation, entityState.scale));
		shader.setViewMatrix(MatrixUtils.viewMatrix(cameraState.position, cameraState.rotation));
		shader.setProjectionMatrix(((View) camera.getComponent(View.class)).window.getProjectionMatrix());
		glDrawElements(GL_TRIANGLES, mesh.indices.length, GL_UNSIGNED_INT, 0);
	}
}