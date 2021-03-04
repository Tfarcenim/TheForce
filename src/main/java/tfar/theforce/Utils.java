package tfar.theforce;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class Utils {

    public static Entity getGrabbedEntity(PlayerEntity player) {
        CompoundNBT nbt = player.getPersistentData();
        CompoundNBT stored = nbt.getCompound(TheForce.MODID);
        if (stored.hasUniqueId("uuid")) {
            UUID uuid = stored.getUniqueId("uuid");
            return ((ServerWorld) player.world).getEntityByUuid(uuid);
        }
        return null;
    }
}
