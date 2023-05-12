package com.zCore.Core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import optifine.Reflector;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import space.lunaclient.luna.impl.elements.combat.Killaura;
import space.lunaclient.luna.impl.events.MoveEvent;
import space.lunaclient.luna.impl.tools.ChatUtils;
import space.lunaclient.luna.impl.tools.FontUtils.FontType;
import space.lunaclient.luna.impl.tools.math.MathUtils;

import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Zhn
 */
public class zCore {

    public static int shit = 0;
    public int shit2 = 0;
    public static boolean back;
    private Graphics2D theGraphics;
    private final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-ORU]");
    private final Pattern patternUnsupported = Pattern.compile("(?i)\\u00A7[K-O]");
    private String getzCore = "(i)*zCore.*";
    private int startChar;
    private int endChar;
    private float[] xPos;
    private float[] yPos;
    private float extraSpacing = 0.0F;
    private DynamicTexture dynamicTexture;
    private DynamicTexture dynamicTextureBlurred = null;
    private FontMetrics theMetrics;
    public static String zCore_BUILD = "7";

    private static final Random rng = new Random();

    public static Random getRng() {
        return rng;
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569F * c.getRed();
        float g = 0.003921569F * c.getGreen();
        float b = 0.003921569F * c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    public static class SpeedUtils {

        public static float getDirection() {
            float var1 = Minecraft.thePlayer.rotationYaw;
            if (Minecraft.thePlayer.moveForward < 0.0F) {
                var1 += 180.0F;
            }
            float forward = 1.0F;
            if (Minecraft.thePlayer.moveForward < 0.0F) {
                forward = -0.5F;
            } else if (Minecraft.thePlayer.moveForward > 0.0F) {
                forward = 0.5F;
            }
            if (Minecraft.thePlayer.moveStrafing > 0.0F) {
                var1 -= 90.0F * forward;
            }
            if (Minecraft.thePlayer.moveStrafing < 0.0F) {
                var1 += 90.0F * forward;
            }
            var1 *= 0.017453292F;

            return var1;

        }
    }

    public static class WaitTimer {
        public static long time;

        public static boolean hasTimeElapsed(long time, boolean reset) {
            if (getTime() >= time) {
                if (reset) {
                    reset();
                }
                return true;
            }
            return false;
        }

        public static long getTime() {
            return System.nanoTime() / 1000000l - time;
        }

        public static void reset() {
            time = (System.nanoTime() / 1000000l);
        }
    }

    public static class AuraUtil {
        private static Random RANDOM = new Random();

        public static void faceEntity(EntityLivingBase entity) {
            float[] rotations = getRotations(entity);
            if (rotations != null) {
                Minecraft.thePlayer.rotationYaw = rotations[0];
                Minecraft.thePlayer.rotationPitch = rotations[1] - 8.0F;
            }

        }

        public static void faceEntityPacket(EntityLivingBase entity) {
            float[] rotations = getRotations(entity);
            if (rotations != null) {
                Minecraft.getMinecraft();
                Minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(rotations[0], rotations[1] - 8.0F,
                        Minecraft.thePlayer.onGround));
            }

        }

        public static float[] faceEntity(final Entity p_70625_1_, final float p_70625_2_, final float p_70625_3_) {
            final double var4 = p_70625_1_.posX - Minecraft.thePlayer.posX;
            final double var5 = p_70625_1_.posZ - Minecraft.thePlayer.posZ;
            double var7;
            if (p_70625_1_ instanceof EntityLivingBase) {
                final EntityLivingBase var6 = (EntityLivingBase) p_70625_1_;
                var7 = var6.posY + var6.getEyeHeight() - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight());
            } else {
                var7 = (p_70625_1_.getEntityBoundingBox().minY + p_70625_1_.getEntityBoundingBox().maxY) / 2.0 - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight());
            }
            final double var8 = MathHelper.sqrt_double(var4 * var4 + var5 * var5);
            final float yaw = (float) (Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
            final float pitch = (float) (-(Math.atan2(var7, var8) * 180.0 / 3.141592653589793));
            return new float[]{yaw, pitch};
        }

        public static float[] getRotations(final Entity entityIn) {
            final double x = entityIn.posX - Minecraft.thePlayer.posX;
            final double z = entityIn.posZ - Minecraft.thePlayer.posZ;
            double result;
            if (entityIn instanceof EntityLivingBase) {
                final EntityLivingBase entity = (EntityLivingBase) entityIn;
                result = entity.posY + entity.getEyeHeight() - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight());
            } else {
                result = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0 - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight());
            }
            final double var141 = MathHelper.sqrt_double(x * x + z * z);
            final float yaw = (float) (Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
            final float pitch = (float) (-(Math.atan2(result, var141) * 180.0 / 3.141592653589793));
            return new float[]{yaw, pitch};
        }

        public static float[] faceTileEntity(final TileEntityChest p_70625_1_, final float p_70625_2_, final float p_70625_3_) {
            final double var4 = p_70625_1_.getPos().getX() - Minecraft.thePlayer.posX + 0.25;
            final double var5 = p_70625_1_.getPos().getZ() - Minecraft.thePlayer.posZ + 0.25;
            final double var6 = p_70625_1_.getPos().getY() + 0.5 - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight()) + 0.22;
            final double var7 = MathHelper.sqrt_double(var4 * var4 + var5 * var5);
            final float yaw = (float) (Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
            final float pitch = (float) (-(Math.atan2(var6, var7) * 180.0 / 3.141592653589793));
            return new float[]{yaw, pitch};
        }

        public void faceEntityHard(final Entity p_70625_1_, final float p_70625_2_, final float p_70625_3_) {
            final double var4 = p_70625_1_.posX - Minecraft.thePlayer.posX;
            final double var5 = p_70625_1_.posZ - Minecraft.thePlayer.posZ;
            double var7;
            if (p_70625_1_ instanceof EntityLivingBase) {
                final EntityLivingBase var6 = (EntityLivingBase) p_70625_1_;
                var7 = var6.posY + var6.getEyeHeight() - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight());
            } else {
                var7 = (p_70625_1_.getEntityBoundingBox().minY + p_70625_1_.getEntityBoundingBox().maxY) / 2.0 - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight());
            }
            final double var8 = MathHelper.sqrt_double(var4 * var4 + var5 * var5);
            final float var9 = (float) (Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
            final float var10 = (float) (-(Math.atan2(var7, var8) * 180.0 / 3.141592653589793));
            Minecraft.thePlayer.rotationPitch = updateRotation(Minecraft.thePlayer.rotationPitch, var10, p_70625_3_);
            Minecraft.thePlayer.rotationYaw = updateRotation(Minecraft.thePlayer.rotationYaw, var9, p_70625_2_);
        }

        public static float updateRotation(final float p_70663_1_, final float p_70663_2_, final float p_70663_3_) {
            float var4 = MathHelper.wrapAngleTo180_float(p_70663_2_ - p_70663_1_);
            if (var4 > p_70663_3_) {
                var4 = p_70663_3_;
            }
            if (var4 < -p_70663_3_) {
                var4 = -p_70663_3_;
            }
            return p_70663_1_ + var4;
        }

        public static int random(int minCPSValue, int maxCPSValue) {
            return RANDOM.nextInt(maxCPSValue - minCPSValue) + minCPSValue;
        }
    }

    public static class AuraTimer {
        public static long lastMS;

        public AuraTimer() {
            lastMS = -1L;
        }

        public static void updateLastMS() {
            lastMS = System.currentTimeMillis();
        }

        public static void updateLastMS(final long plusMS) {
            lastMS += plusMS;
        }

        public static boolean hasTimePassedM(final long MS) {
            return System.currentTimeMillis() >= lastMS + MS;
        }

        public static boolean hasTimePassedS(final float speed) {
            return System.currentTimeMillis() >= lastMS + (long) (1000.0f / speed);
        }
    }

    /**
     * Same as Minecraft.getMinecraft(), Just with zCore.mc() now.
     *
     * @return
     */
    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    public static ArrayList<Vector3f> vanillaTeleportPositions(double tpX, double tpY, double tpZ, double speed) {
        ArrayList<Vector3f> positions = new ArrayList();
        Minecraft mc = Minecraft.getMinecraft();
        double posX = tpX - Minecraft.thePlayer.posX;
        double posY = tpY - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight() + 1.1D);
        double posZ = tpZ - Minecraft.thePlayer.posZ;
        float yaw = (float) (Math.atan2(posZ, posX) * 180.0D / 3.141592653589793D - 90.0D);
        float pitch = (float) (-Math.atan2(posY, Math.sqrt(posX * posX + posZ * posZ)) * 180.0D / 3.141592653589793D);
        double tmpX = Minecraft.thePlayer.posX;
        double tmpY = Minecraft.thePlayer.posY;
        double tmpZ = Minecraft.thePlayer.posZ;
        double steps = 1.0D;
        for (double d = speed; d < getDistance(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ, tpX, tpY,
                tpZ); d += speed) {
            steps += 1.0D;
        }
        for (double d = speed; d < getDistance(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ, tpX, tpY,
                tpZ); d += speed) {
            tmpX = Minecraft.thePlayer.posX - Math.sin(getDirection(yaw)) * d;
            tmpZ = Minecraft.thePlayer.posZ + Math.cos(getDirection(yaw)) * d;
            tmpY -= (Minecraft.thePlayer.posY - tpY) / steps;
            positions.add(new Vector3f((float) tmpX, (float) tmpY, (float) tmpZ));
        }
        positions.add(new Vector3f((float) tpX, (float) tpY, (float) tpZ));

        return positions;
    }

    public static float getDirection(float yaw) {
        if (Minecraft.thePlayer.moveForward < 0.0F) {
            yaw += 180.0F;
        }
        float forward = 1.0F;
        if (Minecraft.thePlayer.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (Minecraft.thePlayer.moveForward > 0.0F) {
            forward = 0.5F;
        }
        if (Minecraft.thePlayer.moveStrafing > 0.0F) {
            yaw -= 90.0F * forward;
        }
        if (Minecraft.thePlayer.moveStrafing < 0.0F) {
            yaw += 90.0F * forward;
        }
        yaw *= 0.017453292F;

        return yaw;
    }

    public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double d0 = x1 - x2;
        double d1 = y1 - y2;
        double d2 = z1 - z2;
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    /**
     * Gets the EntityPlayerSP class, To use this, do zCore.p().***
     *
     * @return
     */
    public static EntityPlayerSP p() {
        return Minecraft.thePlayer;
    }

    public static float[] getRotations(Entity entity) {
        double pX = Minecraft.thePlayer.posX;
        double pY = Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight();
        double pZ = Minecraft.thePlayer.posZ;
        double eX = entity.posX;
        double eY = entity.posY + entity.height / 2.0F;
        double eZ = entity.posZ;
        double dX = pX - eX;
        double dY = pY - eY;
        double dZ = pZ - eZ;
        double dH = Math.sqrt(Math.pow(dX, 2.0D) + Math.pow(dZ, 2.0D));
        double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0D;
        double pitch = Math.toDegrees(Math.atan2(dH, dY));
        return new float[]{(float) yaw, (float) (90.0D - pitch)};
    }

    public static String getDirections() {
        return mc.func_175606_aa().func_174811_aO().name();
    }

    public static class iTimer {

        private static long lastMS;

        public static long getCurrentMS() {
            return System.nanoTime() / 1000000L;
        }

        public static boolean hasReached(long milliseconds) {
            return getCurrentMS() - lastMS >= milliseconds;
        }

        public static long getTime() {
            return getCurrentMS() - lastMS;
        }

        public static void reset() {
            lastMS = getCurrentMS();
        }

    }

    public static float[] getRotationFromPosition(double x, double y, double z) {
        double xDiff = x - Minecraft.thePlayer.posX;
        double zDiff = z - Minecraft.thePlayer.posZ;
        double yDiff = y - Minecraft.thePlayer.posY - Minecraft.thePlayer.getEyeHeight();

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsNeededBlock(double x, double y, double z) {
        double diffX = x + 0.5D - Minecraft.thePlayer.posX;
        double diffZ = z + 0.5D - Minecraft.thePlayer.posZ;

        double diffY = y + 0.5D
                - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight());
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / 3.141592653589793D);
        return new float[]{
                Minecraft.thePlayer.rotationYaw
                        + MathHelper.wrapAngleTo180_float(yaw - Minecraft.thePlayer.rotationYaw),
                Minecraft.thePlayer.rotationPitch
                        + MathHelper.wrapAngleTo180_float(pitch - Minecraft.thePlayer.rotationPitch)};
    }

    public static float[] getRotationsNeededBlock(double x, double y, double z, double x1, double y1, double z1) {
        double diffX = x1 + 0.5D - x;
        double diffZ = z1 + 0.5D - z;

        double diffY = y1 + 0.5D - (y + Minecraft.thePlayer.getEyeHeight());
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    public static ArrayList<EntityLivingBase> getClosestEntities(float range) {
        ArrayList<EntityLivingBase> entities = new ArrayList<EntityLivingBase>();
        for (Object o : Minecraft.theWorld.loadedEntityList) {
            if (isNotItem(o) && !(o instanceof EntityPlayerSP)) {
                EntityLivingBase en = (EntityLivingBase) o;
                if (en instanceof EntityPlayer) {
                    if (Minecraft.thePlayer.getDistanceToEntity(en) < range) {
                        entities.add(en);
                    }
                }
            }
        }
        return entities;
    }

    public static boolean isNotItem(Object o) {
        return o instanceof EntityLivingBase;
    }

    public static EntityPlayerSP player() {
        return Minecraft.thePlayer;
    }

    public static PlayerControllerMP playerControll() {
        return Minecraft.playerController;
    }

    public static void sendChatMessage(String msg) {
        zCore.sendPacket(new C01PacketChatMessage(msg));
    }

    /**
     * Taken from Winter
     */
    public static void rainbowCircle(ScaledResolution sr, int x, int y, int rad) {
        GL11.glPushMatrix();
        int scale = zCore.mc().gameSettings.guiScale;
        zCore.mc().gameSettings.guiScale = 2;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        double tau = 6.283185307179586D;
        double radius = rad;
        double fans = 55.0D;
        GL11.glLineWidth(3.0F);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glBegin(3);
        for (int i = 0; i <= fans; i++) {
            Color color = new Color(Color.HSBtoRGB(
                    (float) (Minecraft.thePlayer.ticksExisted / fans + Math.sin(i / fans * 1.5707963267948966D))
                            % 1.0F,
                    0.5058824F, 1.0F));
            GL11.glColor3f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);
            GL11.glVertex2d(x + rad + radius * Math.cos(i * tau / fans), y + rad + radius * Math.sin(i * tau / fans));
        }
        GL11.glEnd();
        zCore.mc().gameSettings.guiScale = scale;
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }

    public static EntityLivingBase getClosestEntity(float range) {
        EntityLivingBase closestEntity = null;
        float mindistance = range;
        for (Object o : Minecraft.theWorld.loadedEntityList) {
            if (isNotItem(o) && !(o instanceof EntityPlayerSP)) {
                EntityLivingBase en = (EntityLivingBase) o;
                if (!validEntity(en)) {
                    continue;
                }
                if (Minecraft.thePlayer.getDistanceToEntity(en) < mindistance) {
                    mindistance = Minecraft.thePlayer.getDistanceToEntity(en);
                    closestEntity = en;
                }
            }
        }
        return closestEntity;
    }

    public static boolean validEntity(EntityLivingBase en) {
        if (en instanceof EntityVillager) {
            return false;
        }
        if (en.isEntityEqual(Minecraft.thePlayer)) {
            return false;
        }
        return false;
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.startDrawing(3);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.minZ);
        tessellator.draw();
        worldRenderer.startDrawing(3);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.minZ);
        tessellator.draw();
        worldRenderer.startDrawing(1);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.maxZ);
        tessellator.draw();
    }

    public static void onSetSpeedMode() {
        if (Minecraft.thePlayer.onGround && Minecraft.thePlayer.isMoving()) {
            Minecraft.thePlayer.jumpMovementFactor = (float) 0.8;
            net.minecraft.util.Timer.timerSpeed = 1.009F;
            Minecraft.thePlayer.jump();
        }
    }

    /**
     * Sets the players move speed to the specified amount.
     */
    public static void setMoveSpeed(final MoveEvent event, final double speed) {
        double forward = MovementInput.moveForward;
        double strafe = MovementInput.moveStrafe;
        float yaw = yaw();
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f))
                    - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }

    public static float[] faceEntity(final Entity p_70625_1_, final float p_70625_2_, final float p_70625_3_) {
        final double var4 = p_70625_1_.posX - Minecraft.thePlayer.posX;
        final double var5 = p_70625_1_.posZ - Minecraft.thePlayer.posZ;
        double var7;
        if (p_70625_1_ instanceof EntityLivingBase) {
            final EntityLivingBase var6 = (EntityLivingBase) p_70625_1_;
            var7 = var6.posY + var6.getEyeHeight()
                    - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight());
        } else {
            var7 = (p_70625_1_.getEntityBoundingBox().minY + p_70625_1_.getEntityBoundingBox().maxY) / 2.0
                    - (Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight());
        }
        final double var8 = MathHelper.sqrt_double(var4 * var4 + var5 * var5);
        final float yaw = (float) (Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float) (-(Math.atan2(var7, var8) * 180.0 / 3.141592653589793));
        return new float[]{yaw, pitch};
    }

    public static class RaycastUtil {
        private static Entity pointedEntity;


        /**
         * Credits to Centio / Silence / Cirex for this RayCast Util.
         *
         * @return
         */

        public static Entity raycastCubeCraft(Entity entity, float pitch, float yaw) {
            Entity var2 = mc.thePlayer;
            if ((var2 != null) && (mc.theWorld != null)) {
                double var3 = 6.0D;
                Vec3 var78 = mc.thePlayer.func_174824_e(mc.timer.renderPartialTicks);
                Vec3 var88 = getLook(pitch, yaw);
                Vec3 var68 = var78.addVector(var88.xCoord * var3, var88.yCoord * var3, var88.zCoord * var3);
                MovingObjectPosition objectMouseOver = mc.theWorld.rayTraceBlocks(var78, var68, false, false, false);
                double var5 = var3;
                Vec3 var7 = mc.thePlayer.getPositionVector().add(new Vec3(0.0D, mc.thePlayer.getEyeHeight(), 0.0D));

                var3 = 6.0D;
                var5 = 6.0D;
                if (objectMouseOver != null) {
                    var5 = objectMouseOver.hitVec.distanceTo(var7);
                }
                Vec3 var8 = getLook(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);
                Vec3 var9 = new Vec3(entity.posX, entity.posY, entity.posZ);
                Entity pointedEntity = null;
                Vec3 var10 = null;
                float var11 = 1.0F;
                List var12 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2,
                        var2.getEntityBoundingBox().addCoord(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3)
                                .expand(var11, var11, var11));
                double var13 = var5;
                for (int var15 = 0; var15 < var12.size(); var15++) {
                    Entity var16 = (Entity) var12.get(var15);
                    if (var16.canBeCollidedWith()) {
                        float var17 = var16.getCollisionBorderSize();
                        AxisAlignedBB var18 = var16.getEntityBoundingBox().expand(var17, var17,
                                var17);
                        MovingObjectPosition var19 = var18.calculateIntercept(var7, var9);
                        if (var18.isVecInside(var7)) {
                            if ((0.0D < var13) || (var13 == 0.0D)) {
                                pointedEntity = var16;
                                var10 = var19 == null ? var7 : var19.hitVec;
                                var13 = 0.0D;
                            }
                        } else if (var19 != null) {
                            double var20 = var7.distanceTo(var19.hitVec);
                            if ((var20 < var13) || (var13 == 0.0D)) {
                                boolean canRiderInteract = false;
                                if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                                    canRiderInteract = Reflector.callBoolean(Integer.valueOf(var15),
                                            Reflector.ForgeEntity_canRiderInteract, new Object[0]);
                                }
                                if ((var16 == var2.ridingEntity) && (!canRiderInteract)) {
                                    if (var13 == 0.0D) {
                                        pointedEntity = var16;
                                        var10 = var19.hitVec;
                                    }
                                } else {
                                    pointedEntity = var16;
                                    var10 = var19.hitVec;
                                    var13 = var20;
                                }
                            }
                        }
                    }
                }
                if ((pointedEntity != null) && ((var13 < var5) || (mc.objectMouseOver == null))) {
                    objectMouseOver = new MovingObjectPosition(pointedEntity, var10);
                    if (((pointedEntity instanceof EntityLivingBase)) || ((pointedEntity instanceof EntityItemFrame))) {
                        return pointedEntity;
                    }
                }
            }
            return Killaura.target;
        }

        public static Entity raycast(Entity entity, float pitch, float yaw) {
            Entity var2 = mc.thePlayer;
            if ((var2 != null) && (mc.theWorld != null)) {
                double var3 = 6.0D;
                Vec3 var78 = mc.thePlayer.func_174824_e(mc.timer.renderPartialTicks);
                Vec3 var88 = getLook(pitch, yaw);
                Vec3 var68 = var78.addVector(var88.xCoord * var3, var88.yCoord * var3, var88.zCoord * var3);
                MovingObjectPosition objectMouseOver = mc.theWorld.rayTraceBlocks(var78, var68, false, false, true);
                double var5 = var3;
                Vec3 var7 = mc.thePlayer.getPositionVector().add(new Vec3(0.0D, mc.thePlayer.getEyeHeight(), 0.0D));

                var3 = 6.0D;
                var5 = 6.0D;
                if (objectMouseOver != null) {
                    var5 = objectMouseOver.hitVec.distanceTo(var7);
                }
                Vec3 var8 = getLook(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);
                Vec3 var9 = new Vec3(entity.posX, entity.posY, entity.posZ);
                EntityLivingBase pointedEntity = null;
                Vec3 var10 = null;
                float var11 = 1.0F;
                List var12 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2,
                        var2.getEntityBoundingBox().addCoord(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3)
                                .expand(var11, var11, var11));
                double var13 = var5;
                for (int var15 = 0; var15 < var12.size(); var15++) {
                    Entity var16 = (Entity) var12.get(var15);
                    if (var16.canBeCollidedWith()) {
                        float var17 = var16.getCollisionBorderSize();
                        AxisAlignedBB var18 = var16.getEntityBoundingBox().expand(var17, var17,
                                var17);
                        MovingObjectPosition var19 = var18.calculateIntercept(var7, var9);
                        if (var18.isVecInside(var7)) {
                            if ((0.0D < var13) || (var13 == 0.0D)) {
                                pointedEntity = (EntityLivingBase) var16;
                                var10 = var19 == null ? var7 : var19.hitVec;
                                var13 = 0.0D;
                            }
                        } else if (var19 != null) {
                            double var20 = var7.distanceTo(var19.hitVec);
                            if ((var20 < var13) || (var13 == 0.0D)) {
                                boolean canRiderInteract = false;
                                if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                                    canRiderInteract = Reflector.callBoolean(Integer.valueOf(var15),
                                            Reflector.ForgeEntity_canRiderInteract, new Object[0]);
                                }
                                if ((var16 == var2.ridingEntity) && (!canRiderInteract)) {
                                    if (var13 == 0.0D) {
                                        pointedEntity = (EntityLivingBase) var16;
                                        var10 = var19.hitVec;
                                    }
                                } else {
                                    pointedEntity = (EntityLivingBase) var16;
                                    var10 = var19.hitVec;
                                    var13 = var20;
                                }
                            }
                        }
                    }
                }
                if ((pointedEntity != null) && ((var13 < var5) || (mc.objectMouseOver == null))) {
                    objectMouseOver = new MovingObjectPosition(pointedEntity, var10);
                    if (((pointedEntity instanceof EntityLivingBase)) || ((pointedEntity instanceof EntityMob)) || ((pointedEntity instanceof
                            EntityZombie)) || ((pointedEntity instanceof EntityAnimal)) || ((pointedEntity instanceof EntityAmbientCreature))) {
                        pointedEntity.setInvisible(false);
                        return pointedEntity;
                    }
                }
            }
            return null;
        }

        public static BlockPos rayTraceBlock(BlockPos p, float pitch, float yaw) {
            Entity var2 = mc.thePlayer;
            if ((var2 != null) && (mc.theWorld != null)) {
                double var3 = 6.0D;
                Vec3 var78 = mc.thePlayer.func_174824_e(mc.timer.renderPartialTicks);
                Vec3 var88 = getLook(pitch, yaw);
                Vec3 var68 = var78.addVector(var88.xCoord * var3, var88.yCoord * var3, var88.zCoord * var3);
                MovingObjectPosition objectMouseOver = mc.theWorld.rayTraceBlocks(var78, var68, false, false, true);
                double var5 = var3;
                Vec3 var7 = mc.thePlayer.getPositionVector().add(new Vec3(0.0D, mc.thePlayer.getEyeHeight(), 0.0D));

                var3 = 6.0D;
                var5 = 6.0D;
                if (objectMouseOver != null) {
                    var5 = objectMouseOver.hitVec.distanceTo(var7);
                }
                Vec3 var8 = getLook(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);
                Vec3 var9 = new Vec3(p);
                Entity pointedEntity = null;
                Vec3 var10 = null;
                float var11 = 1.0F;
                List var12 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2,
                        var2.getEntityBoundingBox().addCoord(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3)
                                .expand(var11, var11, var11));
                double var13 = var5;
                for (int var15 = 0; var15 < var12.size(); var15++) {
                    Entity var16 = (Entity) var12.get(var15);
                    if (var16.canBeCollidedWith()) {
                        float var17 = var16.getCollisionBorderSize();
                        AxisAlignedBB var18 = var16.getEntityBoundingBox().expand(var17, var17,
                                var17);
                        MovingObjectPosition var19 = var18.calculateIntercept(var7, var9);
                        if (var18.isVecInside(var7)) {
                            if ((0.0D < var13) || (var13 == 0.0D)) {
                                pointedEntity = var16;
                                var10 = var19 == null ? var7 : var19.hitVec;
                                var13 = 0.0D;
                            }
                        } else if (var19 != null) {
                            double var20 = var7.distanceTo(var19.hitVec);
                            if ((var20 < var13) || (var13 == 0.0D)) {
                                boolean canRiderInteract = false;
                                if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                                    canRiderInteract = Reflector.callBoolean(Integer.valueOf(var15),
                                            Reflector.ForgeEntity_canRiderInteract, new Object[0]);
                                }
                                if ((var16 == var2.ridingEntity) && (!canRiderInteract)) {
                                    if (var13 == 0.0D) {
                                        pointedEntity = var16;
                                        var10 = var19.hitVec;
                                    }
                                } else {
                                    pointedEntity = var16;
                                    var10 = var19.hitVec;
                                    var13 = var20;
                                }
                            }
                        }
                    }
                }
                if ((pointedEntity != null) && ((var13 < var5) || (mc.objectMouseOver == null))) {
                    objectMouseOver = new MovingObjectPosition(pointedEntity, var10);
                    if (((pointedEntity instanceof EntityLivingBase)) || ((pointedEntity instanceof EntityItemFrame))) {
                        mc.pointedEntity = pointedEntity;
                    }
                }
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    return objectMouseOver.func_178782_a();
                }
            }
            return null;
        }

        public static BlockPos rayTraceBlock(float pitch, float yaw) {
            Entity var2 = mc.thePlayer;
            if ((var2 != null) && (mc.theWorld != null)) {
                double var3 = 1337.0D;
                Vec3 var78 = mc.thePlayer.func_174824_e(mc.timer.renderPartialTicks);
                Vec3 var88 = getLook(pitch, yaw);
                Vec3 var68 = var78.addVector(var88.xCoord * var3, var88.yCoord * var3, var88.zCoord * var3);
                MovingObjectPosition objectMouseOver = mc.theWorld.rayTraceBlocks(var78, var68, false, false, true);
                double var5 = var3;
                Vec3 var7 = mc.thePlayer.getPositionVector().add(new Vec3(0.0D, mc.thePlayer.getEyeHeight(), 0.0D));

                var3 = 6.0D;
                var5 = 6.0D;
                if (objectMouseOver != null) {
                    var5 = objectMouseOver.hitVec.distanceTo(var7);
                }
                Vec3 var8 = getLook(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);
                Vec3 var9 = getLook(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);
                Entity pointedEntity = null;
                Vec3 var10 = null;
                float var11 = 1.0F;
                List var12 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2,
                        var2.getEntityBoundingBox().addCoord(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3)
                                .expand(var11, var11, var11));
                double var13 = var5;
                for (int var15 = 0; var15 < var12.size(); var15++) {
                    Entity var16 = (Entity) var12.get(var15);
                    if (var16.canBeCollidedWith()) {
                        float var17 = var16.getCollisionBorderSize();
                        AxisAlignedBB var18 = var16.getEntityBoundingBox().expand(var17, var17,
                                var17);
                        MovingObjectPosition var19 = var18.calculateIntercept(var7, var9);
                        if (var18.isVecInside(var7)) {
                            if ((0.0D < var13) || (var13 == 0.0D)) {
                                pointedEntity = var16;
                                var10 = var19 == null ? var7 : var19.hitVec;
                                var13 = 0.0D;
                            }
                        } else if (var19 != null) {
                            double var20 = var7.distanceTo(var19.hitVec);
                            if ((var20 < var13) || (var13 == 0.0D)) {
                                boolean canRiderInteract = false;
                                if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                                    canRiderInteract = Reflector.callBoolean(Integer.valueOf(var15),
                                            Reflector.ForgeEntity_canRiderInteract, new Object[0]);
                                }
                                if ((var16 == var2.ridingEntity) && (!canRiderInteract)) {
                                    if (var13 == 0.0D) {
                                        pointedEntity = var16;
                                        var10 = var19.hitVec;
                                    }
                                } else {
                                    pointedEntity = var16;
                                    var10 = var19.hitVec;
                                    var13 = var20;
                                }
                            }
                        }
                    }
                }
                if ((pointedEntity != null) && ((var13 < var5) || (mc.objectMouseOver == null))) {
                    objectMouseOver = new MovingObjectPosition(pointedEntity, var10);
                    if (((pointedEntity instanceof EntityLivingBase)) || ((pointedEntity instanceof EntityItemFrame))) {
                        mc.pointedEntity = pointedEntity;
                    }
                }
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    return objectMouseOver.func_178782_a();
                }
            }
            return null;
        }

        public static final Vec3 getLook(float p_174806_1_, float p_174806_2_) {
            float var3 = MathHelper.cos(-p_174806_2_ * 0.017453292F - 3.1415927F);
            float var4 = MathHelper.sin(-p_174806_2_ * 0.017453292F - 3.1415927F);
            float var5 = -MathHelper.cos(-p_174806_1_ * 0.017453292F);
            float var6 = MathHelper.sin(-p_174806_1_ * 0.017453292F);
            return new Vec3(var4 * var5, var6, var3 * var5);
        }


        public static EntityLivingBase raycastEntity(final EntityLivingBase e) { // Credits to Jan / Skush for this.
            final Entity pl = Minecraft.thePlayer;
            Entity point = null;
            if (pl != null && Minecraft.theWorld != null) {
                final double reach = 100.0;
                final Vec3 var7 = pl.func_174824_e(0.0f);
                final float[] prot = faceEntity(e, 360.0f, 360.0f);
                if (prot == null) {
                    return null;
                }
                final Vec3 var8 = toLookVec3(prot[0], prot[1]);
                final Vec3 var9 = var7.addVector(var8.xCoord * reach, var8.yCoord * reach, var8.zCoord * reach);
                Vec3 var10 = null;
                final List var11 = Minecraft.theWorld.getEntitiesWithinAABBExcludingEntity(pl,
                        pl.getEntityBoundingBox()
                                .addCoord(var8.xCoord * reach, var8.yCoord * reach, var8.zCoord * reach)
                                .expand(1.0, 1.0, 1.0));
                double var12 = reach;
                for (int var13 = 0; var13 < var11.size(); ++var13) {
                    final Entity var14 = (Entity) var11.get(var13);
                    if (var14.canBeCollidedWith()) {
                        final float var15 = var14.getCollisionBorderSize();
                        final AxisAlignedBB var16 = var14.getEntityBoundingBox().expand((double) var15, (double) var15,
                                (double) var15);
                        final MovingObjectPosition var17 = var16.calculateIntercept(var7, var9);
                        if (var16.isVecInside(var7)) {
                            if (0.0 < var12 || var12 == 0.0) {
                                point = var14;
                                var10 = ((var17 == null) ? var7 : var17.hitVec);
                                var12 = 0.0;
                            }
                        } else if (var17 != null) {
                            final double var18 = var7.distanceTo(var17.hitVec);
                            if (var18 < var12 || var12 == 0.0) {
                                point = var14;
                                var10 = var17.hitVec;
                                var12 = var18;
                            }
                        }
                    }
                }
                if (point != null && var12 < reach && point instanceof EntityLivingBase) {
                    return (EntityLivingBase) point;
                }
            }
            return e;
        }

        public static Vec3 toLookVec3(final float yaw, final float pitch) {
            final float rvar3 = MathHelper.cos(-yaw * 0.017453292f - 3.1415927f);
            final float rvar4 = MathHelper.sin(-yaw * 0.017453292f - 3.1415927f);
            final float rvar5 = -MathHelper.cos(-pitch * 0.017453292f);
            final float rvar6 = MathHelper.sin(-pitch * 0.017453292f);
            return new Vec3((double) (rvar4 * rvar5), (double) rvar6, (double) (rvar3 * rvar5));
        }
    }

    public static float[] getNeededRotations(final EntityLivingBase entity) {
        final Vec3d eyesPos = new Vec3d(Minecraft.thePlayer.posX,
                Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(),
                Minecraft.thePlayer.posZ);
        final AxisAlignedBB bb = entity.getEntityBoundingBox();
        final Vec3d vec = new Vec3d(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5,
                bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }

    public static class auraUtils {
        private static Random random = new Random();

        public static void attackMethod(Entity entity) {
            Minecraft.thePlayer.swingItem(); /** Swing the players item **/
            Minecraft.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK)); /** Attack the target via packets **/
            entity = RaycastUtil.raycastEntity((EntityLivingBase) entity);
            Minecraft.getNetHandler().netManager.rotationPackets((int) getNeededRotations((EntityLivingBase) entity)[0], (int) getNeededRotations((EntityLivingBase) entity)[1], true);
        }

        public static int random(Integer minCPSValue, Integer maxCPSValue) {
            return random.nextInt(maxCPSValue - minCPSValue) + minCPSValue;
        }
    }

    public static class Timer {
        private long lastMS = 0L;

        public int convertToMS(int d) {
            return 1000 / d;
        }

        public long getCurrentMS() {
            return System.nanoTime() / 1000000L;
        }

        public boolean hasReached(long milliseconds) {
            return getCurrentMS() - lastMS >= milliseconds;
        }

        public boolean hasTimeReached(long delay) {
            return System.currentTimeMillis() - lastMS >= delay;
        }

        public long getDelay() {
            return System.currentTimeMillis() - lastMS;
        }

        public void reset() {
            lastMS = getCurrentMS();
        }

        public void setLastMS() {
            lastMS = System.currentTimeMillis();
        }

        public void setLastMS(long lastMS) {
            this.lastMS = lastMS;
        }
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void glColor2(Color color) {
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f,
                (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
    }

    public static void glColor(int hex) {
        float alpha = (float) (hex >> 24 & 255) / 255.0f;
        float red = (float) (hex >> 16 & 255) / 255.0f;
        float green = (float) (hex >> 8 & 255) / 255.0f;
        float blue = (float) (hex & 255) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
        float red = 0.003921569f * (float) redRGB;
        float green = 0.003921569f * (float) greenRGB;
        float blue = 0.003921569f * (float) blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void drawBorderRect(int left, int top, int right, int bottom, int bcolor, int icolor, int bwidth) {
        drawRect(left + bwidth, top + bwidth, right - bwidth, bottom - bwidth, icolor);
        drawRect(left, top + 1, left + bwidth, bottom - 1, bcolor);
        drawRect(left + bwidth, top, right, top + bwidth, bcolor);
        drawRect(left + bwidth, bottom - bwidth, right, bottom, bcolor);
        drawRect(right - bwidth, top + bwidth, right, bottom - bwidth, bcolor);
    }

    public static void drawRectZZ(int x, int y, int x1, int y1) {
        GL11.glBegin(7);
        GL11.glVertex2f((float) x, (float) y1);
        GL11.glVertex2f((float) x1, (float) y1);
        GL11.glVertex2f((float) x1, (float) y);
        GL11.glVertex2f((float) x, (float) y);
        GL11.glEnd();
    }

    public static void outlineOne() {
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3008);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(2.0F);
        GL11.glEnable(2848);
        GL11.glEnable(2960);
        GL11.glClear(1024);
        GL11.glClearStencil(15);
        GL11.glStencilFunc(512, 1, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void outlineTwo() {
        GL11.glStencilFunc(512, 0, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6914);
    }

    public static void outlineThree() {
        GL11.glStencilFunc(514, 1, 15);
        GL11.glStencilOp(7680, 7680, 7680);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void outlineFour() {
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(10754);
        GL11.glPolygonOffset(1.0F, -2000000.0F);
        GL11.glColor4f(0.9529412F, 0.6117647F, 0.07058824F, 1.0F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    }

    public static int transparency(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * c.getRed();
        float g = 0.003921569f * c.getGreen();
        float b = 0.003921569f * c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    public static float getRandom() {
        return rng.nextFloat();
    }

    public static int getRandom(int cap) {
        return rng.nextInt(cap);
    }

    public static int getRandom(int floor, int cap) {
        return floor + rng.nextInt(cap - floor + 1);
    }

    public static void drawRect(int x, int y, int x1, int y1, int color) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        Gui.drawRect(x, y, x1, y1, color);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawRect(float g, float h, float i, float j, int colorRED, int colorGREEN, int colorBLUE,
                                int colorALPHA) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);

        GL11.glPushMatrix();
        GL11.glColor4f(colorRED, colorGREEN, colorBLUE, colorALPHA);
        GL11.glBegin(7);
        GL11.glVertex2d(i, h);
        GL11.glVertex2d(g, h);
        GL11.glVertex2d(g, j);
        GL11.glVertex2d(i, j);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void drawBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aa.minX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.maxZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.maxZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aa.minX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aa.minX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.maxZ);
        worldRenderer.addVertex(aa.minX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.minX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.minZ);
        worldRenderer.addVertex(aa.maxX, aa.maxY, aa.maxZ);
        worldRenderer.addVertex(aa.maxX, aa.minY, aa.maxZ);
        tessellator.draw();
    }

    public static void drawOutlinedBlockESP(double x, double y, double z, float red, float green, float blue,
                                            float alpha, float lineWidth) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        // GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        GL11.glColor4f(red, green, blue, alpha);
        drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawBlockESP(double x, double y, double z, float red, float green, float blue, float alpha,
                                    float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWidth) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        // GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D));
        GL11.glLineWidth(lineWidth);
        GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
        drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawSolidBlockESP(double x, double y, double z, float red, float green, float blue,
                                         float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        // GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawOutlinedEntityESP(double x, double y, double z, double width, double height, float red,
                                             float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        // GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawSolidEntityESP(double x, double y, double z, double width, double height, float red,
                                          float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        // GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green,
                                     float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        // GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
        drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawTracerLine(double x, double y, double z, Color color, float alpha, float lineWdith) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        // GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(lineWdith);
        glColor(color);
        GL11.glBegin(2);
        GL11.glVertex3d(0.0D, 0.0D + Minecraft.thePlayer.getEyeHeight(), 0.0D);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        // GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    public static void glColor(Color color) {
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
                color.getAlpha() / 255.0F);
    }

    public static void drawCircle(int x, int y, double r, int c) {
        float f = ((c >> 24) & 0xff) / 255F;
        float f1 = ((c >> 16) & 0xff) / 255F;
        float f2 = ((c >> 8) & 0xff) / 255F;
        float f3 = (c & 0xff) / 255F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = 0; i <= 360; i++) {
            double x2 = Math.sin(((i * Math.PI) / 180)) * r;
            double y2 = Math.cos(((i * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void drawFilledCircle(int x, int y, double r, int c) {
        float f = ((c >> 24) & 0xff) / 255F;
        float f1 = ((c >> 16) & 0xff) / 255F;
        float f2 = ((c >> 8) & 0xff) / 255F;
        float f3 = (c & 0xff) / 255F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360; i++) {
            double x2 = Math.sin(((i * Math.PI) / 180)) * r;
            double y2 = Math.cos(((i * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void dr(double i, double j, double k, double l, int i1) {
        if (i < k) {
            double j1 = i;
            i = k;
            k = j1;
        }

        if (j < l) {
            double k1 = j;
            j = l;
            l = k1;
        }

        float f = ((i1 >> 24) & 0xff) / 255F;
        float f1 = ((i1 >> 16) & 0xff) / 255F;
        float f2 = ((i1 >> 8) & 0xff) / 255F;
        float f3 = (i1 & 0xff) / 255F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(i, l, 0.0D);
        worldRenderer.addVertex(k, l, 0.0D);
        worldRenderer.addVertex(k, j, 0.0D);
        worldRenderer.addVertex(i, j, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static int getRainbowParallaxa(int speed, int offset) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        return Color.getHSBColor(hue, 1f, 1f).getRGB();
    }

    public static void sendPacket(Packet packet) {
        zCore.getSendQueue().addToSendQueue(packet);
    }

    public static NetHandlerPlayClient getSendQueue() {
        return zCore.p().sendQueue;
    }

    public static void sendPacketDirectToServer(Packet packet) {
        Minecraft.getNetHandler().netManager.sendPacket(packet);
    }

    private static Minecraft mc = Minecraft.getMinecraft();
    private static Random rand = new Random();

    public static boolean spectator;

    public static ArrayList<Integer> blackList = new ArrayList<Integer>();

    static double x;
    static double y;
    static double z;
    static double xPreEn;
    static double yPreEn;
    static double zPreEn;
    static double xPre;
    static double yPre;
    static double zPre;

    // static ArrayList<Vec3> positions = new ArrayList<Vec3>();
    // static ArrayList<Vec3> positionsBack = new ArrayList<Vec3>();

    private static void preInfiniteReach(double range, double maxXZTP, double maxYTP, ArrayList<Vec3> positionsBack,
                                         ArrayList<Vec3> positions, Vec3 targetPos, boolean tpStraight, boolean up, boolean attack,
                                         boolean tpUpOneBlock, boolean sneaking) {

    }

    private static void postInfiniteReach() {

    }

    public static boolean infiniteReach(double range, double maxXZTP, double maxYTP, ArrayList<Vec3> positionsBack,
                                        ArrayList<Vec3> positions, EntityLivingBase en) {

        int ind = 0;
        xPreEn = en.posX;
        yPreEn = en.posY;
        zPreEn = en.posZ;
        xPre = Minecraft.thePlayer.posX;
        yPre = Minecraft.thePlayer.posY;
        zPre = Minecraft.thePlayer.posZ;
        boolean attack = true;
        boolean up = false;
        boolean tpUpOneBlock = false;

        // If something in the way
        boolean hit = false;
        boolean tpStraight = false;

        boolean sneaking = Minecraft.thePlayer.isSneaking();

        positions.clear();
        positionsBack.clear();

        // preInfiniteReach(range, maxXZTP, maxYTP, positionsBack, positions, new
        // Vec3(en.posX, en.posY, en.posZ), tpStraight, up, attack, tpUpOneBlock,
        // sneaking);
        double step = maxXZTP / range;
        int steps = 0;
        for (int i = 0; i < range; i++) {
            steps++;
            // Jigsaw.chatMessage(maxXZTP * steps);
            if (maxXZTP * steps > range) {
                break;
            }
        }
        MovingObjectPosition rayTrace = null;
        MovingObjectPosition rayTrace1 = null;
        MovingObjectPosition rayTraceCarpet = null;
        if ((rayTraceWide(new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ),
                new Vec3(en.posX, en.posY, en.posZ), false, false, true))
                || (rayTrace1 = rayTracePos(
                new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(), Minecraft.thePlayer.posZ),
                new Vec3(en.posX, en.posY + Minecraft.thePlayer.getEyeHeight(), en.posZ), false, false,
                true)) != null) {
            if ((rayTrace = rayTracePos(new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ),
                    new Vec3(en.posX, Minecraft.thePlayer.posY, en.posZ), false, false, true)) != null
                    || (rayTrace1 = rayTracePos(
                    new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(),
                            Minecraft.thePlayer.posZ),
                    new Vec3(en.posX, Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(), en.posZ), false, false,
                    true)) != null) {
                MovingObjectPosition trace = null;
                if (rayTrace == null) {
                    trace = rayTrace1;
                }
                if (rayTrace1 == null) {
                    trace = rayTrace;
                }
                if (trace == null) {
                    // y = mc.thePlayer.posY;
                    // yPreEn = mc.thePlayer.posY;
                } else {
                    if (trace.getBlockPos() != null) {
                        boolean fence = false;
                        BlockPos target = trace.getBlockPos();
                        // positions.add(BlockTools.getVec3(target));
                        up = true;
                        y = target.up().getY();
                        yPreEn = target.up().getY();
                        Block lastBlock = null;
                        Boolean found = false;
                        for (int i = 0; i < maxYTP; i++) {
                            MovingObjectPosition tr = rayTracePos(
                                    new Vec3(Minecraft.thePlayer.posX, target.getY() + i, Minecraft.thePlayer.posZ),
                                    new Vec3(en.posX, target.getY() + i, en.posZ), false, false, true);
                            if (tr == null) {
                                continue;
                            }
                            if (tr.getBlockPos() == null) {
                                continue;
                            }

                            BlockPos blockPos = tr.getBlockPos();
                            Block block = Minecraft.theWorld.getBlockState(blockPos).getBlock();
                            if (block.getMaterial() != Material.air) {
                                lastBlock = block;
                                continue;
                            }
                            fence = lastBlock instanceof BlockFence;
                            y = target.getY() + i;
                            yPreEn = target.getY() + i;
                            if (fence) {
                                y += 1;
                                yPreEn += 1;
                                if (i + 1 > maxYTP) {
                                    found = false;
                                    break;
                                }
                            }
                            found = true;
                            break;
                        }
                        double difX = Minecraft.thePlayer.posX - xPreEn;
                        double difZ = Minecraft.thePlayer.posZ - zPreEn;
                        double divider = step * 0;
                        if (!found) {
                            attack = false;
                            return false;
                        }
                    } else {
                        attack = false;
                        return false;
                    }
                }
            } else {
                MovingObjectPosition ent = rayTracePos(
                        new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ),
                        new Vec3(en.posX, en.posY, en.posZ), false, false, false);
                if (ent != null && ent.entityHit == null) {
                    y = Minecraft.thePlayer.posY;
                    yPreEn = Minecraft.thePlayer.posY;
                } else {
                    y = Minecraft.thePlayer.posY;
                    yPreEn = en.posY;
                }

            }
        }
        if (!attack) {
            return false;
        }
        if (sneaking) {
            Minecraft.getNetHandler().getNetworkManager()
                    .sendPacket(new C0BPacketEntityAction(Minecraft.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }
        for (int i = 0; i < steps; i++) {
            ind++;
            if (i == 1 && up) {
                x = Minecraft.thePlayer.posX;
                y = yPreEn;
                z = Minecraft.thePlayer.posZ;
                sendPacket(false, positionsBack, positions);
            }
            if (i != steps - 1) {
                {
                    double difX = Minecraft.thePlayer.posX - xPreEn;
                    double difY = Minecraft.thePlayer.posY - yPreEn;
                    double difZ = Minecraft.thePlayer.posZ - zPreEn;
                    double divider = step * i;
                    x = Minecraft.thePlayer.posX - difX * divider;
                    y = Minecraft.thePlayer.posY - difY * (up ? 1 : divider);
                    z = Minecraft.thePlayer.posZ - difZ * divider;
                }
                sendPacket(false, positionsBack, positions);
            } else {
                // if last teleport
                {
                    double difX = Minecraft.thePlayer.posX - xPreEn;
                    double difY = Minecraft.thePlayer.posY - yPreEn;
                    double difZ = Minecraft.thePlayer.posZ - zPreEn;
                    double divider = step * i;
                    x = Minecraft.thePlayer.posX - difX * divider;
                    y = Minecraft.thePlayer.posY - difY * (up ? 1 : divider);
                    z = Minecraft.thePlayer.posZ - difZ * divider;
                }
                sendPacket(false, positionsBack, positions);
                double xDist = x - xPreEn;
                double zDist = z - zPreEn;
                double yDist = y - en.posY;
                double dist = Math.sqrt(xDist * xDist + zDist * zDist);
                if (dist > 4) {
                    x = xPreEn;
                    y = yPreEn;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                } else if (dist > 0.05 && up) {
                    x = xPreEn;
                    y = yPreEn;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                }
                if (Math.abs(yDist) < maxYTP && Minecraft.thePlayer.getDistanceToEntity(en) >= 4) {
                    x = xPreEn;
                    y = en.posY;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                    if (en.onGround) {
                        for (int ii = 0; ii < 300; ii++) {
                            Minecraft.getNetHandler().getNetworkManager()
                                    .sendPacket(new C03PacketPlayer(Minecraft.thePlayer.onGround));
                        }
                    }
                    attackInf(en);
                } else {
                    attack = false;
                }
            }
        }

        // Go back!
        for (int i = positions.size() - 2; i > -1; i--) {
            {
                x = positions.get(i).xCoord;
                y = positions.get(i).yCoord;
                z = positions.get(i).zCoord;
            }
            sendPacket(false, positionsBack, positions);
        }
        x = Minecraft.thePlayer.posX;
        y = Minecraft.thePlayer.posY;
        z = Minecraft.thePlayer.posZ;
        sendPacket(false, positionsBack, positions);
        if (!attack) {
            if (sneaking) {
                Minecraft.getNetHandler().getNetworkManager().sendPacket(
                        new C0BPacketEntityAction(Minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            }
            positions.clear();
            positionsBack.clear();
            return false;
        }
        if (sneaking) {
            Minecraft.getNetHandler().getNetworkManager()
                    .sendPacket(new C0BPacketEntityAction(Minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        }
        return true;
    }

    public static void blinkToPosFromPos(Vec3 src, Vec3 dest, double maxTP) {
        double range = 0;
        double xDist = src.xCoord - dest.xCoord;
        double yDist = src.yCoord - dest.yCoord;
        double zDist = src.zCoord - dest.zCoord;
        double x1 = src.xCoord;
        double y1 = src.yCoord;
        double z1 = src.zCoord;
        double x2 = dest.xCoord;
        double y2 = dest.yCoord;
        double z2 = dest.zCoord;
        range = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
        double step = maxTP / range;
        int steps = 0;
        for (int i = 0; i < range; i++) {
            steps++;
            if (maxTP * steps > range) {
                break;
            }
        }
        for (int i = 0; i < steps; i++) {
            double difX = x1 - x2;
            double difY = y1 - y2;
            double difZ = z1 - z2;
            double divider = step * i;
            double x = x1 - difX * divider;
            double y = y1 - difY * divider;
            double z = z1 - difZ * divider;
            Minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        }
        Minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z2, true));
    }

    public static boolean infiniteReach(double range, double maxXZTP, double maxYTP, ArrayList<Vec3> positionsBack,
                                        ArrayList<Vec3> positions, BlockPos targetBlockPos) {
        positions.clear();
        positionsBack.clear();
        boolean tpUpOneBlock = false;
        double step = maxXZTP / range;
        int steps = 0;
        for (int i = 0; i < range; i++) {
            steps++;
            // Jigsaw.chatMessage(maxXZTP * steps);
            if (maxXZTP * steps > range) {
                break;
            }
        }
        int ind = 0;
        double posX = ((double) targetBlockPos.getX()) + 0.5;
        double posY = ((double) targetBlockPos.getY()) + 1.0;
        double posZ = ((double) targetBlockPos.getZ()) + 0.5;
        xPreEn = posX;
        yPreEn = posY;
        zPreEn = posZ;
        xPre = Minecraft.thePlayer.posX;
        yPre = Minecraft.thePlayer.posY;
        zPre = Minecraft.thePlayer.posZ;
        boolean attack = true;
        boolean up = false;

        // If something in the way
        boolean hit = false;
        boolean tpStraight = false;
        boolean sneaking = Minecraft.thePlayer.isSneaking();
        MovingObjectPosition rayTrace = null;
        MovingObjectPosition rayTrace1 = null;
        MovingObjectPosition rayTraceCarpet = null;
        if ((rayTraceWide(new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ), new Vec3(posX, posY, posZ),
                false, false, true))
                || (rayTrace1 = rayTracePos(
                new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(), Minecraft.thePlayer.posZ),
                new Vec3(posX, posY + Minecraft.thePlayer.getEyeHeight(), posZ), false, false, true)) != null) {
            if ((rayTrace = rayTracePos(new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ),
                    new Vec3(posX, Minecraft.thePlayer.posY, posZ), false, false, true)) != null
                    || (rayTrace1 = rayTracePos(
                    new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(),
                            Minecraft.thePlayer.posZ),
                    new Vec3(posX, Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(), posZ), false, false,
                    true)) != null) {
                MovingObjectPosition trace = null;
                if (rayTrace == null) {
                    trace = rayTrace1;
                }
                if (rayTrace1 == null) {
                    trace = rayTrace;
                }
                if (trace == null) {
                    // y = mc.thePlayer.posY;
                    // yPreEn = mc.thePlayer.posY;
                } else {
                    if (trace.getBlockPos() != null) {
                        boolean fence = false;
                        BlockPos target = trace.getBlockPos();
                        // positions.add(BlockTools.getVec3(target));
                        up = true;
                        y = target.up().getY();
                        yPreEn = target.up().getY();
                        Block lastBlock = null;
                        Boolean found = false;
                        for (int i = 0; i < maxYTP; i++) {
                            MovingObjectPosition tr = rayTracePos(
                                    new Vec3(Minecraft.thePlayer.posX, target.getY() + i, Minecraft.thePlayer.posZ),
                                    new Vec3(posX, target.getY() + i, posZ), false, false, true);
                            if (tr == null) {
                                continue;
                            }
                            if (tr.getBlockPos() == null) {
                                continue;
                            }

                            BlockPos blockPos = tr.getBlockPos();
                            Block block = Minecraft.theWorld.getBlockState(blockPos).getBlock();
                            if (block.getMaterial() != Material.air) {
                                lastBlock = block;
                                continue;
                            }
                            fence = lastBlock instanceof BlockFence;
                            y = target.getY() + i;
                            yPreEn = target.getY() + i;
                            if (fence) {
                                y += 1;
                                yPreEn += 1;
                                if (i + 1 > maxYTP) {
                                    found = false;
                                    break;
                                }
                            }
                            found = true;
                            break;
                        }
                        double difX = Minecraft.thePlayer.posX - xPreEn;
                        double difZ = Minecraft.thePlayer.posZ - zPreEn;
                        double divider = step * 0;
                        if (!found) {
                            attack = false;
                            return false;
                        }
                    } else {
                        attack = false;
                        return false;
                    }
                }
            } else {
                MovingObjectPosition ent = rayTracePos(
                        new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ), new Vec3(posX, posY, posZ),
                        false, false, false);
                if (ent != null && ent.entityHit == null) {
                    y = Minecraft.thePlayer.posY;
                    yPreEn = Minecraft.thePlayer.posY;
                } else {
                    y = Minecraft.thePlayer.posY;
                    yPreEn = posY;
                }

            }
        }
        if (!attack) {
            return false;
        }
        if (sneaking) {
            Minecraft.getNetHandler().getNetworkManager()
                    .sendPacket(new C0BPacketEntityAction(Minecraft.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }
        for (int i = 0; i < steps; i++) {
            ind++;
            if (i == 1 && up) {
                x = Minecraft.thePlayer.posX;
                y = yPreEn;
                z = Minecraft.thePlayer.posZ;
                sendPacket(false, positionsBack, positions);
            }
            if (i != steps - 1) {
                {
                    double difX = Minecraft.thePlayer.posX - xPreEn;
                    double difY = Minecraft.thePlayer.posY - yPreEn;
                    double difZ = Minecraft.thePlayer.posZ - zPreEn;
                    double divider = step * i;
                    x = Minecraft.thePlayer.posX - difX * divider;
                    y = Minecraft.thePlayer.posY - difY * (up ? 1 : divider);
                    z = Minecraft.thePlayer.posZ - difZ * divider;
                }
                sendPacket(false, positionsBack, positions);
            } else {
                // if last teleport
                {
                    double difX = Minecraft.thePlayer.posX - xPreEn;
                    double difY = Minecraft.thePlayer.posY - yPreEn;
                    double difZ = Minecraft.thePlayer.posZ - zPreEn;
                    double divider = step * i;
                    x = Minecraft.thePlayer.posX - difX * divider;
                    y = Minecraft.thePlayer.posY - difY * (up ? 1 : divider);
                    z = Minecraft.thePlayer.posZ - difZ * divider;
                }
                sendPacket(false, positionsBack, positions);
                double xDist = x - xPreEn;
                double zDist = z - zPreEn;
                double yDist = y - posY;
                double dist = Math.sqrt(xDist * xDist + zDist * zDist);
                if (dist > 4) {
                    x = xPreEn;
                    y = yPreEn;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                } else if (dist > 0.05 && up) {
                    x = xPreEn;
                    y = yPreEn;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                }
                if (Math.abs(yDist) < maxYTP && Minecraft.thePlayer.getDistance(posX, posY, posZ) >= 4) {
                    x = xPreEn;
                    y = posY;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                    Minecraft.thePlayer.swingItem();
                    Minecraft.getNetHandler().getNetworkManager()
                            .sendPacket(new C07PacketPlayerDigging(
                                    net.minecraft.network.play.client.C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                                    targetBlockPos, EnumFacing.UP));
                    Minecraft.getNetHandler().getNetworkManager()
                            .sendPacket(new C07PacketPlayerDigging(
                                    net.minecraft.network.play.client.C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                                    targetBlockPos, EnumFacing.UP));
                } else {
                    attack = false;
                }
            }
        }

        // Go back!
        for (int i = positions.size() - 2; i > -1; i--) {
            {
                x = positions.get(i).xCoord;
                y = positions.get(i).yCoord;
                z = positions.get(i).zCoord;
            }
            sendPacket(false, positionsBack, positions);
        }
        x = Minecraft.thePlayer.posX;
        y = Minecraft.thePlayer.posY;
        z = Minecraft.thePlayer.posZ;
        sendPacket(false, positionsBack, positions);
        if (!attack) {
            if (sneaking) {
                Minecraft.getNetHandler().getNetworkManager().sendPacket(
                        new C0BPacketEntityAction(Minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            }
            positions.clear();
            positionsBack.clear();
            return false;
        }
        if (sneaking) {
            Minecraft.getNetHandler().getNetworkManager()
                    .sendPacket(new C0BPacketEntityAction(Minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        }
        return true;
    }

    public static boolean infiniteReach(Vec3 src, Vec3 dest, double range, double maxXZTP, double maxYTP,
                                        ArrayList<Vec3> positionsBack, ArrayList<Vec3> positions) {
        positions.clear();
        positionsBack.clear();
        boolean tpUpOneBlock = false;
        double step = maxXZTP / range;
        int steps = 0;
        for (int i = 0; i < range; i++) {
            steps++;
            // Jigsaw.chatMessage(maxXZTP * steps);
            if (maxXZTP * steps > range) {
                break;
            }
        }
        int ind = 0;
        xPreEn = dest.xCoord;
        yPreEn = dest.yCoord;
        zPreEn = dest.zCoord;
        xPre = Minecraft.thePlayer.posX;
        yPre = Minecraft.thePlayer.posY;
        zPre = Minecraft.thePlayer.posZ;
        boolean attack = true;
        boolean up = false;

        // If something in the way
        boolean hit = false;
        boolean tpStraight = false;
        boolean sneaking = Minecraft.thePlayer.isSneaking();
        MovingObjectPosition rayTrace = null;
        MovingObjectPosition rayTrace1 = null;
        MovingObjectPosition rayTraceCarpet = null;
        if ((rayTraceWide(new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ),
                new Vec3(dest.xCoord, dest.yCoord, dest.zCoord), false, false, true))
                || (rayTrace1 = rayTracePos(
                new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(), Minecraft.thePlayer.posZ),
                new Vec3(dest.xCoord, dest.yCoord + Minecraft.thePlayer.getEyeHeight(), dest.zCoord), false, false,
                true)) != null) {
            if ((rayTrace = rayTracePos(new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ),
                    new Vec3(dest.xCoord, Minecraft.thePlayer.posY, dest.zCoord), false, false, true)) != null
                    || (rayTrace1 = rayTracePos(
                    new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(),
                            Minecraft.thePlayer.posZ),
                    new Vec3(dest.xCoord, Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(), dest.zCoord), false,
                    false, true)) != null) {
                MovingObjectPosition trace = null;
                if (rayTrace == null) {
                    trace = rayTrace1;
                }
                if (rayTrace1 == null) {
                    trace = rayTrace;
                }
                if (trace == null) {
                    // y = mc.thePlayer.posY;
                    // yPreEn = mc.thePlayer.posY;
                } else {
                    if (trace.getBlockPos() != null) {
                        boolean fence = false;
                        BlockPos target = trace.getBlockPos();
                        // positions.add(BlockTools.getVec3(target));
                        up = true;
                        y = target.up().getY();
                        yPreEn = target.up().getY();
                        Block lastBlock = null;
                        Boolean found = false;
                        for (int i = 0; i < maxYTP; i++) {
                            MovingObjectPosition tr = rayTracePos(
                                    new Vec3(Minecraft.thePlayer.posX, target.getY() + i, Minecraft.thePlayer.posZ),
                                    new Vec3(dest.xCoord, target.getY() + i, dest.zCoord), false, false, true);
                            if (tr == null) {
                                continue;
                            }
                            if (tr.getBlockPos() == null) {
                                continue;
                            }

                            BlockPos blockPos = tr.getBlockPos();
                            Block block = Minecraft.theWorld.getBlockState(blockPos).getBlock();
                            if (block.getMaterial() != Material.air) {
                                lastBlock = block;
                                continue;
                            }
                            fence = lastBlock instanceof BlockFence;
                            y = target.getY() + i;
                            yPreEn = target.getY() + i;
                            if (fence) {
                                y += 1;
                                yPreEn += 1;
                                if (i + 1 > maxYTP) {
                                    found = false;
                                    break;
                                }
                            }
                            found = true;
                            break;
                        }
                        double difX = Minecraft.thePlayer.posX - xPreEn;
                        double difZ = Minecraft.thePlayer.posZ - zPreEn;
                        double divider = step * 0;
                        if (!found) {
                            attack = false;
                            return false;
                        }
                    } else {
                        attack = false;
                        return false;
                    }
                }
            } else {
                MovingObjectPosition ent = rayTracePos(
                        new Vec3(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY, Minecraft.thePlayer.posZ),
                        new Vec3(dest.xCoord, dest.yCoord, dest.zCoord), false, false, false);
                if (ent != null && ent.entityHit == null) {
                    y = Minecraft.thePlayer.posY;
                    yPreEn = Minecraft.thePlayer.posY;
                } else {
                    y = Minecraft.thePlayer.posY;
                    yPreEn = dest.yCoord;
                }

            }
        }
        if (!attack) {
            return false;
        }
        if (sneaking) {
            Minecraft.getNetHandler().getNetworkManager()
                    .sendPacket(new C0BPacketEntityAction(Minecraft.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }
        for (int i = 0; i < steps; i++) {
            ind++;
            if (i == 1 && up) {
                x = Minecraft.thePlayer.posX;
                y = yPreEn;
                z = Minecraft.thePlayer.posZ;
                sendPacket(false, positionsBack, positions);
            }
            if (i != steps - 1) {
                {
                    double difX = Minecraft.thePlayer.posX - xPreEn;
                    double difY = Minecraft.thePlayer.posY - yPreEn;
                    double difZ = Minecraft.thePlayer.posZ - zPreEn;
                    double divider = step * i;
                    x = Minecraft.thePlayer.posX - difX * divider;
                    y = Minecraft.thePlayer.posY - difY * (up ? 1 : divider);
                    z = Minecraft.thePlayer.posZ - difZ * divider;
                }
                sendPacket(false, positionsBack, positions);
            } else {
                // if last teleport
                {
                    double difX = Minecraft.thePlayer.posX - xPreEn;
                    double difY = Minecraft.thePlayer.posY - yPreEn;
                    double difZ = Minecraft.thePlayer.posZ - zPreEn;
                    double divider = step * i;
                    x = Minecraft.thePlayer.posX - difX * divider;
                    y = Minecraft.thePlayer.posY - difY * (up ? 1 : divider);
                    z = Minecraft.thePlayer.posZ - difZ * divider;
                }
                sendPacket(false, positionsBack, positions);
                double xDist = x - xPreEn;
                double zDist = z - zPreEn;
                double yDist = y - dest.yCoord;
                double dist = Math.sqrt(xDist * xDist + zDist * zDist);
                if (dist > 4) {
                    x = xPreEn;
                    y = yPreEn;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                } else if (dist > 0.05 && up) {
                    x = xPreEn;
                    y = yPreEn;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                }
                if (Math.abs(yDist) < maxYTP) {
                    x = xPreEn;
                    y = dest.yCoord;
                    z = zPreEn;
                    sendPacket(false, positionsBack, positions);
                    // Attack / interact

                } else {
                    attack = false;
                }
            }
        }

        // Go back!
        for (int i = positions.size() - 2; i > -1; i--) {
            {
                x = positions.get(i).xCoord;
                y = positions.get(i).yCoord;
                z = positions.get(i).zCoord;
            }
            sendPacket(false, positionsBack, positions);
        }
        x = Minecraft.thePlayer.posX;
        y = Minecraft.thePlayer.posY;
        z = Minecraft.thePlayer.posZ;
        sendPacket(false, positionsBack, positions);
        if (!attack) {
            if (sneaking) {
                Minecraft.getNetHandler().getNetworkManager().sendPacket(
                        new C0BPacketEntityAction(Minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            }
            positions.clear();
            positionsBack.clear();
            return false;
        }
        if (sneaking) {
            Minecraft.getNetHandler().getNetworkManager()
                    .sendPacket(new C0BPacketEntityAction(Minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        }
        return true;
    }

    private static void attackInf(EntityLivingBase en) {
        Minecraft.thePlayer.swingItem();
        Minecraft.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(en, C02PacketUseEntity.Action.ATTACK));

        float sharpLevel = EnchantmentHelper.func_152377_a(Minecraft.thePlayer.getHeldItem(), en.getCreatureAttribute());
        boolean vanillaCrit = (Minecraft.thePlayer.fallDistance > 0.0F) && (!Minecraft.thePlayer.onGround)
                && (!Minecraft.thePlayer.isOnLadder()) && (!Minecraft.thePlayer.isInWater())
                && (!Minecraft.thePlayer.isPotionActive(Potion.blindness)) && (Minecraft.thePlayer.ridingEntity == null);
        if (sharpLevel > 0.0F) {
            Minecraft.thePlayer.onEnchantmentCritical(en);
        }
    }

    /**
     * Gets the World class, To use this, do zCore.w().***
     *
     * @return
     */

    public static void sendPacket(boolean goingBack, ArrayList<Vec3> positionsBack, ArrayList<Vec3> positions) {
        C04PacketPlayerPosition playerPacket = new C04PacketPlayerPosition(x, y, z, true);
        Minecraft.getNetHandler().getNetworkManager().sendPacket(playerPacket);
        if (goingBack) {
            positionsBack.add(new Vec3(x, y, z));
            return;
        }
        positions.add(new Vec3(x, y, z));
    }

    private final static float limitAngleChange(final float current, final float intended, final float maxChange) {
        float change = intended - current;
        if (change > maxChange)
            change = maxChange;
        else if (change < -maxChange)
            change = -maxChange;
        return current + change;
    }

    public static double normalizeAngle(double angle) {
        return (angle + 360) % 360;
    }

    public static float normalizeAngle(float angle) {
        return (angle + 360) % 360;
    }

    @SuppressWarnings("unused")
    public static MovingObjectPosition rayTracePos(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid,
                                                   boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        float[] rots = getFacePosRemote(vec32, vec31);
        float yaw = rots[0];
        double angleA = Math.toRadians(normalizeAngle(yaw));
        double angleB = Math.toRadians(normalizeAngle(yaw) + 180);
        double size = 2.1;
        double size2 = 2.1;
        Vec3 left = new Vec3(vec31.xCoord + Math.cos(angleA) * size, vec31.yCoord,
                vec31.zCoord + Math.sin(angleA) * size);
        Vec3 right = new Vec3(vec31.xCoord + Math.cos(angleB) * size, vec31.yCoord,
                vec31.zCoord + Math.sin(angleB) * size);
        Vec3 left2 = new Vec3(vec32.xCoord + Math.cos(angleA) * size, vec32.yCoord,
                vec32.zCoord + Math.sin(angleA) * size);
        Vec3 right2 = new Vec3(vec32.xCoord + Math.cos(angleB) * size, vec32.yCoord,
                vec32.zCoord + Math.sin(angleB) * size);
        Vec3 leftA = new Vec3(vec31.xCoord + Math.cos(angleA) * size2, vec31.yCoord,
                vec31.zCoord + Math.sin(angleA) * size2);
        Vec3 rightA = new Vec3(vec31.xCoord + Math.cos(angleB) * size2, vec31.yCoord,
                vec31.zCoord + Math.sin(angleB) * size2);
        Vec3 left2A = new Vec3(vec32.xCoord + Math.cos(angleA) * size2, vec32.yCoord,
                vec32.zCoord + Math.sin(angleA) * size2);
        Vec3 right2A = new Vec3(vec32.xCoord + Math.cos(angleB) * size2, vec32.yCoord,
                vec32.zCoord + Math.sin(angleB) * size2);
        if (false) {
            MovingObjectPosition trace2 = Minecraft.theWorld.rayTraceBlocks(vec31, vec32, stopOnLiquid,
                    ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
            return trace2;
        }
        // MovingObjectPosition trace4 = mc.theWorld.rayTraceBlocks(leftA,
        // left2A, stopOnLiquid, ignoreBlockWithoutBoundingBox,
        // returnLastUncollidableBlock);
        MovingObjectPosition trace1 = Minecraft.theWorld.rayTraceBlocks(left, left2, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace2 = Minecraft.theWorld.rayTraceBlocks(vec31, vec32, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace3 = Minecraft.theWorld.rayTraceBlocks(right, right2, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        // MovingObjectPosition trace5 = mc.theWorld.rayTraceBlocks(rightA,
        // right2A, stopOnLiquid, ignoreBlockWithoutBoundingBox,
        // returnLastUncollidableBlock);
        // positionsBack.add(rightA);
        // positionsBack.add(right2A);
        // positionsBack.add(leftA);
        // positionsBack.add(left2A);
        MovingObjectPosition trace4 = null;
        MovingObjectPosition trace5 = null;
        if (trace2 != null || trace1 != null || trace3 != null || trace4 != null || trace5 != null) {
            if (returnLastUncollidableBlock) {
                if (trace5 != null
                        && (getBlock(trace5.getBlockPos()).getMaterial() != Material.air || trace5.entityHit != null)) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace5;
                }
                if (trace4 != null
                        && (getBlock(trace4.getBlockPos()).getMaterial() != Material.air || trace4.entityHit != null)) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace4;
                }
                if (trace3 != null
                        && (getBlock(trace3.getBlockPos()).getMaterial() != Material.air || trace3.entityHit != null)) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace3;
                }
                if (trace1 != null
                        && (getBlock(trace1.getBlockPos()).getMaterial() != Material.air || trace1.entityHit != null)) {
                    // positions.add(BlockTools.getVec3(trace1.getBlockPos()));
                    return trace1;
                }
                if (trace2 != null
                        && (getBlock(trace2.getBlockPos()).getMaterial() != Material.air || trace2.entityHit != null)) {
                    // positions.add(BlockTools.getVec3(trace2.getBlockPos()));
                    return trace2;
                }
            } else {
                if (trace5 != null) {
                    return trace5;
                }
                if (trace4 != null) {
                    return trace4;
                }
                if (trace3 != null) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace3;
                }
                if (trace1 != null) {
                    // positions.add(BlockTools.getVec3(trace1.getBlockPos()));
                    return trace1;
                }
                if (trace2 != null) {
                    // positions.add(BlockTools.getVec3(trace2.getBlockPos()));
                    return trace2;
                }
            }
        }
        if (trace2 == null) {
            if (trace3 == null) {
                if (trace1 == null) {
                    if (trace5 == null) {
                        if (trace4 == null) {
                            return null;
                        }
                        return trace4;
                    }
                    return trace5;
                }
                return trace1;
            }
            return trace3;
        }
        return trace2;
    }

    public static boolean rayTraceWide(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid,
                                       boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        float yaw = getFacePosRemote(vec32, vec31)[0];
        yaw = normalizeAngle(yaw);
        yaw += 180;
        yaw = MathHelper.wrapAngleTo180_float(yaw);
        double angleA = Math.toRadians(yaw);
        double angleB = Math.toRadians(yaw + 180);
        double size = 2.1;
        double size2 = 2.1;
        Vec3 left = new Vec3(vec31.xCoord + Math.cos(angleA) * size, vec31.yCoord,
                vec31.zCoord + Math.sin(angleA) * size);
        Vec3 right = new Vec3(vec31.xCoord + Math.cos(angleB) * size, vec31.yCoord,
                vec31.zCoord + Math.sin(angleB) * size);
        Vec3 left2 = new Vec3(vec32.xCoord + Math.cos(angleA) * size, vec32.yCoord,
                vec32.zCoord + Math.sin(angleA) * size);
        Vec3 right2 = new Vec3(vec32.xCoord + Math.cos(angleB) * size, vec32.yCoord,
                vec32.zCoord + Math.sin(angleB) * size);
        Vec3 leftA = new Vec3(vec31.xCoord + Math.cos(angleA) * size2, vec31.yCoord,
                vec31.zCoord + Math.sin(angleA) * size2);
        Vec3 rightA = new Vec3(vec31.xCoord + Math.cos(angleB) * size2, vec31.yCoord,
                vec31.zCoord + Math.sin(angleB) * size2);
        Vec3 left2A = new Vec3(vec32.xCoord + Math.cos(angleA) * size2, vec32.yCoord,
                vec32.zCoord + Math.sin(angleA) * size2);
        Vec3 right2A = new Vec3(vec32.xCoord + Math.cos(angleB) * size2, vec32.yCoord,
                vec32.zCoord + Math.sin(angleB) * size2);
        // MovingObjectPosition trace4 = mc.theWorld.rayTraceBlocks(leftA,
        // left2A, stopOnLiquid, ignoreBlockWithoutBoundingBox,
        // returnLastUncollidableBlock);
        MovingObjectPosition trace1 = Minecraft.theWorld.rayTraceBlocks(left, left2, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace2 = Minecraft.theWorld.rayTraceBlocks(vec31, vec32, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace3 = Minecraft.theWorld.rayTraceBlocks(right, right2, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        // MovingObjectPosition trace5 = mc.theWorld.rayTraceBlocks(rightA,
        // right2A, stopOnLiquid, ignoreBlockWithoutBoundingBox,
        // returnLastUncollidableBlock);
        MovingObjectPosition trace4 = null;
        MovingObjectPosition trace5 = null;
        if (returnLastUncollidableBlock) {
            return (trace1 != null && getBlock(trace1.getBlockPos()).getMaterial() != Material.air)
                    || (trace2 != null && getBlock(trace2.getBlockPos()).getMaterial() != Material.air)
                    || (trace3 != null && getBlock(trace3.getBlockPos()).getMaterial() != Material.air)
                    || (trace4 != null && getBlock(trace4.getBlockPos()).getMaterial() != Material.air)
                    || (trace5 != null && getBlock(trace5.getBlockPos()).getMaterial() != Material.air);
        } else {
            return trace1 != null || trace2 != null || trace3 != null || trace5 != null || trace4 != null;
        }

    }

    public static WorldClient w() {
        return Minecraft.theWorld;
    }

    public static GameSettings gsettings() {
        return mc().gameSettings;
    }

    public static MovementInput movementInput() {
        return p().movementInput;
    }

    public static double x() {
        return p().posX;
    }

    public static void x(double x) {
        p().posX = x;
    }

    public static double y() {
        return p().posY;
    }

    public static void y(double y) {
        p().posY = y;
    }

    public static double z() {
        return p().posZ;
    }

    public static void z(double z) {
        p().posZ = z;
    }

    public static float yaw() {
        return p().rotationYaw;
    }

    public static void yaw(float yaw) {
        p().rotationYaw = yaw;
    }

    public static float pitch() {
        return p().rotationPitch;
    }

    public static void pitch(float pitch) {
        p().rotationPitch = pitch;
    }

    public static void tpRel(double x, double y, double z) {
        EntityPlayerSP player = Minecraft.thePlayer;
        player.setPosition(player.posX + x, player.posY + y, player.posZ + z);
    }

    public static void setSpeed(final MoveEvent event, final double speed) {
        double forward = MovementInput.moveForward;
        double strafe = MovementInput.moveStrafe;
        float yaw = yaw();
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f))
                    - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }

    /**
     * Updates The Rotating Shit For The Circle
     *
     * @return
     */
    public void updateRotating() {
        if (back) {
            if (shit > 0) {
                shit -= 2;
            }
        }
        if (shit < 360 && !back) {
            shit += 2;
        }
        if (shit <= 0) {
            ++this.shit2;
            if (this.shit2 > 10) {
                this.shit2 = 0;
                back = false;
            }
        }
        if (shit >= 360) {
            ++this.shit2;
            if (this.shit2 > 10) {
                this.shit2 = 0;
                back = true;
            }
        }
    }

    public static void drawCircleAnimated(final int x, final int y, final float radius, final float lineWidth,
                                          final int color) {
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(3);
        for (int i = 0; i <= shit; ++i) {
            GL11.glVertex2d(
                    (x) - (back ? Math.sin(i * 3.141526 / 180.0) * radius : 0)
                            + (back ? 0 : Math.sin(i * 3.141526 / 180.0) * radius),
                    y + Math.cos(i * 3.141526 / 180.0) * radius);
        }
        GL11.glEnd();
        GlStateManager.popMatrix();
    }

    public static void tpPacket(double x, double y, double z) {
        EntityPlayerSP player = Minecraft.thePlayer;
        player.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(player.posX + x, player.posY + y,
                player.posZ + z, player.onGround));
    }

    public float getDirection() {
        float yaw = zCore.p().rotationYawHead;
        float forward = zCore.p().moveForward;
        float strafe = zCore.p().moveStrafing;
        yaw += (forward < 0.0F ? 180 : 0);
        if (strafe < 0.0F) {
            yaw += (forward < 0.0F ? -45 : forward == 0.0F ? 90 : 45);
        }
        if (strafe > 0.0F) {
            yaw -= (forward < 0.0F ? -45 : forward == 0.0F ? 90 : 45);
        }
        return yaw * 0.017453292F;
    }

    public double getSpeed() {
        return Math.sqrt(MathUtils.square(zCore.p().motionX) + MathUtils.square(zCore.p().motionZ));
    }

    public void setSpeed(double speed) {
        zCore.p().motionX = (-MathHelper.sin(getDirection()) * speed);
        zCore.p().motionZ = (MathHelper.cos(getDirection()) * speed);
    }

    public static float[] getFacePosRemote(Vec3 src, Vec3 dest) {
        double diffX = dest.xCoord - src.xCoord;
        double diffY = dest.yCoord - (src.yCoord);
        double diffZ = dest.zCoord - src.zCoord;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }

    public void setY(double speed) {
        zCore.p().motionY = (-MathHelper.sin(getDirection()) * speed);
        zCore.p().chunkCoordY = (int) (-MathHelper.sin(getDirection()) * speed);
    }

    public static Block getBlock(BlockPos pos) {
        return Minecraft.theWorld.getBlockState(pos).getBlock();
    }

    public static InventoryPlayer getInventory() {
        return p().inventory;
    }

    public static FontRenderer getFontRenderer() {
        return Minecraft.fontRendererObj;
    }

    public static boolean doesMove() {
        return (zCore.mc().gameSettings.keyBindForward.pressed) || (zCore.mc().gameSettings.keyBindRight.pressed)
                || (zCore.mc().gameSettings.keyBindBack.pressed) || (zCore.mc().gameSettings.keyBindLeft.pressed);
    }

    /**
     * Used for custom ViTox-Sided chat messages.
     *
     * @return
     */
    public static void addChatMessage(String s) {
        p().addChatMessage(new ChatComponentText("\2478[\2477zCore\2478] \2477" + s));
    }

    public static void addErrorChatMessage(String msg) {
        p().addChatMessage(new ChatComponentText("\2478[\2477zCore\2478] \247cERROR: " + msg));
    }

    /**
     * Used for Lunas's addErrorChatMessage and addChatMessage
     */

    public static void addChatMessageP(String s) {
        p().addChatMessage(new ChatComponentText("\2478[\2477Luna\2478] \2477" + s));
    }

    public static void addBotMessage(String s) {
        p().addChatMessage(new ChatComponentText("\2478[\2477AntiBot\2478] \2477" + s));
    }

    public static void addNiceMessage(String s) {
        p().addChatMessage(new ChatComponentText("\2474>> \2477" + s));
    }

    public static void addPlayerCheckMessageP(String s) {
        p().addChatMessage(new ChatComponentText("" + s));
    }

    private static Map<Integer, Boolean> glCapMap = new HashMap();

    public static void setGLCap(int cap, boolean flag) {
        glCapMap.put(Integer.valueOf(cap), Boolean.valueOf(GL11.glGetBoolean(cap)));
        if (flag) {
            GL11.glEnable(cap);
        } else {
            GL11.glDisable(cap);
        }
    }

    public static void revertGLCap(int cap) {
        Boolean origCap = glCapMap.get(Integer.valueOf(cap));
        if (origCap != null) {
            if (origCap.booleanValue()) {
                GL11.glEnable(cap);
            } else {
                GL11.glDisable(cap);
            }
        }
    }

    public static void glEnable(int cap) {
        setGLCap(cap, true);
    }

    public static void glDisable(int cap) {
        setGLCap(cap, false);
    }

    public static void revertAllCaps() {
        for (Iterator localIterator = glCapMap.keySet().iterator(); localIterator.hasNext(); ) {
            int cap = ((Integer) localIterator.next()).intValue();
            revertGLCap(cap);
        }
    }

    public static void addErrorChatMessageP(String msg) {
        p().addChatMessage(new ChatComponentText("\2478[\2477Luna\2478] \247cERROR: \2477" + msg));
    }

    public static void faceEntity(Entity entity) {
        double posX = entity.posX - p().posX;
        double posZ = entity.posZ - p().posZ;
        double posY = entity.posY + entity.getEyeHeight() - p().posY + p().getEyeHeight();
        double helper = MathHelper.sqrt_double(posX * posX + posZ * posZ);
        float newYaw = (float) Math.toDegrees(-Math.atan(posX / posZ));
        float newPitch = (float) -Math.toDegrees(Math.atan(posY / helper));
        if ((posZ < 0.0D) && (posX < 0.0D)) {
            newYaw = (float) (90.0D + Math.toDegrees(Math.atan(posZ / posX)));
        } else if ((posZ < 0.0D) && (posX > 0.0D)) {
            newYaw = (float) (-90.0D + Math.toDegrees(Math.atan(posZ / posX)));
        }
        getSendQueue().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(newYaw, newPitch - 0.1F, p().onGround));
    }

    public static Session getSession() {
        return mc().getSession();
    }

    public static void updatePosition(double x, double y, double z) {
        sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, p().onGround));
    }

    public static int drawStringWithShadow(String string, float x, float y, int color) {
        return getFontRenderer().func_175065_a(string, x, y, color, true);
    }

    public static void useCPUUsage(int CPU) {
        int fpsdrain = 0;
        while (fpsdrain < CPU * 100) {
            drawStringWithShadow("CPU USAGE", Display.getX() + CPU, Display.getY() + CPU, 16514926);

            fpsdrain++;
            if (fpsdrain >= CPU * 100) {
                break;
            }
        }
    }

    public static Block getBlockUnderPlayer(EntityPlayer inPlayer) {
        return getBlock(new BlockPos(inPlayer.posX, inPlayer.posY - 1.0D, inPlayer.posZ));
    }

    public static void setSpeedM(float speed) {
        EntityPlayerSP player = Minecraft.thePlayer;
        double yaw = player.rotationYaw;
        boolean isMoving = (player.moveForward != 0.0F) || (player.moveStrafing != 0.0F);
        boolean isMovingForward = player.moveForward > 0.0F;
        boolean isMovingBackward = player.moveForward < 0.0F;
        boolean isMovingRight = player.moveStrafing > 0.0F;
        boolean isMovingLeft = player.moveStrafing < 0.0F;
        boolean isMovingSideways = (isMovingLeft) || (isMovingRight);
        boolean isMovingStraight = (isMovingForward) || (isMovingBackward);
        if (isMoving) {
            if ((isMovingForward) && (!isMovingSideways)) {
                yaw += 0.0D;
            } else if ((isMovingBackward) && (!isMovingSideways)) {
                yaw += 180.0D;
            } else if ((isMovingForward) && (isMovingLeft)) {
                yaw += 45.0D;
            } else if (isMovingForward) {
                yaw -= 45.0D;
            } else if ((!isMovingStraight) && (isMovingLeft)) {
                yaw += 90.0D;
            } else if ((!isMovingStraight) && (isMovingRight)) {
                yaw -= 90.0D;
            } else if ((isMovingBackward) && (isMovingLeft)) {
                yaw += 135.0D;
            } else if (isMovingBackward) {
                yaw -= 135.0D;
            }
            yaw = Math.toRadians(yaw);
            player.motionX = (-Math.sin(yaw) * speed);
            player.motionZ = (Math.cos(yaw) * speed);
        }
    }

    public static void packet(Packet packet) {
        Minecraft.getNetHandler().addToSendQueue(packet);
    }

    public static double rn = 1.5D;

    public static int Color() {
        int cxd = 0;
        cxd = (int) (cxd + rn);
        if (cxd > 50) {
            cxd = 0;
        }
        Color color = new Color(Color.HSBtoRGB((float) (Minecraft.thePlayer.ticksExisted / 60.0D
                + Math.sin(cxd / 60.0D * 1.5707963267948966D)) % 1.0F, 0.5882353F, 1.0F));
        int col = new Color(color.getRed(), color.getGreen(), color.getBlue(), 200).getRGB();
        return col;
    }

    public static void circleOutline(double x, double y, double radius, int color) {
        String volcano = "Thx Volcano for the Circle ESP :)";
        float red = (color >> 24 & 0xFF) / 255.0f;
        float green = (color >> 16 & 0xFF) / 255.0f;
        float blue = (color >> 8 & 0xFF) / 255.0f;
        float alpha = (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.pushMatrix();
        GlStateManager.func_179090_x();
        GlStateManager.enableBlend();
        GlStateManager.color(green, blue, alpha, red);
        GL11.glEnable(2848);
        GL11.glEnable(2881);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glBegin(3);
        for (int i = 0; i <= 360; ++i) {
            final double x2 = Math.sin(i * 3.141592653589793 / 180.0) * radius;
            final double y2 = Math.cos(i * 3.141592653589793 / 180.0) * radius;
            GL11.glVertex2d(x + x2, y + y2);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glDisable(2881);
        GlStateManager.func_179098_w();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static Color rainbow(float offset) {
        float hue = ((float) System.nanoTime() + offset) / 1.0E10F % 8.0F;
        long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, 1.0F, 1.001F)).intValue()),
                16);
        Color c = new Color((int) color);
        return new Color(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, c.getAlpha() / 255.0F);
    }

    public static void drawBorderedCircle(int x, int y, float radius, int outsideC, int insideC) {
        GL11.glEnable(3042);

        GL11.glDisable(3553);

        GL11.glBlendFunc(770, 771);

        GL11.glEnable(2848);

        GL11.glPushMatrix();

        float scale = 0.1F;

        GL11.glScalef(scale, scale, scale);

        x = (int) (x * (1.0F / scale));

        y = (int) (y * (1.0F / scale));

        radius *= 1.0F / scale;

        drawCircle(x, y, radius, insideC);

        drawUnfilledCircle(x, y, radius, 1.0F, outsideC);

        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);

        GL11.glPopMatrix();

        GL11.glEnable(3553);

        GL11.glDisable(3042);

        GL11.glDisable(2848);
    }

    public static void drawUnfilledCircle(int x, int y, float radius, float lineWidth, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;

        float red = (color >> 16 & 0xFF) / 255.0F;

        float green = (color >> 8 & 0xFF) / 255.0F;

        float blue = (color & 0xFF) / 255.0F;

        GL11.glColor4f(red, green, blue, alpha);

        GL11.glLineWidth(lineWidth);

        GL11.glEnable(2848);

        GL11.glBegin(2);
        for (int i = 0; i <= 360; i++) {
            GL11.glVertex2d(x + Math.sin(i * 3.141526D / 180.0D) * radius,
                    y + Math.cos(i * 3.141526D / 180.0D) * radius);
        }
        GL11.glEnd();

        GL11.glDisable(2848);
    }

    public static void drawCircle(int x, int y, float radius, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;

        float red = (color >> 16 & 0xFF) / 255.0F;

        float green = (color >> 8 & 0xFF) / 255.0F;

        float blue = (color & 0xFF) / 255.0F;

        GL11.glColor4f(red, green, blue, alpha);

        GL11.glBegin(9);
        for (int i = 0; i <= 360; i++) {
            GL11.glVertex2d(x + Math.sin(i * 3.141526D / 180.0D) * radius,
                    y + Math.cos(i * 3.141526D / 180.0D) * radius);
        }
        GL11.glEnd();
    }

    public static int getRainbow(int speed, int offset) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        return Color.getHSBColor(hue, 1f, 1f).getRGB();
    }

    private Rectangle2D getBounds(String text) {
        return this.theMetrics.getStringBounds(text, this.theGraphics);
    }

    public float getStringWidth(String str) {
        return (float) (getBounds(StringUtils.stripControlCodes(str)).getWidth() + this.extraSpacing) / 2.0F;
    }

    public float getStringHeight(String text) {
        return (float) getBounds(text).getHeight() / 2.0F;
    }

    public void drawCenteredStringWithShadow(String text, double x, double y, int color) {
        drawStringWithShadowTag(text, x - getStringWidth(text) / 2.0F, y - getStringHeight(text) / 2.0F, color);
    }

    public String stripControlCodes(String s) {
        return this.patternControlCode.matcher(s).replaceAll("");
    }

    public String stripUnsupported(String s) {
        return this.patternUnsupported.matcher(s).replaceAll("");
    }

    public void drawString(String text, double x, double y, FontType fontType, int color, int color2, boolean shadow) {
        text = stripUnsupported(text);

        GlStateManager.enableBlend();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);

        String text2 = stripControlCodes(text);
        boolean skip = false;
        switch (fontType) {
            case PLAIN:
                drawer(text2, x + 0.5D, y, color2, false);
                drawer(text2, x - 0.5D, y, color2, false);
                drawer(text2, x, y + 0.5D, color2, false);
                drawer(text2, x, y - 0.5D, color2, false);
                break;
            case OVERLAY_BLUR:
                drawer(text2, x + 0.4000000059604645D, y + 0.4000000059604645D, color2, false);
                break;
            case OUTLINE_THIN:
                drawer(text2, x + 1.0D, y + 1.0D, color2, false);
                break;
            case EMBOSS_TOP:
                if (this.dynamicTextureBlurred != null) {
                    drawer(text2, x + 0.5D, y + 0.5D, color2, true);
                }
                break;
            case SHADOW_THIN:
                if (this.dynamicTextureBlurred != null) {
                    skip = true;
                    drawer(text, x, y, color, false);
                    drawer(text2, x, y, color2, true);
                }
                break;
            case SHADOW_THICK:
                drawer(text2, x, y + 0.5D, color2, false);
                break;
            case SHADOW_BLURRED:
                drawer(text2, x, y - 0.5D, color2, false);
                break;
            case EMBOSS_BOTTOM:
                break;
        }
        if (shadow) {
            color2 = 0 >> 2 | color & 0xFF000000;
        }
        if (!skip) {
            drawer(text, x, y, color, false);
        }
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GL11.glHint(3155, 4352);
    }

    private void drawer(String text, double x, double y, int color, boolean useBlur) {
        x *= 2.0D;
        y *= 2.0D;
        GlStateManager.func_179098_w();
        if (useBlur) {
            GlStateManager.func_179144_i(this.dynamicTextureBlurred.getGlTextureId());
        } else {
            GlStateManager.func_179144_i(this.dynamicTexture.getGlTextureId());
        }
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, 255.0F);
        double startX = x;
        for (int i = 0; i < text.length(); i++) {
            if ((text.charAt(i) == '\247') && (i + 1 < text.length())) {
                char oneMore = Character.toLowerCase(text.charAt(i + 1));
                if (oneMore == 'n') {
                    y += this.theMetrics.getAscent() + 2;
                    x = startX;
                }
                int colorCode = "0123456789abcdefklmnoru".indexOf(oneMore);
                if ((colorCode < 16) && (colorCode > -1)) {
                    int newColor = Minecraft.fontRendererObj.colorCode[colorCode];
                    GlStateManager.color((newColor >> 16) / 255.0F, (newColor >> 8 & 0xFF) / 255.0F,
                            (newColor & 0xFF) / 255.0F, 255.0F);
                } else if (oneMore == 'f') {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 255.0F);
                } else if (oneMore == 'r') {
                    GlStateManager.color(red, green, blue, 255.0F);
                } else if (oneMore == 'u') {
                    int newColor = -10859859;
                    GlStateManager.color((newColor >> 16 & 0xFF) / 255.0F, (newColor >> 8 & 0xFF) / 255.0F,
                            (newColor & 0xFF) / 255.0F, 255.0F);
                }
                i++;
            } else {
                try {
                    char c = text.charAt(i);
                    drawChar(c, x, y);
                    x += getStringWidth(Character.toString(c)) * 2.0F;
                } catch (ArrayIndexOutOfBoundsException indexException) {
                    indexException.printStackTrace();
                }
            }
        }
    }

    private void drawChar(char character, double x, double y) {
        if ((character >= this.startChar) && (character <= this.endChar)) {
            Rectangle2D bounds = this.theMetrics.getStringBounds(Character.toString(character), this.theGraphics);
            drawTexturedModalRect(x, y - this.theMetrics.getAscent() / 2, this.xPos[(character - this.startChar)],
                    this.yPos[(character - this.startChar)], bounds.getWidth(), getHeight());
        }
    }

    private void drawTexturedModalRect(double x, double y, double u, double v, double width, double height) {
        double scale = 1.0D / 512.0D;
        GL11.glBegin(7);
        GL11.glTexCoord2d(u * scale, v * scale);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2d(u * scale, (v + height) * scale);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2d((u + width) * scale, (v + height) * scale);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2d((u + width) * scale, v * scale);
        GL11.glVertex2d(x + width, y);
        GL11.glEnd();
    }

    public static void checkSetupFBO() {
        Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
        if (fbo != null) {
            if (fbo.depthBuffer > -1) {
                EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);

                int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();

                EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);

                EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getMinecraft().displayWidth,
                        Minecraft.getMinecraft().displayHeight);

                EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);

                EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);

                fbo.depthBuffer = -1;
            }
        }
    }

    public final float getHeight() {
        return this.theMetrics.getHeight() + this.theMetrics.getDescent() + 3;
    }

    public void drawStringWithShadowTag(String text, double d, double e, int color) {
        GlStateManager.enableBlend();
        drawString(text, d, e, FontType.SHADOW_THIN, color, 0, true);
        GlStateManager.disableBlend();
    }

    public static void toFwd(double speed) {
        float f = Minecraft.thePlayer.rotationYaw * 0.017453292F;
        Minecraft.thePlayer.motionX -= MathHelper.sin(f) * speed;
        Minecraft.thePlayer.motionZ += MathHelper.cos(f) * speed;
    }

    public static void outlineFive() {
        GL11.glPolygonOffset(1.0F, 2000000.0F);
        GL11.glDisable(10754);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(2960);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glEnable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        GL11.glPopAttrib();
    }

}
