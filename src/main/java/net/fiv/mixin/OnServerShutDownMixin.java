package net.fiv.mixin;

import net.fiv.actor.DatabaseManagerActor;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class OnServerShutDownMixin {
    @Inject(method="shutdown", at = @At("TAIL"))
    private void onServerShutDownMixin(CallbackInfo ci) throws Exception {
        DatabaseManagerActor.getBorukvaInventoryBackupDB().closeDbConnection();
    }
}
