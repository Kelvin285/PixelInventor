#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;
layout (location=3) in vec3 color;

out vec2 outTexCoord;
out vec3 mvVertexPos;
out vec3 vertexPos;
out vec3 vertexColor;

out float lValue;

out vec4 mlightviewVertexPos;
out mat4 outModelViewMatrix;
out mat4 shadowViewMatrix;
out mat4 secondShadowViewMatrix;
out mat4 thirdShadowViewMatrix;
out mat4 fourthShadowViewMatrix;

out vec4 secondLightVertexPos;
out vec4 thirdLightVertexPos;
out vec4 fourthLightVertexPos;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;

uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;
uniform mat4 secondOrthoMatrix;
uniform mat4 thirdOrthoMatrix;
uniform mat4 fourthOrthoMatrix;

uniform float loadValue;

out vec3 vertexNormal;

void main()
{
	vertexNormal = normal;
	vec3 pos = position;
	lValue = loadValue;
	gl_Position = projectionMatrix * modelMatrix * vec4(pos, 1.0);
	mvVertexPos = gl_Position.xyz;
	outTexCoord = texCoord;
	
	mlightviewVertexPos = orthoProjectionMatrix * modelLightViewMatrix * vec4(pos, 1.0);
	secondLightVertexPos = secondOrthoMatrix * modelLightViewMatrix * vec4(pos, 1.0);
	thirdLightVertexPos = thirdOrthoMatrix * modelLightViewMatrix * vec4(pos, 1.0);
	fourthLightVertexPos = fourthOrthoMatrix * modelLightViewMatrix * vec4(pos, 1.0);
	
    outModelViewMatrix = modelMatrix;
    shadowViewMatrix = orthoProjectionMatrix * modelLightViewMatrix;
    secondShadowViewMatrix = secondOrthoMatrix * modelLightViewMatrix;
    thirdShadowViewMatrix = thirdOrthoMatrix * modelLightViewMatrix;
    fourthShadowViewMatrix = fourthOrthoMatrix * modelLightViewMatrix;
  
    vertexPos = pos;
    vertexColor = color;
}