#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

out vec2 outTexCoord;
out vec3 mvVertexPos;

out vec4 mlightviewVertexPos;
out mat4 outModelViewMatrix;

out vec4 secondLightVertexPos;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;

uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;
uniform mat4 secondOrthoMatrix;

void main()
{
	gl_Position = projectionMatrix * modelMatrix * vec4(position, 1.0);
	mvVertexPos = gl_Position.xyz;
	outTexCoord = texCoord;
	
	mlightviewVertexPos = orthoProjectionMatrix * modelLightViewMatrix * vec4(position, 1.0);
	secondLightVertexPos = secondOrthoMatrix * modelLightViewMatrix * vec4(position, 1.0);
    outModelViewMatrix = modelMatrix;
}