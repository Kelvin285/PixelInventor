#version 330

out vec4 fragColor;

struct Fog
{
    int activeFog;
    vec3 color;
    float density;
};

in vec2 outTexCoord;
in vec3 mvVertexPos;
in vec3 vertexPos;

in vec4 mlightviewVertexPos;
in mat4 outModelViewMatrix;
in vec4 secondLightVertexPos;

in mat4 shadowViewMatrix;
in mat4 secondShadowViewMatrix;

in float lValue;

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

uniform int renderToDepth;

uniform int voxelRender;



void main()
{
	fragColor = mix(texture(texture_sampler, outTexCoord), vec4(1.0, 1.0, 1.0, 0.0), lValue);
	
	if (fragColor.a == 0) discard;
	
	gl_FragDepth = gl_FragCoord.z;
}