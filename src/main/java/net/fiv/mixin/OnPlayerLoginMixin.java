package net.fiv.mixin;

import akka.actor.ActorRef;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.actor.BActorMessages;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class OnPlayerLoginMixin {
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity player,
                                    ConnectedClientData clientData, CallbackInfo ci){
        System.out.println("PlayerConnect: "+player.getInventory().main);
        BorukvaInventoryBackup.getDatabaseManagerActor().tell(
                new BActorMessages.SavePlayerDataOnPlayerConnect(player), ActorRef.noSender());
    }
}
