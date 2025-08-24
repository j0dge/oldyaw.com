package opm.luftwaffe.features.modules.HUD;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import opm.luftwaffe.features.Feature;
import net.minecraft.entity.player.EntityPlayer;
import opm.luftwaffe.features.command.Command;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import java.util.ArrayList;

public class govno extends Module {

    public govno() {
        super("govno", "govno", Module.Category.HUD, true, false, false);
    }
}