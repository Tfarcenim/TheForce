package tfar.theforce;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TheForce.MODID)
public class TheForce {
    // Directly reference a log4j logger.

    public static final String MODID = "theforce";

    private static final Logger LOGGER = LogManager.getLogger();

    public TheForce() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::playerTick);

        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(Client::clientSetup);
            MinecraftForge.EVENT_BUS.addListener(Client::keyPress);
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.registerMessages(MODID);
    }

    private void playerTick(TickEvent.PlayerTickEvent e) {
        if (!e.player.world.isRemote) {
            PlayerEntity player = e.player;
            Entity forceHeld = Utils.getGrabbedEntity(e.player);
            if (forceHeld != null) {
                double lastXPos = player.lastTickPosX;
                double lastYPos = player.lastTickPosY;
                double lastZPos = player.lastTickPosZ;

                RayTraceResult result = player.pick(5,0,true);

                BlockPos pos = ((BlockRayTraceResult)result).getPos();

                double XPos = pos.getX();
                double YPos = pos.getY();
                double ZPos = pos.getZ();

                if (forceHeld instanceof MobEntity) {
                    ((MobEntity)forceHeld).goalSelector.disableFlag(Goal.Flag.MOVE);
                }

                float f = 6;

                double d0 = (XPos - forceHeld.getPosX()) / (double)f;
                double d1 = (YPos - forceHeld.getPosY()) / (double)f;
                double d2 = (ZPos - forceHeld.getPosZ()) / (double)f;
                forceHeld.setMotion(forceHeld.getMotion().add(Math.copySign(d0 * d0 * 0.4D, d0), Math.copySign(d1 * d1 * 0.4D, d1), Math.copySign(d2 * d2 * 0.4D, d2)));


                if (forceHeld instanceof MobEntity) {
                    ((MobEntity)forceHeld).goalSelector.enableFlag(Goal.Flag.MOVE);
                }
            }
        }
    }

    /**
     * Gets the block or object that is being moused over.
     */
    public static Entity getMouseOver(PlayerEntity player) {
        double d0 = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
        Vector3d vector3d = player.getEyePosition(0);
        double d1 = d0;
        d1 = d1 * d1;

        Vector3d vector3d1 = player.getLook(1.0F);
        Vector3d vector3d2 = vector3d.add(vector3d1.x * d0, vector3d1.y * d0, vector3d1.z * d0);
        float f = 1.0F;
        AxisAlignedBB axisalignedbb = player.getBoundingBox().expand(vector3d1.scale(d0)).grow(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(player, vector3d, vector3d2, axisalignedbb,
                (entity) -> !entity.isSpectator() && entity.canBeCollidedWith(), d1);
        if (entityraytraceresult != null) {
            return entityraytraceresult.getEntity();
        }
        return null;
    }

public static class Client {
    public static final KeyBinding FORCE_GRAB = new KeyBinding("theforce.force_grab", GLFW.GLFW_KEY_O, "category.theforce");

    public static void clientSetup(final FMLClientSetupEvent blockRegistryEvent) {
        ClientRegistry.registerKeyBinding(FORCE_GRAB);
    }

    public static void keyPress(InputEvent.KeyInputEvent e) {
        while (FORCE_GRAB.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new C2SForceGrabPacket());
        }
    }

}
}
