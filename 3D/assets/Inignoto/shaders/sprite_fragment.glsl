#version 330

out vec4 fragColor;
in vec2 outTexCoord;

uniform sampler2D texture_sampler;
uniform sampler2D depth_texture;
uniform vec4 color;

uniform int post_processing;
uniform int raycasting;

uniform float exposure;

void main()
{
	if (raycasting == 1) {
		
		
	} else {
		if (post_processing == 0) {
			fragColor = color * texture(texture_sampler, outTexCoord);
		}
		else {
			float depth = texture(texture_sampler, outTexCoord).z;
		
			fragColor = color * texture(texture_sampler, outTexCoord);
		
			float texelSize = 0.002f;
			float mul = 0.5;
			vec4 addColor = vec4(0.0);
			float negate = 1.5f;
			int blursize = 0;
			int ii = 0;
			for (int x = -blursize; x < blursize + 1; x++) {
				for (int y = -blursize; y < blursize + 1; y++) {
					vec4 t = color * texture(texture_sampler, outTexCoord + vec2(x * texelSize, y * texelSize));
					addColor += t * (t - fragColor) * negate + t * mul;
					ii++;
				}
			}
			if (ii > 0)
			addColor /= ii;
			fragColor += addColor;
			
			bool outline = false;
			
			if (outline == true) {
				float offs = 0.001;
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
		}
	}
	
	if (fragColor.a == 0) discard;
}