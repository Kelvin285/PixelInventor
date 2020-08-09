#version 330

out vec4 fragColor;
out vec4 fragDepth;
in vec2 outTexCoord;

uniform sampler2D texture_sampler;
uniform sampler2D depth_texture;
uniform sampler2D blur_texture;
uniform vec4 color;

uniform int post_processing;
uniform int raycasting;

uniform float exposure;

float blurCoords[5];
void main()
{
	blurCoords[0] = 1.1;
	blurCoords[1] = 1.2;
	blurCoords[2] = 1.3;
	blurCoords[3] = 1.2;
	blurCoords[4] = 1.1;
	if (raycasting == 1) {
		
		
	} else {
		if (post_processing == 0) {
			fragColor = color * texture(texture_sampler, outTexCoord);
		}
		else {
			float depth = texture(depth_texture, outTexCoord).z;
		
			fragColor = color * texture(texture_sampler, outTexCoord);
		
			float texelSize = 0.002f;
			vec4 addColor = vec4(0.0);
			int ii = 0;
			float blur = 5;
			for (float x = 0; x < blur; x+=1) {
				float X = x - (blur / 2);
				vec4 t = color * texture(texture_sampler, outTexCoord + vec2(X * texelSize, 0));
				addColor += t * blurCoords[int(x)];
				ii++;
			}
			
			if (ii > 0)
			addColor /= ii;
			fragColor = addColor;
			
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
			gl_FragDepth = depth;
			fragDepth = vec4(vec3(depth), 1.0);
		}
	}
	
	if (fragColor.a == 0) discard;
}