package fr.max2.dfi.render;

import java.util.Arrays;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedQuadScaledRetextured extends BakedQuad
{
    private final TextureAtlasSprite texture;
    private final float scale;

    public BakedQuadScaledRetextured(BakedQuad quad, TextureAtlasSprite textureIn, float scale)
    {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.getTintIndex(), FaceBakery.getFacingFromVertexData(quad.getVertexData()), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
        this.texture = textureIn;
        this.scale = scale;
        this.remapQuad();
    }

    private void remapQuad()
    {
        for (int i = 0; i < 4; ++i)
        {
            int j = format.getIntegerSize() * i;
            int uvIndex = format.getUvOffsetById(0) / 4;
            this.vertexData[j + uvIndex] = Float.floatToRawIntBits(this.texture.getInterpolatedU(this.sprite.getUnInterpolatedU(Float.intBitsToFloat(this.vertexData[j + uvIndex])) * this.scale));
            this.vertexData[j + uvIndex + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV(this.sprite.getUnInterpolatedV(Float.intBitsToFloat(this.vertexData[j + uvIndex + 1])) * this.scale));
        }
    }
}