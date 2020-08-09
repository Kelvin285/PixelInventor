#version 330

out vec4 fragColor;
in vec2 outTexCoord;

uniform sampler2D texture_sampler;
uniform sampler2D depth_texture;
uniform sampler2D blur_texture;
uniform vec4 color;

uniform int post_processing;
uniform int raycasting;

uniform float exposure;

uniform int distance_blur;

uniform vec3 fogColor;
uniform float fogDensity;

float blurCoords[5];

const int len = 5;
vec2 poissonDisk[len];

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
			float deg = 360 / len;
			float rad = 180 / 3.14;
			
			for (int i = 0; i < len; i++) {
				float angle = i * rad * deg;
				poissonDisk[i] = vec2(cos(angle), sin(angle));
			}
		
			float depth = texture(depth_texture, outTexCoord).z;
		
			fragColor = color * texture(texture_sampler, outTexCoord);
			if (distance_blur == 1) {
				float texelSize = 0.002f;
				vec4 addColor = vec4(0.0);
				int ii = 0;
				float blur = 5;
				for (float x = 0; x < blur; x+=1) {
					float X = x - (blur / 2);
					vec4 t = color * texture(blur_texture, outTexCoord + vec2(0, X * texelSize));
					addColor += t * blurCoords[int(x)];
					ii++;
				}
				
				if (ii > 0)
				addColor /= ii;
				
				
				fragColor = mix(fragColor, addColor, depth);
			}
			
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
			
			fragColor = mix(fragColor, vec4(fogColor, 1.0), min(depth * fogDensity + 0.5 * fogDensity, 1.0));
			
			vec3 depthPos = vec3(outTexCoord, depth);
			
			float occStep = 0.001f;
			float occlusion = 0;
			float ii = 0;
			float depth_bias = 0.01f;
			int size = 5;
			
			occlusion = 0;
			
			for (int j = 0; j < size; j++)
			for (int i = 0; i < len; i++) {
				float d = texture(depth_texture, depthPos.xy + poissonDisk[i] * occStep * j).z;
				if (d - depth_bias > depth) {
					occlusion += 1;
				}
				ii++;
			}
			
			
			occlusion /= ii;
			fragColor *= vec4(1.0 - vec3(occlusion), 1.0f);
		}
	}
	
	if (fragColor.a == 0) discard;
}