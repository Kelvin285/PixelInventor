#version 330

out vec4 fragColor;
out vec4 depthColor;

uniform sampler2D texture_sampler;
uniform float zAdd;
in vec4 out_pos;
in vec2 outTexCoord;

void main()
{
	if (texture(texture_sampler, outTexCoord).a <= 0) {
		discard;
	}
	fragColor = vec4(1.0);
	float depth = gl_FragCoord.z - 0.04f + zAdd;
	depthColor = vec4(vec3(depth), 1.0);
}