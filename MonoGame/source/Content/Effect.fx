float4x4 World;
float4x4 View;
float4x4 Projection;

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
};

VertexShaderOutput VertexShaderFunction(VertexPositionColorTexture input)
{
    VertexShaderOutput output;

    float4 worldPosition = mul(input.Position, World);
    float4 viewPosition = mul(worldPosition, View);
    output.Position = mul(viewPosition, Projection);

    output.Color = input.Color;

	output.TextureCoordinate = input.TextureCoordinate;

    return output;
}

float4 PixelShaderFunction(VertexShaderOutput input) : COLOR0
{	
	float4 textureColor = tex2D(textureSampler, input.TextureCoordinate);
	
	if (textureColor.a <= 0) clip(-1);
	
	return textureColor * (input.Color);
}

technique Specular
{
    pass Pass1
    {
        VertexShader = compile vs_2_0 VertexShaderFunction();
        PixelShader = compile ps_2_0 PixelShaderFunction();
    }
}