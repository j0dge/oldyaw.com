package opm.luftwaffe;

import me.luftwaffe.BuildConstants;
import opm.luftwaffe.api.manager.*;
import opm.luftwaffe.api.util.discord.DiscordManager;
import opm.luftwaffe.api.util.IconUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.InputStream;
import java.nio.ByteBuffer;

@Mod(modid = "luftwaffe.xyz", name = "luftwaffe.xyz", version = "4.0.7")
public class Luftwaffe {
    public static final String MODID = "luftwaffe.xyz";
    public static final String MODNAME = "luftwaffe.xyz";
    public static final String MODVER = "4.0.7";
    public static final String MODNAME1 = "luftwaffe";
    public static final String MODNAME2 = ".xyz";
    public static final String GITHASH = BuildConstants.GIT_HASH;
    public static final String GITREVISION = BuildConstants.GIT_REVISION;
    public static final Logger LOGGER = LogManager.getLogger("luftwaffe.xyz");
    public static TimerManager timerManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static HoleManager holeManager;
    public static InventoryManager inventoryManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    public static ChatManager chatManager;
    public static DiscordManager discordManager;
    @Mod.Instance
    public static Luftwaffe INSTANCE;
    private static boolean unloaded;

    static {
        unloaded = false;
    }

    public static void load() {
        LOGGER.info("\n\nLoading luftwaffe.xyz");
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        rotationManager = new RotationManager();
        packetManager = new PacketManager();
        eventManager = new EventManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        serverManager = new ServerManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        positionManager = new PositionManager();
        configManager = new ConfigManager();
        holeManager = new HoleManager();
        timerManager = new TimerManager();
        chatManager = new ChatManager();
        LOGGER.info("Managers loaded.");
        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        textManager.init(true);
        initDRPC();
        moduleManager.onLoad();
        Luftwaffe.setWindowIcon();
        LOGGER.info("luftwaffe.xyz successfully loaded!\n");
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading luftwaffe.xyz");
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        Luftwaffe.onUnload();
        eventManager = null;
        friendManager = null;
        speedManager = null;
        holeManager = null;
        positionManager = null;
        rotationManager = null;
        configManager = null;
        commandManager = null;
        colorManager = null;
        serverManager = null;
        fileManager = null;
        potionManager = null;
        inventoryManager = null;
        moduleManager = null;
        textManager = null;
        chatManager = null;
        LOGGER.info("luftwaffe.xyz unloaded!\n");
    }

    public static void reload() {
        Luftwaffe.unload(false);
        Luftwaffe.load();
    }

    public static void setWindowIcon() {
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (InputStream inputStream16x = Minecraft.class.getResourceAsStream("/luftwaffe/assets/luftwaffe-16x.png");
                 InputStream inputStream32x = Minecraft.class.getResourceAsStream("/luftwaffe/assets/luftwaffe-32x.png")) {
                ByteBuffer[] icons = new ByteBuffer[]{IconUtil.INSTANCE.readImageToBuffer(inputStream16x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x)};
                Display.setIcon(icons);
            } catch (Exception e) {
                Luftwaffe.LOGGER.error("Couldn't set Windows Icon", e);
            }
        }
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(Luftwaffe.configManager.config.replaceFirst("luftwaffe/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("I got hacked 6 times this will be 7 - p0sixsprwn");
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        String title = MODID + " | " + MODVER + " | " +
                net.minecraft.client.Minecraft.getMinecraft().getVersion() + " | " +
                net.minecraft.client.Minecraft.getMinecraft().getSession().getUsername();
        org.lwjgl.opengl.Display.setTitle(title);
        LOGGER.info("Initializing luftwaffe...");
        Display.setTitle("卐luftwaffe.xyz卐");
        Luftwaffe.load();
        LOGGER.info("luftwaffe initialized successfully!");
    }

    private static void initDRPC() {
        discordManager = DiscordManager.getInstance();
        discordManager.start(true);
        discordManager.setDetails("favorite dallas opp");
        discordManager.setState(MODVER + "+" + GITHASH + "+" + GITREVISION);
        discordManager.setLargeImage("luftwaffe", "nazi ahhh client");
        discordManager.setSmallImage("luftwaffe", "luftwaffe");
        discordManager.setStartTimestampToNow();
    }
}

