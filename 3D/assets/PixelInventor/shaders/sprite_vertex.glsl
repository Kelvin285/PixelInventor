#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

out vec2 outTexCoord;

uniform vec2 offset;
uniform float rotation;
uniform vec2 scale;

void main()
{
	float rot = rotation;
	
	float mul = 1.0f / 1920.0f;
	float mul2 = 1.0f / 1080.0f;
	vec2 pos = vec2(position.x, position.y);
	
	pos.x -= 1;
	pos.y -= 1;
	
	float rad = (3.14 / 180.0);
	
	
	
	if (position.x == 1 && position.y == 0) {
		pos.x = cos((rotation - 45) * rad);
		pos.y = sin((rotation - 45) * rad);
	}
	
	if (position.x == 1 && position.y == 1) {
		pos.x = cos((rotation + 90 - 45) * rad);
		pos.y = sin((rotation + 90 - 45) * rad);
	}
	
	if (position.x == 0 && position.y == 1) {
		pos.x = cos((rotation + 90 * 2 - 45) * rad);
		pos.y = sin((rotation + 90 * 2 - 45) * rad);
	}
	
	if (position.x == 0 && position.y == 0) {
		pos.x = cos((rotation + 90 * 3 - 45) * rad);
		pos.y = sin((rotation + 90 * 3 - 45) * rad);
	}
	
	
	pos.x += 1;
	pos.y += 1;
	
	pos = pos * scale;
	pos = pos + vec2(offset.x * 2, offset.y * 2);
	pos.x = pos.x * mul;
	pos.y = pos.y * mul2;
	
	
	
	
	
    gl_Position = vec4(pos.xy - vec2(1, 1), 0, 1.0);
    outTexCoord = texCoord;
}