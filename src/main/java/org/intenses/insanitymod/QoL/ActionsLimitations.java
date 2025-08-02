package org.intenses.insanitymod.QoL;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.intenses.insanitymod.Insanitymod;


@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ActionsLimitations {
    private static final TagKey<Item> GLIDERS = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(Insanitymod.MOD_ID, "gliders"));


//    public static void applyCrawlLimitation(ServerPlayer player,boolean permission){
//        Limitation.get(player, new Limitation.ID(Insanitymod.MOD_ID, "crawl"))
//                .enable()
//                .permit(Crawl.class, permission)
//                .apply();
//    }
//
//    public static void applyDodgeLimitation(ServerPlayer player,boolean permission){
//        Limitation.get(player, new Limitation.ID(Insanitymod.MOD_ID, "dodge"))
//                .enable()
//                .permit(Dodge.class, permission)
//                .apply();
//    }
//
//    public static void applyFastRunLimitation(ServerPlayer player,boolean permission){
//        Limitation.get(player, new Limitation.ID(Insanitymod.MOD_ID, "fastrun"))
//                .enable()
//                .permit(FastRun.class, permission)
//                .apply();
//    }
//
//    public static void applySlideLimitation(ServerPlayer player,boolean permission){
//        Limitation.get(player, new Limitation.ID(Insanitymod.MOD_ID, "slide"))
//                .enable()
//                .permit(Slide.class, permission)
//                .apply();
//    }
//
//
//
//    public static boolean isParagliding(ServerPlayer player){
//        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
//        ItemStack gliderStack = CuriosApi.getCuriosHelper()
//                .findFirstCurio(player, Insanitymod.SPECIAL_ITEM.get())
//                .filter(result -> "glider".equals(result.slotContext().identifier()))
//                .map(SlotResult::stack)
//                .orElse(ItemStack.EMPTY);
//        return (ParagliderItem.isItemParagliding(chestplate) || ParagliderItem.isItemParagliding(gliderStack));
//    }
//
//    Player Aplayer;
//    Action Aaction;
//
//    private void setAplayer(Player player){
//        Aplayer=player;
//    }
//
//    private void setAaction(Action action){
//        Aaction = action;
//    }
//
//    @SubscribeEvent
//    public void action(ParCoolActionEvent event){
//        setAplayer(event.getPlayer());
//        setAaction(event.getAction());
//
//    }
//
//    @SubscribeEvent
//    public void onPlayerTick(TickEvent.PlayerTickEvent event){
//        ServerPlayer player = (ServerPlayer) event.player;
//        applyCrawlLimitation(player, !isParagliding(player));
//        if (Aplayer == player && Aaction.isDoing()){
//            ED2ClientStorage.setCooldown(10);
//        } else ED2ClientStorage.setCooldown(0);
//    }
}
