float4x4 World;
float4x4 View;
float4x4 Projection;
float fog_distance;
float4 fog_color;
bool water;
float time;

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

VertexShaderOutput VertexShaderFunction(VertexPositionColorTexture input)
{
    VertexShaderOutput output;

    float4 worldPosition = mul(input.Position, World);

    output.Color = input.Color;

    if (water) {
        worldPosition.y -= abs(0.05f * (sin(worldPosition.x + time) + cos(worldPosition.z + time)));
        output.Color.a *= 0.5f;
    }

    float4 viewPosition = mul(worldPosition, View);


    output.Position = mul(viewPosition, Projection);

	output.TextureCoordinate = input.TextureCoordinate;

	output.PixelPos = output.Position;

	//output.Position.y -= distance(output.Position.xz, float2(0, 0)) * 0.1f; //ROUND PLANET

	

    return output;
}

float4 PixelShaderFunction(VertexShaderOutput input, float2 vPos : VPOS) : COLOR0
{	
	
	float4 textureColor = tex2D(textureSampler, input.TextureCoordinate);
	float depth = min(1, max(0, distance(input.PixelPos.xyz, float3(0, 0, 0)) / fog_distance));

	if (textureColor.a <= 0) clip(-1);

	float4 final_color = textureColor * input.Color;

	final_color.r = lerp(final_color.r, fog_color.r, depth);
	final_color.g = lerp(final_color.g, fog_color.g, depth);
	final_color.b = lerp(final_color.b, fog_color.b, depth);

	return final_color;
}

technique Specular
{
    pass Pass1
    {
        AlphaBlendEnable = true; // Setup For Alpha Blending
        ZEnable = true; // Setup For Alpha Blending
        ZWriteEnable = true; // Setup For Alpha Blending

        //SrcBlend = One; // Additive Blending
        //DestBlend = One; // Additive Blending
        //BlendOp = Add; // Additive Blending

        // Final Colour = srcColour * srcAlpha + destColour * (1 - srcAlpha)
        SrcBlend = SrcAlpha; // Normal Alpha Blending
        DestBlend = InvSrcAlpha; // Normal Alpha Blending
        BlendOp = Add; // Normal Alpha Blending

        VertexShader = compile vs_2_0 VertexShaderFunction();
        PixelShader = compile ps_3_0 PixelShaderFunction();
    }
}