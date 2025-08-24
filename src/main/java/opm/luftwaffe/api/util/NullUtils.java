package opm.luftwaffe.api.util;

public class NullUtils implements Util{
    public static boolean nullCheck() {
        return NullUtils.mc.player == null || NullUtils.mc.world == null || NullUtils.mc.playerController == null;
    }
}
