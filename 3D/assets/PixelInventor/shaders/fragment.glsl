#version 330

out vec4 fragColor;
in vec2 outTexCoord;

uniform sampler2D texture_sampler;

void main()
{
	fragColor = texture(texture_sampler, outTexCoord);
	if (fragColor.a == 0) discard;
}