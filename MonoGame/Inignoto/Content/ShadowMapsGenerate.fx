#if OPENGL
#define SV_POSITION POSITION
#define VS_SHADERMODEL vs_3_0
#define PS_SHADERMODEL ps_3_0
#else
#define VS_SHADERMODEL vs_3_0
#define PS_SHADERMODEL ps_3_0
#endif

float4x4 World;
float4x4 View;
float4x4 Projection;

float fog_distance;
float4 fog_color;
bool water;
float time;
float3 sunLook;

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
    float Normal : NORMAL0;
    float4 Hue : COLOR1;
    float2 OverlayTextureCoordinate : TEXCOORD1;
};

struct VertexShaderOutput
{
    float4 Position : POSITION0;
    float4 Color : COLOR0;
	float2 TextureCoordinate : TEXCOORD0;
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

    int CAM_CX = (int)(camera_pos.x) / 16 - 4;
    int CAM_CY = (int)(camera_pos.y) / 16 - 2;
    int CAM_CZ = (int)(camera_pos.z) / 16 - 4;

    int CX = (int)(worldPosition.x) / 16 - CAM_CX;
    int CY = (int)(worldPosition.y) / 16 - CAM_CY;
    int CZ = (int)(worldPosition.z) / 16 - CAM_CZ;
    
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
    
    output.WorldPos = worldPosition;

    float4 viewPosition = mul(worldPosition, View);

    if (water) {

        float val = abs(sin(rand(float2(worldPosition.x, worldPosition.z)) + worldPosition.x + time));

        worldPosition.y -= 0.1f * val;

        float val2 = (1.0f - val) * 0.5f;
        float water_fog = (viewPosition.y - viewPosition.z) / 35.0f;

    }

    output.Position = mul(viewPosition, Projection);

    output.TextureCoordinate = input.TextureCoordinate;

    float depth = output.Position.z / output.Position.w;

    output.Color = float4(depth, 0, 0, 1);
    

    return output;
}

float4 OpaquePixelShaderFunction(VertexShaderOutput input) : COLOR0
{
	float4 textureColor = tex2D(textureSampler, input.TextureCoordinate);
	if (textureColor.a != 1) clip(-1);

    float c = (input.Color.x + input.Light.w * 0.001);

    float4 final_color = float4(c, c, c, 1);

    return final_color;
}

float4 TransparentPixelShaderFunction(VertexShaderOutput input) : COLOR0
{
    float4 textureColor = tex2D(textureSampler, input.TextureCoordinate);
	if (textureColor.a < 1) clip(-1);

    float c = (input.Color.x + input.Light.w * 0.001);

	float4 final_color = float4(c, c, c, 1);
    
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