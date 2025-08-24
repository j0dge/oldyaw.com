package opm.luftwaffe.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import opm.luftwaffe.api.event.events.PacketEvent;
import opm.luftwaffe.features.command.Command;
import opm.luftwaffe.features.modules.Module;
import opm.luftwaffe.features.setting.Setting;
import opm.luftwaffe.api.util.MathUtil;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoBait extends Module {
    public static AutoBait INSTANCE;
    public Setting<String> targetPlayer = register(new Setting("Target", "indianscammer", "bait niggas"));

    public AutoBait() {
        super("AutoBait", "Automatically responds to targeted player", Category.COMBAT, true, false, false);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat)event.getPacket();
            String rawMessage = packet.getChatComponent().getUnformattedText();
            String Msg = ChatFormatting.stripFormatting(rawMessage).toLowerCase();

            if (Msg.contains("<" + this.targetPlayer.getValue().toLowerCase() + ">")) {
                Command.sendSilentMessage("Baiting target player...");

                if (Msg.contains("i'm not") || Msg.contains("im not")) {
                    sendMsg(
                            "no?",
                            "yes?",
                            "nah LOLOLOLOL",
                            "yes",
                            "no",
                            "COPE!!!!",
                            "you are"
                    );
                }
                else if (Msg.contains(" nn")) {
                    sendMsg(
                            "compare youtube views",
                            "lets compare botnet users LOL",
                            "LETS COMPARE RATS LELELEL",
                            "compare azura.lol uid?",
                            "no opium?",
                            "who ARE YOU?!?!?",
                            "You do not own somahax LMAO.",
                            "compare opium uid?",
                            "you're talking to me? LOL",
                            "u talking to me?",
                            "no opium no speak nn",
                            "doick said ur unkown LEL!",
                            "team doick overall",
                            "CRYSTALOPIUM DOESNT KNOW YOU LEL",
                            "Project more LEL",
                            "larping like you are a big namer yourself LOLOLOLOL"
                    );
                }
                else if (Msg.contains(" iq?") || Msg.contains("what is your iq") || Msg.contains("what is ur iq")) {
                    mc.player.sendChatMessage(String.valueOf(MathUtil.random(200, 250)));
                }
                else if (Msg.contains("your iq") || Msg.contains("ur iq")) {
                    mc.player.sendChatMessage("you're*");
                }
                else if (Msg.contains(mc.player.getName())) {
                    sendMsg(
                            "who me?",
                            "crystaldoickum?"
                    );
                }
                else if (Msg.contains("you are") || Msg.contains("youre") || Msg.contains("you're")) {
                    sendMsg(
                            "who me?",
                            "opium? or me"
                    );
                }
                else if (Msg.contains("you ")) {
                    sendMsg(
                            "who me?",
                            "doickopium?"
                    );
                }
                else if (Msg.contains("main?") || Msg.contains("main")) {
                    sendMsg(
                            "2 NEW 2 KNOW MY MAIN",
                            mc.player.getName(),
                            "my main is doickopium",
                            "LOOOOOOOOL im more known than jqq"
                    );
                }
                else if (Msg.contains("im the")) {
                    mc.player.sendChatMessage("no?");
                }
                else if (Msg.contains("unfunny") || Msg.contains("who laughed")) {
                    mc.player.sendChatMessage("i laughed");
                }
                else if (Msg.contains("opium")) {
                    mc.player.sendChatMessage("opium is so good we love opium");
                }
                else if (Msg.contains("i win")) {
                    sendMsg(
                            "erm no?",
                            "my palms are on my forhead",
                            "I got second hand embarrassment from that",
                            "nice cope haha",
                            "im white, i take care of my self, i go to work & im NOT fat, whos the real winner?",
                            "cope nn",
                            "i win YOU LOSE.",
                            "when? LOL"
                    );
                }
                else {
                    sendMsg(
                            "yep",
                            "LELEL KEEP TALKING NN DOG",
                            "erm, no?",
                            "noone knows you bro ur unknown as shit LOL!",
                            "ARENT YOU LIKE BINNED ASF?",
                            "totemfull dog talking LELEL",
                            "crystalopium doesnt know you tho LOL!!!",
                            "XDXD ur bad bro wtff",
                            "LOL LOL SAY THAT WHEN YOU D!E IN A SW4T",
                            "DOICKSWAG IS SWATTING YOU IN THE VC RN XD",
                            "ok benjamin",
                            "uhh ok?",
                            "STFU NN",
                            "who are you again?",
                            "LELELE soma doesnt know this guy btw",
                            "Didnt you get ratted by us? LOLLLLLLL",
                            "lets go rat 4 rat",
                            "bark rn like a dog",
                            "LOOOL IQ?",
                            "YEAH UR GETTING SW4TT3D TN",
                            "watch out for the sw4t",
                            "LOOOL HARMLESS DOG PIPING UP 2 THE KING OF CRYSTAL PVP",
                            "the thing is that you are still unknown asf",
                            "XDXDXD Who are you again?",
                            "bro is not him....",
                            "NOONE KNOWS THIS NN XD",
                            "LEL IM THE #1 CRYSTALPVPER XD"
                    );
                }
            }
        }
    }

    private void sendMsg(String... responses) {
        if (responses.length == 0) return;
        String response = responses[(int) MathUtil.random(0, responses.length - 1)];

        mc.player.sendChatMessage(response);
    }

    @Override
    public String getDisplayInfo() {
        return targetPlayer.getValue();
    }
}