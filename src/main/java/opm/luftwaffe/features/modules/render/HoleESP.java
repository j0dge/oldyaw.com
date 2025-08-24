package opm.luftwaffe.features.modules.render;

import opm.luftwaffe.api.event.events.Render3DEvent;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.api.util.BlockUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class HoleESP extends Module {
    // Settings
    private final Setting<Integer> range = register(new Setting<>("Range", 10, 0, 25));
    private final Setting<Float> height = register(new Setting<>("Height", 0.0f, -1.0f, 1.0f));
    private final Setting<Integer> alpha = register(new Setting<>("Alpha", 128, 0, 255));
    private final Setting<Integer> lineAlpha = register(new Setting<>("LineAlpha", 255, 0, 255));
    private final Setting<Float> lineWidth = register(new Setting<>("LineWidth", 1.2f, 0.1f, 5.0f));
    public final Setting<Boolean> crossed = register(new Setting<>("Crossed", true));

    // Color settings
    private final Setting<Integer> Bred = register(new Setting<>("BedrockRed", 0, 0, 255));
    private final Setting<Integer> Bgreen = register(new Setting<>("BedrockGreen", 255, 0, 255));
    private final Setting<Integer> Bblue = register(new Setting<>("BedrockBlue", 0, 0, 255));
    private final Setting<Integer> Ored = register(new Setting<>("ObbyRed", 255, 0, 255));
    private final Setting<Integer> Ogreen = register(new Setting<>("ObbyGreen", 0, 0, 255));
    private final Setting<Integer> Oblue = register(new Setting<>("ObbyBlue", 0, 0, 255));

    private int count = 0;

    public HoleESP() {
        super("HoleESP", "browsky hole esp attempt :D", Module.Category.RENDER, false, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (mc.renderViewEntity == null) return;

        count = 0;
        Vec3i playerPos = new Vec3i(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
        int rangeValue = range.getValue();

        for (int x = playerPos.getX() - rangeValue; x <= playerPos.getX() + rangeValue; x++) {
            for (int z = playerPos.getZ() - rangeValue; z <= playerPos.getZ() + rangeValue; z++) {
                for (int y = playerPos.getY() + rangeValue; y >= playerPos.getY() - rangeValue; y--) {
                    BlockPos pos = new BlockPos(x, y, z);

                    // Check if position is a valid hole
                    if (!isValidHole(pos)) continue;

                    // Check for bedrock hole
                    if (isBedrockHole(pos)) {
                        drawHole(pos, true);
                        continue;
                    }

                    // Check for obsidian/other safe block hole
                    if (isSafeHole(pos)) {
                        drawHole(pos, false);
                    }
                }
            }
        }
    }

    private boolean isValidHole(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.AIR
                && mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR
                && mc.world.getBlockState(pos.up(2)).getBlock() == Blocks.AIR;
    }

    private boolean isBedrockHole(BlockPos pos) {
        return mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK
                && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK
                && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK
                && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK
                && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK;
    }

    private boolean isSafeHole(BlockPos pos) {
        return BlockUtil.isBlockUnSafe(mc.world.getBlockState(pos.down()).getBlock())
                && BlockUtil.isBlockUnSafe(mc.world.getBlockState(pos.east()).getBlock())
                && BlockUtil.isBlockUnSafe(mc.world.getBlockState(pos.west()).getBlock())
                && BlockUtil.isBlockUnSafe(mc.world.getBlockState(pos.south()).getBlock())
                && BlockUtil.isBlockUnSafe(mc.world.getBlockState(pos.north()).getBlock());
    }

    public void drawHole(BlockPos pos, boolean isBedrock) {
        count++;

        double x1 = pos.getX();
        double y1 = pos.getY();
        double z1 = pos.getZ();
        double x2 = x1 + 1;
        double y2 = y1 + height.getValue();
        double z2 = z1 + 1;

        float red, green, blue;
        if (isBedrock) {
            red = Bred.getValue() / 255.0f;
            green = Bgreen.getValue() / 255.0f;
            blue = Bblue.getValue() / 255.0f;
        } else {
            red = Ored.getValue() / 255.0f;
            green = Ogreen.getValue() / 255.0f;
            blue = Oblue.getValue() / 255.0f;
        }

        float alphaValue = alpha.getValue() / 255.0f;
        float lineAlphaValue = lineAlpha.getValue() / 255.0f;

        AxisAlignedBB bb = new AxisAlignedBB(
                x1 - mc.getRenderManager().viewerPosX,
                y1 - mc.getRenderManager().viewerPosY,
                z1 - mc.getRenderManager().viewerPosZ,
                x2 - mc.getRenderManager().viewerPosX,
                y2 - mc.getRenderManager().viewerPosY,
                z2 - mc.getRenderManager().viewerPosZ
        );

        setupRender();
        RenderGlobal.renderFilledBox(bb, red, green, blue, alphaValue);
        renderBox(bb, red, green, blue, lineAlphaValue);
        cleanupRender();
    }

    private void setupRender() {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(lineWidth.getValue());
    }

    private void cleanupRender() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderBox(AxisAlignedBB bb, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        // Bottom square
        buffer.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();

        // Vertical lines
        buffer.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();

        // Connect top and bottom
        buffer.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();

        tessellator.draw();

        if (crossed.getValue()) {
            renderCrosses(bb, red, green, blue, alpha);
        }
    }

    private void renderCrosses(AxisAlignedBB bb, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        // X crosses
        buffer.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();

        buffer.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();

        // Z crosses
        buffer.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();

        buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();

        tessellator.draw();
    }

    @Override
    public String getDisplayInfo() {
        return String.valueOf(count);
    }
}

