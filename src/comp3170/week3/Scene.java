package comp3170.week3;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import static comp3170.Math.TAU;

public class Scene {

	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";
	
	
	private float offx = 0.0f;
	private float offy = 0.0f;
	private float MOVE_SPEED = 10f;
	private float rotation = 0.0f;
	private float ROTATION_RATE = TAU/3;
	private float scalex = 1.0f;
	private float scaley = 1.0f;
	

	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	private Vector3f[] colours;
	private int colourBuffer;

	private Shader shader;
	
	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f transMatrix = new Matrix4f();
	private Matrix4f rotMatrix = new Matrix4f();
	private Matrix4f scalMatrix = new Matrix4f();

	public Scene() {

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// @formatter:off
		colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
			};
			// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);
	
		//examples
		//Vector2f offset = new Vector2f(0.25f, 0.0f);
		//float rotation = -TAU/4;
		//Vector2f scale = new Vector2f(1.0f, 0.5f);
		
		// a)
		//scalex = -1.0f;
		//scaley = 1.0f;
		
		// b)
		//rotation = -TAU/4;
		
		// c)
		//offx = 0.5f;
		//offy = -0.5f;
		//scalex = 0.5f;
		//scaley = 0.5f;
		
		// d)
		//offx = -0.65f;
		//offy = 0.65f;
		//rotation = TAU/8;
		//scalex = 0.5f;
		//scaley = 0.5f;
		
		// animation
		offy = 0.5f;
		rotation = TAU/4;
		scalex = 0.1f;
		scaley = 0.1f;
		
		
		Vector2f offset = new Vector2f(offx, offy);
		Vector2f scale = new Vector2f(scalex, scaley);
		
		translationMatrix(offset.x, offset.y, transMatrix);
		rotationMatrix(rotation, rotMatrix);
		scaleMatrix(scale.x, scale.y, scalMatrix);
		modelMatrix.mul(transMatrix).mul(rotMatrix).mul(scalMatrix);
	}
	
	public void update(float deltaTime) {
		float moving = MOVE_SPEED * deltaTime; 
		float rotating = ROTATION_RATE * deltaTime; 
		rotationMatrix(rotating, rotMatrix);
		//modelMatrix.mul(transMatrix).mul(rotMatrix);
		//modelMatrix.mul(rotMatrix);
		modelMatrix.translate(0.0f, moving, 0.0f).rotateZ(rotating);
	}

	public void draw() {
		
		shader.enable();
		// set the attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setUniform("u_modelMatrix", modelMatrix); //model matrix still needs to be declared
		shader.setAttribute("a_colour", colourBuffer);

		// draw using index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest) {
		// clear the matrix to the identity matrix
		dest.identity();

		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]

		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(tx);
		dest.m31(ty);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {
		// TODO: Your code here
		
		// clear the matrix to the identity matrix
		//dest.identity();
		
		//		  [ cos(a) -sin(a) 0 0 ]
		// R(a) = [ sin(a)  cos(a) 0 0 ]
		//		  [		0		0  0 0 ]
		//		  [		0		0  0 1 ]
		
		dest.m00((float) Math.cos(angle));
		dest.m01((float) Math.sin(angle));
		dest.m10((float) Math.sin(-angle));
		dest.m11((float) Math.cos(angle));

		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest) {
		// TODO: Your code here
		
		// clear the matrix to the identity matrix
		//dest.identity();
		
		//			   [ sx  0 0 0 ]
		// S(sx, sy) = [ 0  sy 0 0 ]
		//		 	   [ 0	 0 0 0 ]
		//		  	   [ 0	 0 0 1 ]
		
		dest.m00(sx);
		dest.m11(sy);

		return dest;
	}

}
