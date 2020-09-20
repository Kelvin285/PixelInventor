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

    float4 viewPosition = mul(worldPosition, View);
	
    output.Position = mul(viewPosition, Projection);

    output.Color = input.Color;

	output.TextureCoordinate = input.TextureCoordinate;

	output.PixelPos = output.Position;

	output.Position.y -= distance(output.Position.xz, float2(0, 0)) * 0.1f; //ROUND PLANET

	if (water) {
		output.Position.y += 0.25f * (sin(output.Position.x + time) + cos(output.Position.z + time));
	}

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
        VertexShader = compile vs_2_0 VertexShaderFunction();
        PixelShader = compile ps_3_0 PixelShaderFunction();
    }
}