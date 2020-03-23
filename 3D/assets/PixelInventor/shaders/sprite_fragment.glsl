#version 330

out vec4 fragColor;
in vec2 outTexCoord;

uniform sampler2D texture_sampler;
uniform vec4 color;

uniform int post_processing;
uniform int raycasting;

uniform float exposure;

void main()
{
	if (raycasting == 1) {
		fragColor = color * texture(texture_sampler, outTexCoord);
		
		float texelSize = 0.002f;
		float mul = 0.5f;
		vec4 addColor = vec4(0.0);
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				addColor += color * texture(texture_sampler, outTexCoord + vec2(x * texelSize, y * texelSize)) * mul;
			}
		}
		addColor /= 9;
		fragColor += addColor;
		
		bool outline = false;
		
		if (outline == true) {
			float offs = 0.002;
			vec4 col = texture(texture_sampler, outTexCoord);
			vec4 offsCol = texture(texture_sampler, outTexCoord - vec2(offs));
			if (length(col - offsCol) > 0.1) {
				fragColor *= vec4(0.5, 0.5, 0.5, 1);
			}
		}
		
		float gamma = 1.0f;
		
		vec3 hdrColor = fragColor.rgb;
		vec3 mapped = vec3(1.0) - exp(-hdrColor * exposure);
		mapped = pow(mapped, vec3(1.0 / gamma));
		
		fragColor = mix(fragColor, vec4(mapped, 1.0), 0.5f);
		
	} else {
		if (post_processing == 0) {
			fragColor = color * texture(texture_sampler, outTexCoord);
		}
		else {
			fragColor = color * texture(texture_sampler, outTexCoord);
		}
	}
	
	if (fragColor.a == 0) discard;
}