package opm.luftwaffe.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.play.client.CPacketChatMessage;
import opm.luftwaffe.Luftwaffe;
import opm.luftwaffe.features.command.Command;
import opm.luftwaffe.api.manager.FriendManager;
import opm.luftwaffe.features.modules.HUD.HUD;
import opm.luftwaffe.features.modules.misc.AutoGG;

public class FriendCommand
        extends Command {
    public FriendCommand() {
        super("friend", new String[]{"<add/del/name/clear>", "<name>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (Luftwaffe.friendManager.getFriends().isEmpty()) {
                FriendCommand.sendMessage("Friend list empty D:.");
            } else {
                String f = "Friends: ";
                for (FriendManager.Friend friend : Luftwaffe.friendManager.getFriends()) {
                    try {
                        f = f + friend.getUsername() + ", ";
                    } catch (Exception exception) {
                    }
                }
                FriendCommand.sendMessage(f);
            }
            return;
        }
        if (commands.length == 2) {
            switch (commands[0]) {
                case "reset": {
                    Luftwaffe.friendManager.onLoad();
                    FriendCommand.sendMessage("Friends got reset.");
                    return;
                }
            }
            FriendCommand.sendMessage(commands[0] + (Luftwaffe.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
            return;
        }
        if (commands.length >= 2) {
            switch (commands[0]) {
                case "add": {
                    Luftwaffe.friendManager.addFriend(commands[1]);
                    FriendCommand.sendMessage(ChatFormatting.GREEN + commands[1] + " has been friended");
                    if (HUD.getInstance().friendMsg.getValue().booleanValue()) {
                        FriendCommand.mc.player.connection.sendPacket(new CPacketChatMessage("/w " + commands[1] + " I just have you added thanks to luftwaffe.xyz!"));
                    }
                    return;
                }
                case "del": {
                    Luftwaffe.friendManager.removeFriend(commands[1]);
                    FriendCommand.sendMessage(ChatFormatting.RED + commands[1] + " has been unfriended");
                    return;
                }
            }
            FriendCommand.sendMessage("Unknown Command, try friend add/del (name)");
        }
    }
}

