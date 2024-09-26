package net.fiv.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.fiv.BorukvaInventoryBackup;
import net.fiv.data_base.entities.LogoutTable;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class LogoutHistoryGui extends SimpleGui {

    public static final Logger LOGGER = LoggerFactory.getLogger(BorukvaInventoryBackup.MOD_ID);

    public LogoutHistoryGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
    }
}
