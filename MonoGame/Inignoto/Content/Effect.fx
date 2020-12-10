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

float4x4 ShadowView;

float4x4 ShadowProjection;

float4x4 ShadowProjection2;

float fog_distance;
float4 fog_color;
bool water;
float time;

float3 camera_pos;
float3 sunLook;

float4 color;
float4 ObjectLight;

float radius;
float area;

bool world_render;

bool has_shadows;

texture ShadowTexture;
texture ShadowTexture2;

texture ModelTexture;


sampler2D textureSampler = sampler_state {
    
    Texture = (ModelTexture);
    MagFilter = Point;
    MinFilter = Point;
    AddressU = Clamp;
    AddressV = Clamp;
};

sampler2D shadowSampler = sampler_state {

    Texture = (ShadowTexture);
    MagFilter = Point;
    MinFilter = Point;
    MipFilter = Point;
};

sampler2D shadowSampler2 = sampler_state {

    Texture = (ShadowTexture2);
    MagFilter = Point;
    MinFilter = Point;
    MipFilter = Point;
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
	float4 PixelPos : COLOR1;
    float4 WorldPos : TEXCOORD1;
    float4 Light : TEXCOORD2;
    float Dot : TEXCOORD3;
    float3 Normal : TEXCOORD4;
    float2 OverlayTextureCoordinate : TEXCOORD5;
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
        if (water) {
            worldPosition.x = round(worldPosition.x);
            worldPosition.z = round(worldPosition.z);
        }
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
    output.OverlayTextureCoordinate = input.OverlayTextureCoordinate;

    output.PixelPos = output.Position;


    float n = input.Normal;
    if (n == 0) output.Normal = float3(0, 1, 0);
    if (n == 1) output.Normal = float3(0, -1, 0);
    if (n == 2) output.Normal = float3(-1, 0, 0);
    if (n == 3) output.Normal = float3(1, 0, 0);
    if (n == 4) output.Normal = float3(0, 0, 1);
    if (n == 5) output.Normal = float3(0, 0, -1);

    //cross(normal, float3(0, -1, 0))
    output.Dot = dot(output.Normal, -sunLook);

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


float4 OpaquePixelShaderFunction(VertexShaderOutput input, float2 vPos : VPOS) : COLOR0
{
    float4x4 LightViewProj = mul(ShadowView, ShadowProjection);
    float4 lightingPosition = mul(input.WorldPos, LightViewProj);

    float4x4 LightViewProj2 = mul(ShadowView, ShadowProjection2);
    float4 lightingPosition2 = mul(input.WorldPos, LightViewProj2);

    float2 ShadowTexCoord = 0.5 * lightingPosition.xy / lightingPosition.w + float2(0.5, 0.5);
    ShadowTexCoord.y = 1.0f - ShadowTexCoord.y;

    float2 ShadowTexCoord2 = 0.5 * lightingPosition2.xy / lightingPosition2.w + float2(0.5, 0.5);
    ShadowTexCoord2.y = 1.0f - ShadowTexCoord2.y;

    float shadow = 0.0f;
    float shadow2 = 0.0f;
    float ShadowBias = 0.015f;


    //float ndotl = cross(normal, float3(0, -1, 0));
    float ndotl = input.Dot;

    float ourdepth1 = (lightingPosition.z / lightingPosition.w);
    float ourdepth2 = (lightingPosition2.z / lightingPosition2.w);

    float mul = 0.0001f;
    float I = 0;
    int size = 3;

    int II = 0;
    int II2 = 0;

    for (int xx = -size; xx < size+1; xx++) {
        if (!has_shadows) continue;
        for (int yy = -size; yy < size+1; yy++) {
            float depth1 = tex2D(shadowSampler, ShadowTexCoord + float2(xx, yy) * mul).r;
            float depth2 = tex2D(shadowSampler2, ShadowTexCoord2 + float2(xx, yy) * mul).r;

            if (depth1 > 0 && input.Normal.x == 0) {
                if (depth1 < ourdepth1 - ShadowBias * ourdepth1)
                    shadow += 0.5f;
                else {
                    shadow += 1.0f;
                }
            }
            else {
                shadow += 1.0f;
                II++;
            }

            if (depth2 > 0 && input.Normal.x == 0) {
                if (depth2 < ourdepth2 - ShadowBias)
                    shadow2 += 0.5f;
                else {
                    shadow2 += 1.0f;
                }
            }
            else {
                shadow2 += 1.0f;
                II2++;
            }
            I++;
        }
    }

    shadow /= I;
    shadow2 /= I;

    if (shadow > 0.8) shadow = 1.0f;
    if (shadow2 > 0.8) shadow2 = 1.0f;

    if (!has_shadows) {
        shadow = 1.0f;
        shadow2 = 1.0f;
    }

             //shadow = CalcShadowTermPCF(lightingPosition.z, ndotl, ShadowTexCoord);
    if (ShadowTexCoord2.x < 0 || ShadowTexCoord2.x > 1 || ShadowTexCoord2.y < 0 || ShadowTexCoord2.y > 1) {
        shadow = shadow2;
    }
    else {
        if (shadow2 < shadow) shadow = shadow2;
    }



    ndotl = max(0, ndotl);
    ndotl = (ndotl + 1) / 2;
    shadow = min(shadow, ndotl);


    if (!world_render) shadow = 1;

    float sun = min(input.Light.w, shadow);
    float sun2 = min(ObjectLight.w, shadow);

    if (world_render)
    {
        sun *= max(0.1, -sunLook.y);
        sun2 *= max(0.1, -sunLook.y);
    }

    float4 light = min(float4(input.Light.xyz, 1) + float4(sun, sun, sun, 1), 1.1f);
    float4 light2 = min(float4(ObjectLight.xyz, 1) + float4(sun2, sun2, sun2, 1), 1.1f);

    light = max(light, light2);

	float4 textureColor = tex2D(textureSampler, input.TextureCoordinate);
    float4 textureColor2 = tex2D(textureSampler, input.OverlayTextureCoordinate);
    if (textureColor2.a > 0 && input.OverlayTextureCoordinate.x >= 0) {
        float3 col = lerp(textureColor.xyz, textureColor2.xyz, textureColor2.w);
        textureColor = float4(col, min(textureColor.w + textureColor2.w, 1));
    }
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
    float4 light = min(float4(input.Light.xyz, 1), 1.1f);

    float4 textureColor = tex2D(textureSampler, input.TextureCoordinate);
    float4 textureColor2 = tex2D(textureSampler, input.OverlayTextureCoordinate);
    if (textureColor2.a > 0 && input.OverlayTextureCoordinate.x >= 0) {
        float3 col = lerp(textureColor.xyz, textureColor2.xyz, textureColor2.w);
        textureColor = float4(col, min(textureColor.w + textureColor2.w, 1));
    }
    float depth = min(1, max(0, distance(input.PixelPos.xyz, float3(0, 0, 0)) / fog_distance));

    float fog = GetFog(input);
    if (radius <= 0) {
        fog = 0;
    }

    depth = max(0, min(1, depth));

    if (textureColor.a <= 0 || textureColor.a == 1) clip(-1);

    float sun = 1;
    if (world_render)
        sun = max(0.1, -sunLook.y);

    float4 final_color = textureColor * input.Color * float4(sun, sun, sun, 1);

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
}