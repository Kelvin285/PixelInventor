#version 330

struct Fog
{
    int activeFog;
    vec3 color;
    float density;
};

vec4 fragColor;
in vec2 outTexCoord;
in vec3 mvVertexPos;
in vec3 vertexPos;

in vec4 mlightviewVertexPos;
in mat4 outModelViewMatrix;
in vec4 secondLightVertexPos;

in mat4 shadowViewMatrix;
in mat4 secondShadowViewMatrix;

uniform Fog fog;
uniform Fog shadowBlendFog;

uniform sampler2D texture_sampler;
uniform sampler2D shadowMap;
uniform sampler2D secondShadowMap;
uniform vec3 cameraPos;
uniform vec3 sunPos;
uniform vec3 sunDirection;
uniform vec3 sunColor;

uniform int hasShadows;
uniform int cascadedShadows;

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
    vec2 texelSize = 3.0f / textureSize(shadowMap, 0) * float(mul);
	
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
    vec2 texelSize = 3.0f / textureSize(secondShadowMap, 0) * float(mul);
	
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


float computeScattering(float lightDotView)
{
	float PI = 3.14;
	float G_SCATTERING = 0.1f;
	float result = 1.0f - G_SCATTERING * G_SCATTERING;
	result /= (4.0f * PI * pow(1.0f + G_SCATTERING * G_SCATTERING - (2.0f * G_SCATTERING) * lightDotView, 1.5f));
	return result;
}

vec3 accumFog = vec3(0, 0, 0);
vec3 accumFog2 = vec3(0, 0, 0);

float bias = 0.05f;


void volumetricLighting(vec4 position) {

	vec3 projCoords = position.xyz;

    projCoords = projCoords * 0.5 + 0.5;

	vec3 worldPos = vertexPos;
	vec3 startPosition = cameraPos;
	
	vec3 endRayPosition = startPosition - sunDirection * 15;
	
	vec3 rayVector = endRayPosition - startPosition;
	
	int NB_STEPS = 10;
	float rayLength = length(rayVector);
	vec3 rayDirection = rayVector / rayLength;
	
	float stepLength = rayLength / NB_STEPS;
	vec3 step = rayDirection * stepLength;
	
	vec3 currentPosition = startPosition;
	
	accumFog = vec3(0.0);
	
	for (int i = 0; i < NB_STEPS; i++) {
		vec4 worldInShadowCameraSpace = vec4(currentPosition, 1.0f) * shadowViewMatrix;
		worldInShadowCameraSpace /= worldInShadowCameraSpace.w;
		
		float shadowMapValue = texture(shadowMap, projCoords.xy).r;
		
		if (shadowMapValue > projCoords.z - bias) {
			accumFog += vec3(computeScattering(dot(rayDirection, sunDirection))) * sunColor;
		}
		currentPosition += step;
	}
}

void secondVolumetricLighting(vec4 position) {
	
	vec3 projCoords = position.xyz;

    projCoords = projCoords * 0.5 + 0.5;

	vec3 worldPos = vertexPos;
	vec3 startPosition = cameraPos;
	
	vec3 endRayPosition = startPosition - sunDirection * 15;
	
	vec3 rayVector = endRayPosition - startPosition;
	
	int NB_STEPS = 10;
	float rayLength = length(rayVector);
	vec3 rayDirection = rayVector / rayLength;
	
	float stepLength = rayLength / NB_STEPS;
	vec3 step = rayDirection * stepLength;
	
	vec3 currentPosition = startPosition;
	
	accumFog2 = vec3(0.0);
	
	for (int i = 0; i < NB_STEPS; i++) {
		vec4 worldInShadowCameraSpace = vec4(currentPosition, 1.0f) * secondShadowViewMatrix;
		worldInShadowCameraSpace /= worldInShadowCameraSpace.w;
		
		float shadowMapValue = texture(secondShadowMap, projCoords.xy).r;
		
		if (shadowMapValue > projCoords.z - bias) {
			accumFog2 += vec3(computeScattering(dot(rayDirection, sunDirection))) * sunColor;
		}
		currentPosition += step;
	}
	
}

void main()
{
	volumetricLighting(mlightviewVertexPos);
	secondVolumetricLighting(mlightviewVertexPos);
	accumFog = clamp(accumFog, 0.3, 1);
	accumFog2 = clamp(accumFog2, 0.5, 1);
	fragColor = texture(texture_sampler, outTexCoord);
	
	if (fragColor.a == 0) discard;
	
	if ( fog.activeFog == 1 ) 
	{
		if (hasShadows == 1) {
			vec4 blendFog = calcFog(mvVertexPos, vec4(0, 0, 0, 0), shadowBlendFog);
		
			vec3 shadow = accumFog * (1.0 - blendFog.x);
			vec3 shadow2 = accumFog2 * blendFog.x;
			if (cascadedShadows != 1) {
				shadow2 = sunColor * blendFog.x;
			}
			vec4 shadowColor = vec4(shadow + shadow2, 1.0);
		
			Fog f = fog;
			f.density = shadowColor.x * 100;
			f.color = shadowColor.xyz;
			vec4 fc = calcFog(mvVertexPos, fragColor, f);
			
			fragColor *= vec4(fc.xyz, 1.0);
		}
		else {
			vec4 fc = calcFog(mvVertexPos, fragColor, fog);
			fragColor = vec4(fc.xyz, 1.0);
		}
	}
	
	gl_FragColor = fragColor;
}