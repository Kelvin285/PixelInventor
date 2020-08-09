#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;

uniform mat4 projMatrix1;
uniform mat4 projMatrix2;
uniform mat4 projMatrix3;
uniform mat4 projMatrix4;

uniform mat4 mvMatrix;
uniform float loadValue;
uniform vec3 cameraPos;

uniform int cascade;
uniform float cMul;

out vec4 out_pos;

out vec2 outTexCoord;

void main()
{ 
	outTexCoord = texCoord;
	mat4 projMatrix = projMatrix1;
	if (cascade == 1) projMatrix = projMatrix2;
	if (cascade == 2) projMatrix = projMatrix3;
	if (cascade == 3) projMatrix = projMatrix4;
	//projection * view * model
	vec3 pos = position - vec3(0, 10, 0) * loadValue;
	pos.z += 0.15f;
	vec3 ray = vec4(mvMatrix * vec4(pos, 1.0)).xyz - cameraPos;
	ray /= length(ray);
	pos.x += ray.x * 0.1f * cMul;
	
	
	gl_Position = projMatrix * mvMatrix * vec4(pos, 1.0);
	
	out_pos = gl_Position;
}