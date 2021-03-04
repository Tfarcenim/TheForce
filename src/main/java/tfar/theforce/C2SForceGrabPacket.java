package tfar.theforce;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class C2SForceGrabPacket {

  public C2SForceGrabPacket(){}

  public C2SForceGrabPacket(PacketBuffer buffer){}

  //decode

  public void encode(PacketBuffer buf) {
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = ctx.get().getSender();
      if (player == null) return;
      ctx.get().enqueueWork(  ()-> forceGrab(player));
      ctx.get().setPacketHandled(true);
    }


    private void forceGrab(PlayerEntity player) {
      if (Utils.getGrabbedEntity(player) != null) {
        Utils.getGrabbedEntity(player).setNoGravity(false);
        CompoundNBT nbt = player.getPersistentData();
        CompoundNBT stored = nbt.getCompound(TheForce.MODID);
        stored.remove("uuid");
      } else {
        Entity target = TheForce.getMouseOver(player);
        if (target != null) {
          CompoundNBT nbt = player.getPersistentData();
          CompoundNBT stored = new CompoundNBT();
          stored.putUniqueId("uuid", target.getUniqueID());
          nbt.put(TheForce.MODID, stored);
          target.setNoGravity(true);
          System.out.println("Grabbed!");
        }
      }
    }
}

