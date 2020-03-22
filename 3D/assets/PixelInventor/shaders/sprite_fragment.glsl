#version 330

out vec4 fragColor;
in vec2 outTexCoord;

uniform sampler2D texture_sampler;
uniform vec4 color;

uniform int post_processing;

void main()
{
	
	if (post_processing == 0) {
		fragColor = color * texture(texture_sampler, outTexCoord);
	}
	else {
		fragColor = color * texture(texture_sampler, outTexCoord);
	}
	if (fragColor.a == 0) discard;
}