#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;

out vec2 outTexCoord;

uniform vec2 offset;
uniform vec2 scale;

uniform float zv;

void main()
{
	vec2 offs = offset;
	
	float mul = 1.0f / 1920.0f;
	float mul2 = 1.0f / 1080.0f;
	float rad = 3.14f / 180.0f;
	
	vec2 pos = vec2(position.x, position.y);
	
	pos *= scale * 2;
	
	
	pos.x *= mul;
	pos.y *= mul2;
	offs.x *= mul;
	offs.y *= mul2;
	pos.x += offs.x * 2;
	pos.y += offs.y * 2;
	
	
    gl_Position = vec4(pos.xy - vec2(1, 1), zv, 1.0);
    outTexCoord = texCoord;
}