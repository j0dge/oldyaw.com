package opm.luftwaffe.features.modules.render;

import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.api.event.events.Render3DEvent;
import opm.luftwaffe.api.util.ColorUtil;
import opm.luftwaffe.features.setting.Setting;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import org.lwjgl.opengl.GL11;

public class CrystalSpawns extends Module {
    private static CrystalSpawns INSTANCE = new CrystalSpawns();
    private final Setting<Boolean> Rainbow = this.register(new Setting("Rainbow", true));
    private final Setting<Integer> Red = this.register(new Setting("Red", 150, 0, 255));
    private final Setting<Integer> Green = this.register(new Setting("Green", 0, 0, 255));
    private final Setting<Integer> Blue = this.register(new Setting("Blue", 150, 0, 255));
    public static HashMap<UUID, CrystalSpawns.Thingering> thingers = new HashMap();

    public CrystalSpawns() {
        super("C-Spawns", "CrystalSpawns", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static CrystalSpawns getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrystalSpawns();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onUpdate() {
        Iterator var1 = mc.world.loadedEntityList.iterator();

        while(var1.hasNext()) {
            Entity entity = (Entity)var1.next();
            if (entity instanceof EntityEnderCrystal && !thingers.containsKey(entity.getUniqueID())) {
                thingers.put(entity.getUniqueID(), new CrystalSpawns.Thingering(this, entity));
                ((CrystalSpawns.Thingering)thingers.get(entity.getUniqueID())).starTime = System.currentTimeMillis();
            }
        }
    }

    public void onRender3D(Render3DEvent eventRender3D) {
        if (mc.player != null && mc.world != null) {
            Iterator var2 = thingers.entrySet().iterator();

            while(var2.hasNext()) {
                Entry<UUID, CrystalSpawns.Thingering> entry = (Entry)var2.next();
                if (System.currentTimeMillis() - ((CrystalSpawns.Thingering)entry.getValue()).starTime < 1500L) {
                    float opacity = Float.intBitsToFloat(Float.floatToIntBits(1.2886874E38F) ^ 2126636655);
                    long time = System.currentTimeMillis();
                    long duration = time - ((CrystalSpawns.Thingering)entry.getValue()).starTime;
                    if (duration < 1500L) {
                        opacity = Float.intBitsToFloat(Float.floatToIntBits(13.7902155F) ^ 2128389305) - (float)duration / Float.intBitsToFloat(Float.floatToIntBits(6.1687006E-4F) ^ 2124035443);
                    }

                    drawCircle(((CrystalSpawns.Thingering)entry.getValue()).entity, eventRender3D.getPartialTicks(), Double.longBitsToDouble(Double.doubleToLongBits(205.3116845075892D) ^ 9190063402910341247L), (float)(System.currentTimeMillis() - ((CrystalSpawns.Thingering)entry.getValue()).starTime) / Float.intBitsToFloat(Float.floatToIntBits(0.025765074F) ^ 2115703111), opacity);
                }
            }
        }
    }

    public static void drawCircle(Entity entity, float partialTicks, double rad, float plusY, float alpha) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        startSmooth();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(Float.intBitsToFloat(Float.floatToIntBits(0.8191538F) ^ 2131866640));
        GL11.glBegin(GL11.GL_LINE_STRIP);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - mc.getRenderManager().viewerPosZ;
        double pix2 = Double.longBitsToDouble(Double.doubleToLongBits(0.12418750450734782D) ^ 9198297930843979055L);

        for(int i = 0; i <= 90; ++i) {
            if ((Boolean)getInstance().Rainbow.getValue()) {
                GL11.glColor4f((float)ColorUtil.rainbow(50).getRed() / 255.0F, (float)ColorUtil.rainbow(50).getGreen() / 255.0F, (float)ColorUtil.rainbow(50).getBlue() / 255.0F, alpha);
            } else {
                GL11.glColor4f((float)(Integer)getInstance().Red.getValue() / 255.0F, (float)(Integer)getInstance().Green.getValue() / 255.0F, (float)(Integer)getInstance().Blue.getValue() / 255.0F, alpha);
            }

            GL11.glVertex3d(x + rad * Math.cos((double)i * Double.longBitsToDouble(Double.doubleToLongBits(0.038923223119235344D) ^ 9203893389252104647L) / Double.longBitsToDouble(Double.doubleToLongBits(0.010043755046771538D) ^ 9205940181866359915L)), y + (double)(plusY / Float.intBitsToFloat(Float.floatToIntBits(0.13022153F) ^ 2133153995)), z + rad * Math.sin((double)i * Double.longBitsToDouble(Double.doubleToLongBits(0.012655047216797511D) ^ 9192070147616231359L) / Double.longBitsToDouble(Double.doubleToLongBits(0.00992417958121009D) ^ 9206152726280235987L)));
        }

        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        endSmooth();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    public static void startSmooth() {
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
    }

    public static void endSmooth() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
    }

    public class Thingering {
        public Entity entity;
        public long starTime;
        public CrystalSpawns this$0;

        public Thingering(CrystalSpawns this$0x, Entity entity) {
            this.this$0 = this$0x;
            this.entity = entity;
            this.starTime = 0L;
        }
    }
}