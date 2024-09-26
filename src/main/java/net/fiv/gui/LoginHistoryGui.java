package net.fiv.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public class LoginHistoryGui extends SimpleGui {

    public LoginHistoryGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);


    }
}
