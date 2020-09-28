float4x4 World;
float4x4 View;
float4x4 Projection;
float fog_distance;
float4 fog_color;
bool water;
float time;

float4 color;

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

    output.Color = input.Color;

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

	//output.Position.y -= distance(output.Position.xz, float2(0, 0)) * 0.1f; //ROUND PLANET

	

    return output;
}

float4 OpaquePixelShaderFunction(VertexShaderOutput input, float2 vPos : VPOS) : COLOR0
{	
	
	float4 textureColor = tex2D(textureSampler, input.TextureCoordinate);
	float depth = min(1, max(0, distance(input.PixelPos.xyz, float3(0, 0, 0)) / fog_distance));

	if (textureColor.a != 1) clip(-1);

	float4 final_color = textureColor * input.Color * color;

	final_color.r = lerp(final_color.r, fog_color.r, depth);
	final_color.g = lerp(final_color.g, fog_color.g, depth);
	final_color.b = lerp(final_color.b, fog_color.b, depth);

	return final_color;
}

float4 TransparentPixelShaderFunction(VertexShaderOutput input, float2 vPos : VPOS) : COLOR0
{

    float4 textureColor = tex2D(textureSampler, input.TextureCoordinate);
    float depth = min(1, max(0, distance(input.PixelPos.xyz, float3(0, 0, 0)) / fog_distance));

    if (textureColor.a <= 0 || textureColor.a == 1) clip(-1);

    float4 final_color = textureColor * input.Color;

    final_color.r = lerp(final_color.r, fog_color.r, depth);
    final_color.g = lerp(final_color.g, fog_color.g, depth);
    final_color.b = lerp(final_color.b, fog_color.b, depth);

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

        VertexShader = compile vs_2_0 VertexShaderFunction();
        PixelShader = compile ps_3_0 TransparentPixelShaderFunction();
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

        VertexShader = compile vs_2_0 VertexShaderFunction();
        PixelShader = compile ps_3_0 OpaquePixelShaderFunction();
    }
}