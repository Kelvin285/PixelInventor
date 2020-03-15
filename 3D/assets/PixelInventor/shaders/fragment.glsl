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

uniform Fog fog;

uniform sampler2D texture_sampler;

vec4 calcFog(vec3 pos, vec4 color, Fog fog)
{
    float distance = length(pos);
    float fogFactor = 1.0 / exp( (distance * fog.density)* (distance * fog.density));
    fogFactor = clamp( fogFactor, 0.0, 1.0 );

    vec3 resultColor = mix(fog.color, color.xyz, fogFactor);
    return vec4(resultColor.xyz, color.w);
}

void main()
{
	fragColor = texture(texture_sampler, outTexCoord);
	
	if (fragColor.a == 0) discard;
	
	if ( fog.activeFog == 1 ) 
	{
    	fragColor = calcFog(mvVertexPos, fragColor, fog);
	}
}