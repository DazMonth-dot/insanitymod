package org.intenses.insanitymod.mixins;

import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.client.widget.*;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.intenses.insanitymod.music.ModSounds;
import org.spongepowered.asm.mixin.*;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.intenses.insanitymod.Insanitymod.LOGGER;

@Mixin(value = SkillTreeScreen.class, remap = false)
public abstract class SkillTreeScreenMixin extends Screen {


    protected SkillTreeScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Shadow public abstract Optional<GuiEventListener> getWidgetAt(double mouseX, double mouseY);

    @Shadow @Nullable public abstract SkillButton getSkillAt(double mouseX, double mouseY);

    @Shadow protected abstract void skillButtonPressed(SkillButton button);

    @Shadow private String search;

    @Shadow private boolean showStats;

    @Shadow protected abstract void updateSearch();

    @Shadow private Button buyButton;

    @Shadow private Label pointsInfo;

    @Shadow protected abstract void cancelLearnSkills();

    @Shadow @Final public List<ResourceLocation> newlyLearnedSkills;

    @Shadow protected abstract void learnSkill(PassiveSkill skill);

    @Shadow @Final private Map<ResourceLocation, SkillButton> skillButtons;

    @Shadow protected abstract int getCurrentLevel();

    @Shadow protected abstract boolean canBuySkillPoint(int currentLevel);

    @Shadow @Nonnull protected abstract LocalPlayer getPlayer();

    @Shadow protected abstract void highlightSkillsThatCanBeLearned();

    @Shadow public abstract void addSkillButtons();

    @Shadow protected int maxScrollY;

    @Shadow protected int maxScrollX;

    @Shadow private ScrollableComponentList statsInfo;

    @Shadow protected abstract List<Component> getMergedSkillBonusesTooltips();

    @Shadow private boolean firstInitDone;

    @Shadow protected abstract void firstInit();

    @Shadow private ProgressBar progressBar;

    @Shadow protected abstract void addTopWidgets();

    @Shadow private boolean showProgressInNumbers;

    @Shadow private int prevMouseX;

    @Shadow private int prevMouseY;

    @Shadow @Final private PassiveSkillTree skillTree;

    @Unique
    private void insanitymod$playBack() {
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.playSound(ModSounds.BACKWARD.get(), 1.0F, 1.0F);
    }

    @Unique
    private void insanitymod$playConf(){
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.playSound(ModSounds.CONFIRM.get(), 1.0F, 1.0F);
    }

    @Unique
    private void insanitymod$playForward(){
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.playSound(ModSounds.FORWARD.get(), 1.0F, 1.0F);
    }

    @Unique
    private void insanitymod$playSPL(){
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.playSound(ModSounds.SKILL_POINT_LEARN.get(), 1.0F, 1.0F);
    }

    @Unique
    private Map<SkillButton, Boolean> buttonStates = new HashMap<>();




    /**
     * @author DazMonth
     * @reason Adding sounds for buttons
     */
    @Overwrite
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Optional<GuiEventListener> widget = this.getWidgetAt(mouseX, mouseY);
        if (widget.isPresent()) {
            return ((GuiEventListener)widget.get()).mouseClicked(mouseX, mouseY, button);
        } else {
            SkillButton skill = this.getSkillAt(mouseX, mouseY);
            if (skill == null) {
                return false;
            } else if (button == 0) {
                this.skillButtonPressed(skill);
                if (skill.active){
                    insanitymod$playForward();
                } else {insanitymod$playBack();}
                return true;
            } else if (button == 1) {
                ClientConfig.toggleFavoriteSkill(skill.skill);
                if (skill.active){
                    insanitymod$playForward();
                } else {insanitymod$playBack();}
                return true;
            } else {
                return false;
            }
        }
    }
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        Optional<GuiEventListener> widget = this.getWidgetAt(mouseX, mouseY);
//        if (widget.isPresent()) {
//            return widget.get().mouseClicked(mouseX, mouseY, button);
//        }
//
//        SkillButton skill = this.getSkillAt(mouseX, mouseY);
//        if (skill == null) {
//            return false;
//        }
//
//
//        buttonStates.putIfAbsent(skill, false);
//
//        if (button == 0 || button == 1) {
//            LOGGER.info(button + " " + buttonStates.get(skill));
//            LOGGER.info(button + " " + skill.skillLearned);
//            LOGGER.info(button + " " + skill.selected);
//            LOGGER.info(button + " " + skill.active);
//
//
//            this.skillButtonPressed(skill);
//
//            if (!buttonStates.get(skill)) {
//                buttonStates.put(skill, true);
//                insanitymod$playBack();
//            } else {
//                buttonStates.put(skill, false);
//                insanitymod$playForward();
//            }
//
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * @author DazMonth
     * @reason Adding sounds to the buttons
     */

    @Overwrite
    private void buySkillPoint() {
        int currentLevel = this.getCurrentLevel();
        if (this.canBuySkillPoint(currentLevel)) {
            insanitymod$playSPL();
            int cost = SkillTreeClientData.getSkillPointCost(currentLevel);
            NetworkDispatcher.network_channel.sendToServer(new GainSkillPointMessage());
            this.getPlayer().giveExperiencePoints(-cost);
        }
    }

    /**
     * @author DazMonth
     * @reason Adding sounds to the buttons
     */
    @Overwrite
    private void confirmLearnSkills() {
        this.newlyLearnedSkills.forEach((id) -> this.learnSkill(((SkillButton)this.skillButtons.get(id)).skill));
        this.newlyLearnedSkills.clear();
        insanitymod$playConf();
    }


}




