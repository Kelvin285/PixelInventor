#if OPENGL
#define SV_POSITION POSITION
#define VS_SHADERMODEL vs_5_0
#define PS_SHADERMODEL ps_5_0
#else
#define VS_SHADERMODEL vs_5_0
#define PS_SHADERMODEL ps_5_0
#endif

float4x4 World;
float4x4 View;
float4x4 Projection;
float fog_distance;
float4 fog_color;
bool water;
float time;
bool transparent;

float3 camera_pos;

float4 color;

float radius;
float area;

bool world_render;


texture ModelTexture;
sampler2D textureSampler = sampler_state {
    
    Texture = (ModelTexture);
    MagFilter = Point;
    MinFilter = Point;
    AddressU = Clamp;
    AddressV = Clamp;
};

struct VertexPositionColorTexture
{
    float4 Position : POSITION0;
    float4 Color : COLOR0;
	float2 TextureCoordinate : TEXCOORD0;
};

struct VertexShaderOutput
{
    float4 Position : POSITION0;
    float4 Color : COLOR0;
	float2 TextureCoordinate : TEXCOORD0;
	float4 PixelPos : COLOR1;
    float4 WorldPos : TEXCOORD1;
    float4 Light : TEXCOORD2;
};

float rand(in float2 uv)
{
    float2 noise = (frac(sin(dot(uv, float2(12.9898, 78.233) * 2.0)) * 43758.5453));
    return abs(noise.x + noise.y) * 0.5;
}

VertexShaderOutput VertexShaderFunction(VertexPositionColorTexture input)
{
    
    VertexShaderOutput output;
    float4 worldPosition = mul(input.Position, World);
    
    output.Light = input.Color;

    int CAM_CX = (int)(camera_pos.x) / 16u - 4;
    int CAM_CY = (int)(camera_pos.y) / 16u - 2;
    int CAM_CZ = (int)(camera_pos.z) / 16u - 4;

    int CX = (int)(worldPosition.x) / 16u - CAM_CX;
    int CY = (int)(worldPosition.y) / 16u - CAM_CY;
    int CZ = (int)(worldPosition.z) / 16u - CAM_CZ;
    
    if (world_render == true) {
        float r = radius;
        if (radius <= 0) r = 900000;
        float diameter = r * 2;
        float length = diameter * 4;

        float lat = 3.14 * (worldPosition.x - camera_pos.x) / length;

        lat += 3.14 / 2.0 + 3.14;

        worldPosition.y = worldPosition.y - length * (sin(lat) + 1);

        float lon = 3.14 * (worldPosition.z - camera_pos.z) / (length / 2);

        lon += 3.14 / 2.0 + 3.14;

        worldPosition.y += worldPosition.y - (length / 2) * (sin(lon) + 1);
        worldPosition.y /= 2.0;
    }
    
    output.Color = float4(1, 1, 1, 1);
    output.WorldPos = worldPosition;

    float4 viewPosition = mul(worldPosition, View);

    if (water) {

        float val = abs(sin(rand(float2(worldPosition.x, worldPosition.z)) + worldPosition.x + time));

        worldPosition.y -= 0.1f * val;

        float val2 = (1.0f - val) * 0.5f;
        float water_fog = (viewPosition.y - viewPosition.z) / 35.0f;

        output.Color += float4(val2 + water_fog, val2 + water_fog, val2 + water_fog, val2);
    }

    viewPosition = mul(worldPosition, View);
    output.Position = mul(viewPosition, Projection);


    output.TextureCoordinate = input.TextureCoordinate;

    output.PixelPos = output.Position;

    
    return output;
}

float GetFog(VertexShaderOutput input) {
    float depth = 0;
    float diameter = radius * 2;

    float dist1 = distance(input.WorldPos.xz, float2(radius, radius + diameter));
    float dist2 = distance(input.WorldPos.xz, float2(radius + diameter, radius + diameter));
    float dist3 = abs(input.WorldPos.z - radius);


    float fade = 25;
    float fade_add = 5;
    float max_fade = fade + fade_add;

    bool in_bounds = false;

    if (dist1 <= radius + 2 && area == 2) {
        if (radius - dist1 < max_fade) {
            depth = 1.0f - (radius - dist1 - fade_add) / fade;
        }
        in_bounds = true;
    }

    if (dist2 <= radius + 2 && area == 1) {
        if (radius - dist2 < max_fade) {
            depth = 1.0f - (radius - dist2 - fade_add) / fade;
            in_bounds = true;
        }
        in_bounds = true;
    }
    
    if (input.WorldPos.z > -2 && input.WorldPos.z < diameter + 2 && area == 0) {
        if (radius - dist3 < max_fade) {
            depth = 1.0f - (radius - dist3 - fade_add) / fade;
        }
        in_bounds = true;
    }

    if (!in_bounds) {
        return 1.0f;
    }

    return depth;
}

float4 OpaquePixelShaderFunction(VertexShaderOutput input) : COLOR0
{
    float4 light = min(float4(input.Light.xyz, 0) + float4(input.Light.w, input.Light.w, input.Light.w, 1), 1.1f);

	float4 textureColor = tex2D(textureSampler, input.TextureCoordinate);
	float depth = min(1, max(0, distance(input.PixelPos.xyz, float3(0, 0, 0)) / fog_distance));

    float fog = GetFog(input);
    if (radius <= 0) {
        fog = 0;
    }

    depth = max(0, min(1, depth));

	if (textureColor.a != 1) clip(-1);

	float4 final_color = textureColor * input.Color * color * light;

	final_color.r = lerp(final_color.r, fog_color.r, depth);
	final_color.g = lerp(final_color.g, fog_color.g, depth);
	final_color.b = lerp(final_color.b, fog_color.b, depth);
    
    final_color.r = lerp(final_color.r, 1, fog);
    final_color.g = lerp(final_color.g, 1, fog);
    final_color.b = lerp(final_color.b, 1, fog);
    
	return final_color;
}

float4 TransparentPixelShaderFunction(VertexShaderOutput input) : COLOR0
{
    float4 light = min(float4(input.Light.xyz, 0) + float4(input.Light.w, input.Light.w, input.Light.w, 1), 1.1f);

    float4 textureColor = tex2D(textureSampler, input.TextureCoordinate);
    float depth = min(1, max(0, distance(input.PixelPos.xyz, float3(0, 0, 0)) / fog_distance));

    float fog = GetFog(input);
    if (radius <= 0) {
        fog = 0;
    }

    depth = max(0, min(1, depth));

    if (textureColor.a <= 0 || textureColor.a == 1) clip(-1);

    float4 final_color = textureColor * input.Color * light;

    final_color.r = lerp(final_color.r, fog_color.r, depth);
    final_color.g = lerp(final_color.g, fog_color.g, depth);
    final_color.b = lerp(final_color.b, fog_color.b, depth);

    final_color.r = lerp(final_color.r, 1, fog);
    final_color.g = lerp(final_color.g, 1, fog);
    final_color.b = lerp(final_color.b, 1, fog);

    return final_color;
}

technique Specular
{
    pass Pass2
    {
        AlphaBlendEnable = true;
        ZEnable = true;
        ZWriteEnable = false;

        // Final Colour = srcColour * srcAlpha + destColour * (1 - srcAlpha)
        SrcBlend = SrcAlpha; // Normal Alpha Blending
        DestBlend = InvSrcAlpha; // Normal Alpha Blending
        BlendOp = Add; // Normal Alpha Blending

        VertexShader = compile VS_SHADERMODEL VertexShaderFunction();
        PixelShader = compile PS_SHADERMODEL TransparentPixelShaderFunction();
    }

    pass Pass1
    {
        AlphaBlendEnable = true;
        ZEnable = true;
        ZWriteEnable = true;

        // Final Colour = srcColour * srcAlpha + destColour * (1 - srcAlpha)
        SrcBlend = SrcAlpha; // Normal Alpha Blending
        DestBlend = InvSrcAlpha; // Normal Alpha Blending
        BlendOp = Add; // Normal Alpha Blending

        VertexShader = compile VS_SHADERMODEL VertexShaderFunction();
        PixelShader = compile PS_SHADERMODEL OpaquePixelShaderFunction();
    }
}