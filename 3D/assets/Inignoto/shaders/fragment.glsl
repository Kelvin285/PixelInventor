#version 330

out vec4 fragColor;
out vec4 depthColor;

struct Fog
{
    int activeFog;
    vec3 color;
    float density;
};

in vec2 outTexCoord;
in vec3 mvVertexPos;
in vec3 vertexPos;
in vec3 vertexColor;

in vec4 mlightviewVertexPos;
in mat4 outModelViewMatrix;
in vec4 secondLightVertexPos;
in vec4 thirdLightVertexPos;
in vec4 fourthLightVertexPos;

in mat4 shadowViewMatrix;
in mat4 secondShadowViewMatrix;
in mat4 thirdShadowViewMatrix;
in mat4 fourthShadowViewMatrix;

in float lValue;
in vec3 vertexNormal;

uniform Fog fog;
uniform Fog shadowBlendFog;

uniform sampler2D texture_sampler;
uniform sampler2D shadowMap;
uniform sampler2D shadowMap2;
uniform sampler2D shadowMap3;
uniform sampler2D shadowMap4;

uniform vec3 cameraPos;
uniform vec3 sunPos;
uniform vec3 sunDirection;
uniform vec3 sunColor;

uniform int noColor;

uniform int hasShadows;
uniform int cascadedShadows;

uniform int renderToDepth;

uniform int voxelRender;

const int len = 4;
vec2 poissonDisk[len];

float getShadowFactor(vec3 position, sampler2D sampler) {
		
    float shadowFactor = 1.0f;
    vec3 projCoords = position.xyz;

    projCoords = projCoords * 0.5 + 0.5;
    
    
    // Current fragment is not in shade
    float bias = 0.05;
    
    float mul = 0.5f;
    vec2 texelSize = (1.0f / textureSize(sampler, 1)) * mul;
	
	
	
	for (int i = 0; i < len; i++) {
		float depth = texture(sampler, projCoords.xy + poissonDisk[i] * texelSize).z;
		if (depth < projCoords.z - bias) {
			shadowFactor -= 0.1 * (4.0 / len);
		} else {
			shadowFactor += 0.1 * (4.0 / len);
		}
	}
	
	return shadowFactor;
}

float calcShadow(vec3 spos1, vec3 spos2, vec3 spos3, vec3 spos4)
{
	float sf = 1.0;
	
	if (mvVertexPos.z < 5) {
		sf = getShadowFactor(spos1, shadowMap);
	} else {
		if (mvVertexPos.z < 10) {
			sf = getShadowFactor(spos2, shadowMap2);
		}
		else 
		{
			if (mvVertexPos.z < 20) {
				sf = getShadowFactor(spos3, shadowMap3);
			}
			else
				sf = getShadowFactor(spos4, shadowMap4);
		}
	}
    return sf;
}

void main()
{
	float deg = 360 / len;
	float rad = 180 / 3.14;
	
	for (int i = 0; i < len; i++) {
		float angle = i * rad * deg;
		poissonDisk[i] = vec2(cos(angle), sin(angle));
	}
	
	fragColor = mix(texture(texture_sampler, outTexCoord), vec4(1.0, 1.0, 1.0, 0.0), lValue);
	
	if (fragColor.a == 0) discard;
	
	gl_FragDepth = gl_FragCoord.z;
	depthColor = vec4(vec3(pow(gl_FragCoord.z, 1000)), 1.0);
	
	fragColor = vec4(clamp(fragColor.xyz * 1.1f, 0, 1), fragColor.w) * vec4(vertexColor, 1.0f);
	
	//float shadow = calcShadow(mlightviewVertexPos.xyz, secondLightVertexPos.xyz, thirdLightVertexPos.xyz, fourthLightVertexPos.xyz);
	//fragColor = vec4(clamp(fragColor.xyz * shadow, 0, 1), fragColor.w);
}