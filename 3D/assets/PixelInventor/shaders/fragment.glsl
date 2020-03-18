#version 330

struct Fog
{
    int activeFog;
    vec3 color;
    float density;
};

out vec4 fragColor;
in vec2 outTexCoord;
in vec3 mvVertexPos;

in vec4 mlightviewVertexPos;
in mat4 outModelViewMatrix;
in vec4 secondLightVertexPos;

uniform Fog fog;
uniform Fog shadowBlendFog;

uniform sampler2D texture_sampler;
uniform sampler2D shadowMap;
uniform sampler2D secondShadowMap;

vec4 calcFog(vec3 pos, vec4 color, Fog fog)
{
    float distance = length(pos);
    float fogFactor = 1.0 / exp( (distance * fog.density)* (distance * fog.density));
    fogFactor = clamp( fogFactor, 0.0, 1.0 );

    vec3 resultColor = mix(fog.color, color.xyz, fogFactor);
    return vec4(resultColor.xyz, color.w);
}

vec2 poissonDisk[16] = vec2[]( 
   vec2( -0.94201624, -0.39906216 ), 
   vec2( 0.94558609, -0.76890725 ), 
   vec2( -0.094184101, -0.92938870 ), 
   vec2( 0.34495938, 0.29387760 ), 
   vec2( -0.91588581, 0.45771432 ), 
   vec2( -0.81544232, -0.87912464 ), 
   vec2( -0.38277543, 0.27676845 ), 
   vec2( 0.97484398, 0.75648379 ), 
   vec2( 0.44323325, -0.97511554 ), 
   vec2( 0.53742981, -0.47373420 ), 
   vec2( -0.26496911, -0.41893023 ), 
   vec2( 0.79197514, 0.19090188 ), 
   vec2( -0.24188840, 0.99706507 ), 
   vec2( -0.81409955, 0.91437590 ), 
   vec2( 0.19984126, 0.78641367 ), 
   vec2( 0.14383161, -0.14100790 ) 
);

float calcShadow(vec4 position)
{
    float shadowFactor = 0.0f;
    vec3 projCoords = position.xyz;

    projCoords = projCoords * 0.5 + 0.5;
    // Current fragment is not in shade
    float bias = 0.005;
    
    int mul = 1;
    vec2 texelSize = 1.0f / textureSize(shadowMap, 0) * float(mul);
	
	float currentDepth = texture(shadowMap, projCoords.xy).z;
	
	for (int x = -mul; x < mul; ++x) {
		for (int y = -mul; y < mul; ++y) {
			 float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
        	// shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
        	if (pcfDepth < projCoords.z - bias) {
        		shadowFactor += 0.05f;
        	}    
		}
	}
	
	
    return 1 - shadowFactor;
}

float calcShadow2(vec4 position)
{
    float shadowFactor = 0.0f;
    vec3 projCoords = position.xyz;

    projCoords = projCoords * 0.5 + 0.5;
    // Current fragment is not in shade
    float bias = 0.005;
    
    int mul = 1;
    vec2 texelSize = 1.0f / textureSize(secondShadowMap, 0) * float(mul);
	
	float currentDepth = texture(secondShadowMap, projCoords.xy).z;
	
	for (int x = -mul; x < mul; ++x) {
		for (int y = -mul; y < mul; ++y) {
			 float pcfDepth = texture(secondShadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
        	// shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
        	if (pcfDepth < projCoords.z - bias) {
        		shadowFactor += 0.05f;
        	}    
		}
	}
	
	
    return 1 - shadowFactor;
}

void main()
{
	fragColor = texture(texture_sampler, outTexCoord);
	
	if (fragColor.a == 0) discard;
	
	if ( fog.activeFog == 1 ) 
	{
    	fragColor = calcFog(mvVertexPos, fragColor, fog);
	}
	
	vec4 blendFog = calcFog(mvVertexPos, vec4(0, 0, 0, 0), shadowBlendFog);
	
	float shadow = calcShadow(mlightviewVertexPos) * (1.0 - blendFog.x);
	float shadow2 = calcShadow2(secondLightVertexPos) * blendFog.x;
	fragColor = vec4(clamp(fragColor.xyz * (shadow + shadow2), 0, 1), fragColor.w);
}