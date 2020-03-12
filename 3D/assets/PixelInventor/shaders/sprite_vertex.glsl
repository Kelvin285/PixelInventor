#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

uniform mat4 modelMatrix;
out vec2 outTexCoord;

void main()
{
	gl_Position = modelMatrix * vec4(position, 1.0);
	outTexCoord = texCoord;
}