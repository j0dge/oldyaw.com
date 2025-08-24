package opm.luftwaffe.features.modules.movement;

import opm.luftwaffe.features.modules.Module;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ArrowBooster extends Module {
    private boolean isBoosted = false;
    private int boostTicks = 0;
    private final int BOOST_DURATION = 20; // 20 тиков = 1 секунда

    public ArrowBooster() {super("ArrowBooster", "Run Nigga Run", Category.MOVEMENT, true, false, false);}


    @SubscribeEvent
    public void onDamageTaken(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            // Проверяем, движется ли игрок (WASD зажаты)
            if (player.moveForward != 0 || player.moveStrafing != 0) {
                if (!isBoosted) {
                    // Увеличиваем скорость в 2 раза
                    player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                            .setBaseValue(0.2); // 0.1 - стандарт, 0.2 - x2

                    isBoosted = true;
                    boostTicks = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof EntityPlayer) {
            if (isBoosted) {
                boostTicks++;

                // Сбрасываем через 1 секунду
                if (boostTicks >= BOOST_DURATION) {
                    event.player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                            .setBaseValue(0.1); // Возвращаем стандарт
                    isBoosted = false;
                }
            }
        }
    }
}