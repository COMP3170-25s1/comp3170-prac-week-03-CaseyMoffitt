#version 410

in vec4 a_position;	// vertex position as a homogenous vector in NDC 
uniform mat4 u_modelMatrix; // model Matrix
in vec3 a_colour; // vertex colour RGB

out vec3 v_colour; // to fragment shader

void main() {
	//pass the colour to the fragment shader
	v_colour = a_colour;

	// pad the vertex to a homogeneous 3D point
    //gl_Position = a_position;
    gl_Position = u_modelMatrix * a_position;

}

