package org.intenses.insanitymod.classChoose;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.intenses.insanitymod.classChoose.ItemGroup;
import org.intenses.insanitymod.Insanitymod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.mojang.math.Vector3f;

import static org.intenses.insanitymod.QoL.GlobalTumblers.GIVE_ARMOR_ON_CLASS_CHOOSE;


public class GUI extends Screen {

    private final Minecraft mc = Minecraft.getInstance();
    private static final int MAX_BUTTONS = 6;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("insanitymod", "textures/gui/class_choose_background.png");
    private float rotationYaw = 0f;
    private float rotationPitch = 0f;
    private boolean dragging = false;
    private double lastMouseX;
    private double lastMouseY;
    private List<ResourceLocation> races = OriginsStuff.getAvailableOrigins().stream()
            .filter(Objects::nonNull)
            .toList();

    private VisibilityButton sorcererButton;
    private final int RIGHT = 2;
    private final int LEFT = 1;
    private final int SORCERER = 3;

    private static final List<String> CLASSES = ItemGroup.getClasses();

    private static final List<Item> DefaultItems = ItemGroup.getDefaultItems();
    private static final List<Item> SorcSpells = ItemGroup.getSpells();
    private List<Item> getItemsForClass() {
        return ItemGroup.getClassItems(currClassIndex);
    }
    private List<Item> getClassArmor(){
        return ItemGroup.getClassArmor(currClassIndex);
    }

    private int currClassIndex = 0;
    private Component currentClassName = Component.empty();
    private Component currentRaceName = Component.empty();
    private Component currentRaceDesc = Component.empty();
    private Component currentClassDesc = Component.empty();
    private int currentRaceIndex = 0;

    private final List<IndexCheckBox> leftButtons = new ArrayList<>();
    private final List<IndexCheckBox> rightButtons = new ArrayList<>();
    private final List<IndexCheckBox> sorcererItems = new ArrayList<>();

    private int selectedLeftIndex = -1;
    private int selectedRightIndex = -1;
    private int selectedSpellIndex = -1;
    private Item leftItem;
    private Item rightItem;
    private Item spell;

    private int centerX() {
        return this.width / 2;
    }

    private static final int TITLE_Y = 40;
    private static final int SUBTITLE_Y = 50;

    private static final int INFO_BOX_W = 180;
    private static final int INFO_BOX_PAD = 8;
    private static final int CLASS_BOX_H = 90;
    private static final int RACE_BOX_H = 120;
    private static final int CLASS_BOX_Y = 50;
    private static final int CHECKBOXES_Y = 70;
    private static final int RACE_BOX_Y = CLASS_BOX_Y + CLASS_BOX_H + 10;
    private static final int CHECKBOX_MARGIN_LEFT = 50;

    protected GUI(Component pTitle) {
        super(pTitle);
    }

    private Component getCurrentClassName(int index) {
        String key = CLASSES.get(index);
        return Component.translatable(key);
    }

    private Component getCurrentClassDescription(int index) {
        String key = CLASSES.get(index) + ".desc";
        return Component.translatable(key);
    }





        @Override
        protected void init() {
            super.init();
            leftButtons.clear();
            rightButtons.clear();
            sorcererItems.clear();
            this.clearWidgets();
            int sorcX = this.width / 2 - 32;
            sorcererButton = sorcItemsButton(sorcX,280);
            int centerXName = centerX();
            int leftX = CHECKBOX_MARGIN_LEFT;

            addLeftCheckboxes();
            addRightCheckboxes();
            addSorcCheckboxes();

            int doneBtnWidth = 80;
            int classBtnWidth = 80;
            int btnGap = 20;

            int doneX = this.width / 2 - doneBtnWidth / 2;
            int INTENT = 10;
            int buttonY = this.height - INTENT * 3;

            int classLeftX = doneX - btnGap - classBtnWidth;
            int classRightX = doneX + doneBtnWidth + btnGap;

            this.addRenderableWidget(addClassLeft(classLeftX, buttonY));
            this.addRenderableWidget(addDoneButton(doneX, buttonY));
            this.addRenderableWidget(addClassRight(classRightX, buttonY));

            int raceBtnWidth = 40;
            int raceBtnGap = 20;

            int raceLeftX = centerXName - raceBtnWidth - raceBtnGap / 2;
            int raceRightX = centerXName + raceBtnGap / 2;

            this.addRenderableWidget(addRaceLeft(raceLeftX, INTENT));
            this.addRenderableWidget(addRaceRight(raceRightX, INTENT));

            for (int i = 0; i<races.size();i++){
                System.out.println("[DEBUG]" + races.get(i).toString() + " index= " + i);
            }

            int barWidth = 150;



            int barX = this.width / 2 - barWidth / 2;
            int x1 = barX;


            int x2 = x1+barWidth-64;

            this.addRenderableWidget(classItemsButton(x1,250));
            this.addRenderableWidget(defaultItemsButton(x2,250));
            this.addRenderableWidget(sorcererButton);

            races = OriginsStuff.getAvailableOrigins().stream()
                    .filter(Objects::nonNull)
                    .filter(rl -> {
                        String name = OriginsStuff.getOriginName(rl).toString().trim().toLowerCase();
                        return !name.isEmpty() && !name.equals("empty");
                    })
                    .toList();

            updateRaceLabel();
            updateLabel();
            if (sorcererButton != null) {sorcererButton.visible=false;}
            assert mc.player != null;
            updatePreviewArmor(mc.player);
            for (int i = 0; i<races.size();i++){ //Start race
                if (OriginsStuff.getOriginName(races.get(i)).equals("Human")){
                    currentRaceIndex = i;
                    updateRaceLabel();
                }
            }
        }

        private void updateLabel() {
            this.currentClassName = Component.literal("Class: ").append(getCurrentClassName(currClassIndex));
            this.currentClassDesc = getCurrentClassDescription(currClassIndex);
        }

    private void procSorc(){
        if (!isMage()){
            VisibilitySorc(false);
            sorcererButton.setVisible(false);
            sorcererButton.setActive(false);
            if (selectedSpellIndex >= 0 && selectedSpellIndex < sorcererItems.size()) {
                sorcererItems.get(selectedSpellIndex).forceSetSelected(false);
            }
            selectedSpellIndex = -1;
            spell = null;
        } else {
            sorcererButton.setActive(true);
            sorcererButton.setVisible(true);
        }
    }

        private void addLeftCheckboxes(){
            for (int i = 0; i < MAX_BUTTONS; i++) {
                int y = CHECKBOXES_Y + i * 28;
                IndexCheckBox cbLeft = new IndexCheckBox(
                        CHECKBOX_MARGIN_LEFT, y, 20, 20,
                        DefaultItems.get(i).getName(DefaultItems.get(i).getDefaultInstance()),
                        false, i, LEFT, this, DefaultItems.get(i)
                );
                leftButtons.add(cbLeft);
                this.addRenderableWidget(cbLeft);
            }
            VisibilityLeft(false);
        }

        private void addRightCheckboxes(){
            for (int i = 0; i < MAX_BUTTONS; i++) {
                int y = CHECKBOXES_Y + i * 28;
                IndexCheckBox cbRight = new IndexCheckBox(
                        CHECKBOX_MARGIN_LEFT, y, 20, 20,
                        getItemsForClass().get(i).getName(getItemsForClass().get(i).getDefaultInstance()),
                        false, i, RIGHT, this, getItemsForClass().get(i)
                );
                rightButtons.add(cbRight);
                this.addRenderableWidget(cbRight);
            }
            VisibilityRight(false);
        }
        private void addSorcCheckboxes(){
            for (int i = 0; i < MAX_BUTTONS; i++) {
                int y = CHECKBOXES_Y + i * 28;
                IndexCheckBox sorcCb = new IndexCheckBox(
                        CHECKBOX_MARGIN_LEFT, y, 20, 20,
                        SorcSpells.get(i).getName(SorcSpells.get(i).getDefaultInstance()),
                        false, i, SORCERER, this, SorcSpells.get(i)
                );
                sorcererItems.add(sorcCb);
                this.addRenderableWidget(sorcCb);
            }
            VisibilitySorc(false);
        }

    private void VisibilityLeft(boolean set){
        for (IndexCheckBox cb : leftButtons){
            cb.setVisible(set);
            cb.setActive(set);
        }
    }

    private void VisibilityRight(boolean set){
        for (IndexCheckBox cb : rightButtons){
            cb.setVisible(set);
            cb.setActive(set);
        }
    }

    private void VisibilitySorc(boolean set){
        for (IndexCheckBox cb : sorcererItems){
            cb.setVisible(set);
            cb.setActive(set);
        }
    }

    public void chooseOneGroup(int id){
        switch(id){
            case(1):
                VisibilityLeft(true);
                VisibilityRight(false);
                VisibilitySorc(false);
                break;
            case(2):
                VisibilityLeft(false);
                VisibilityRight(true);
                VisibilitySorc(false);
                break;
            case(3):
                VisibilityLeft(false);
                VisibilityRight(false);
                VisibilitySorc(true);
                break;

        }
    }

    private void giveItems(net.minecraft.world.entity.player.Player player) {
        if (rightItem != null) player.addItem(rightItem.getDefaultInstance());
        if (leftItem != null) player.addItem(leftItem.getDefaultInstance());
        if (spell != null) player.addItem((spell.getDefaultInstance()));
        if (GIVE_ARMOR_ON_CLASS_CHOOSE) {
            giveArmorToPlayer(player);
        }
    }
    private ItemStack createStackWithDamage(Item item, int damage) {
        ItemStack stack = new ItemStack(item);
        if (stack.isDamageableItem()) {
            int maxDamage = stack.getMaxDamage();
            int safeDamage = Math.min(damage, maxDamage);
            stack.setDamageValue(safeDamage);
        }
        return stack;
    }

    private void giveArmorToPlayer(Player player) {
        if (mc.player == null) return;

        List<Item> armor = getClassArmor();

        player.setItemSlot(EquipmentSlot.HEAD, createStackWithDamage(armor.get(0),0));  // шлем
        player.setItemSlot(EquipmentSlot.CHEST, createStackWithDamage(armor.get(1),0)); // нагрудник
        player.setItemSlot(EquipmentSlot.LEGS, createStackWithDamage(armor.get(2),0));  // штаны
        player.setItemSlot(EquipmentSlot.FEET, createStackWithDamage(armor.get(3),0));  // ботинки
    }

    public void onDone() {
        assert mc.player != null;
        mc.player.closeContainer();
        takeOutArmor(mc.player);
        giveItems(mc.player);
        Insanitymod.NETWORK.sendToServer(new SetOriginPacket(races.get(currentRaceIndex)));
    }

    public void classToLeft() {
        currClassIndex = wrapIndex(currClassIndex, -1, CLASSES.size());
        updateRightButtons();
        updateLabel();
        procSorc();
        assert mc.player != null;
        updatePreviewArmor(mc.player);

    }

    private boolean isMage(){
        return CLASSES.get(currClassIndex).equals("insanity.class.sorcerer");
    }

    public void classToRight() {
        currClassIndex = wrapIndex(currClassIndex, 1, CLASSES.size());
        updateRightButtons();
        updateLabel();
        procSorc();
        assert mc.player != null;
        updatePreviewArmor(mc.player);

    }

    public void onLeftCheckboxPressed(int index, IndexCheckBox checkbox) {
        if (selectedLeftIndex == index && checkbox.selected()) {
            checkbox.forceSetSelected(false);
            leftItem = null;
            selectedLeftIndex = -1;
        } else {
            selectedLeftIndex = index;
            deselectAllExceptOne(selectedLeftIndex, LEFT);
            leftItem = DefaultItems.get(index);
            checkbox.forceSetSelected(true);
        }
    }

    public void onRightCheckboxPressed(int index, IndexCheckBox checkbox) {
        if (selectedRightIndex == index && checkbox.selected()) {
            checkbox.forceSetSelected(false);
            rightItem = null;
            selectedRightIndex = -1;
        } else {
            selectedRightIndex = index;
            deselectAllExceptOne(selectedRightIndex, RIGHT);
            rightItem = getItemsForClass().get(index);
            checkbox.forceSetSelected(true);
        }
    }

    public void onSorcererCBPress(int index, IndexCheckBox checkBox){
        if (selectedSpellIndex == index && checkBox.selected()) {
            checkBox.forceSetSelected(false);
            spell = null;
            selectedSpellIndex = -1;
        } else {
            selectedSpellIndex = index;
            deselectAllExceptOne(selectedSpellIndex, SORCERER);
            spell = SorcSpells.get(index);
            checkBox.forceSetSelected(true);
        }
    }

    public void deselectAllExceptOne(int index, int column) {
        if (index >= MAX_BUTTONS) return;
        if (column > 3 || column < 1) return;

        if (column == LEFT) {
            for (int i = 0; i < leftButtons.size(); i++) {
                if (i != index) {
                    leftButtons.get(i).forceSetSelected(false);
                }
            }
        } else if (column == RIGHT) {
            for (int i = 0; i < rightButtons.size(); i++) {
                if (i != index) {
                    rightButtons.get(i).forceSetSelected(false);
                }
            }
        } else {
            for (int i = 0; i < sorcererItems.size(); i++) {
                if (i != index) {
                    sorcererItems.get(i).forceSetSelected(false);
                }
            }

        }
    }

    private void updateRightButtons() {
        List<Item> rightItems = getItemsForClass();
        for (int i = 0; i < MAX_BUTTONS; i++) {
            rightButtons.get(i).setMessage(rightItems.get(i).getName(rightItems.get(i).getDefaultInstance()));
            rightButtons.get(i).setItem(rightItems.get(i));
        }
    }


    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        net.minecraft.world.entity.player.Player player = mc.player;
        if (player != null) {
            player.invulnerableTime = 0;
            player.setDeltaMovement(0, 0, 0);
            player.fallDistance = 0;
        }
    }

    public static int wrapIndex(int currentIndex, int change, int size) {
        int newIndex = (currentIndex + change) % size;
        if (newIndex < 0) newIndex += size;
        return newIndex;
    }

    private VisibilityButton defaultItemsButton(int x, int y){
        return new VisibilityButton(x,y,64,20,Component.literal("Default Items"),1,this);
    }

    private VisibilityButton classItemsButton(int x, int y){
        return new VisibilityButton(x,y,64,20,Component.literal("Class Items"),2,this);
    }

    private VisibilityButton sorcItemsButton(int x, int y){
        return new VisibilityButton(x,y,64,20,Component.literal("Spells"),3,this);
    }

    private CustomButton addDoneButton(int x, int y) {
        return new CustomButton(x, y, 80, 20, Component.literal("Done"), btn -> {
            onDone();
        }, 3, this);
    }

    private CustomButton addClassLeft(int x, int y) {
        return new CustomButton(x, y, 80, 20, Component.literal("Left"), btn -> {
            classToLeft();
        }, 1, this);
    }

    private CustomButton addClassRight(int x, int y) {
        return new CustomButton(x, y, 80, 20, Component.literal("Right"), btn -> {
            classToRight();
        }, 2, this);
    }

    private raceButton addRaceLeft(int x, int y) {
        return new raceButton(x, y, 40, 15, Component.literal("Previous race"), btn -> {
            changeRaceLeft();
        }, 1, this);
    }

    private raceButton addRaceRight(int x, int y) {
        return new raceButton(x, y, 40, 15, Component.literal("Next race"), btn -> {
            changeRaceRight();
        }, 2, this);
    }

    public static void openGui() {
        Minecraft mc = Minecraft.getInstance();
        System.out.println("[DEBUG] OpenedGUI");
        mc.setScreen(new GUI(Component.literal("Choose class")));
    }

    public void changeRaceLeft() {
        int step = -1;
        int nextIndex = wrapIndex(currentRaceIndex, step, races.size());
        if (OriginsStuff.getOriginName(races.get(nextIndex)).equals("empty")) {
            nextIndex = wrapIndex(currentRaceIndex, step * 2, races.size());
        }
        currentRaceIndex = nextIndex;
        updateRaceLabel();
    }

    public void changeRaceRight() {
        int step = 1;
        int nextIndex = wrapIndex(currentRaceIndex, step, races.size());

        if (OriginsStuff.getOriginName(races.get(nextIndex)).equals("empty")) {
            nextIndex = wrapIndex(currentRaceIndex, step * 2, races.size());
        }
        currentRaceIndex = nextIndex;
        updateRaceLabel();
    }

    private void updateRaceLabel() {
        ResourceLocation currentRace = races.get(currentRaceIndex);
        Component raceName = Component.literal(OriginsStuff.getOriginName(currentRace));
        Component raceDesc = Component.literal(OriginsStuff.getOriginDescription(currentRace));
        System.out.println("[DEBUG] race name = " + raceName.toString());
        System.out.println("[DEBUG] race index = " + currentRaceIndex);
        this.currentRaceName = Component.literal("Race: ").append(raceName);
        this.currentRaceDesc = raceDesc;
    }

    private void drawInfoBox(PoseStack stack, String text, int x, int y, int height) {
        fill(stack, x - 1, y - 1, x + INFO_BOX_W + 1, y + height + 1, 0xFF000000);
        fill(stack, x, y, x + INFO_BOX_W, y + height, 0x77222222);

        int usableWidth = INFO_BOX_W - INFO_BOX_PAD * 2;
        int usableHeight = height - INFO_BOX_PAD * 2;
        List<FormattedText> lines = font.getSplitter().splitLines(Component.literal(text), usableWidth, Style.EMPTY);

        int lineH = font.lineHeight;
        int maxLines = usableHeight / lineH;
        for (int i = 0; i < Math.min(lines.size(), maxLines); i++) {
            font.draw(stack,
                    Language.getInstance().getVisualOrder(lines.get(i)),
                    x + INFO_BOX_PAD,
                    y + INFO_BOX_PAD + i * lineH,
                    0xAAAAAA);
        }
    }
    private void updatePreviewArmor(AbstractClientPlayer player) {
        List<Item> armor = getClassArmor();
        player.getInventory().armor.set(3, createStackWithDamage(armor.get(0),0)); // HEAD
        player.getInventory().armor.set(2, createStackWithDamage(armor.get(1),0)); // CHEST
        player.getInventory().armor.set(1, createStackWithDamage(armor.get(2),0)); // LEGS
        player.getInventory().armor.set(0, createStackWithDamage(armor.get(3),0)); // FEET
    }

    private void takeOutArmor(AbstractClientPlayer player) {
        player.getInventory().armor.set(3, new ItemStack(Items.AIR));
        player.getInventory().armor.set(2, new ItemStack(Items.AIR));
        player.getInventory().armor.set(1, new ItemStack(Items.AIR));
        player.getInventory().armor.set(0, new ItemStack(Items.AIR));
    }
//    private void renderEntityPreview(PoseStack poseStack, int x, int y, float scale, float partialTicks) {
//        Minecraft mc = Minecraft.getInstance();
//        AbstractClientPlayer player = (AbstractClientPlayer) mc.player;
//        if (player == null) return;
//
//        PlayerModel<AbstractClientPlayer> playerModel = new PlayerModel<>(
//                mc.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
//        playerModel.setupAnim(player, 0, 0, 0, 0, 0);
//        playerModel.crouching = false;
//
//        HumanoidModel<AbstractClientPlayer> innerArmor = new HumanoidModel<>(
//                mc.getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
//        HumanoidModel<AbstractClientPlayer> outerArmor = new HumanoidModel<>(
//                mc.getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
//
//        PlayerRenderer renderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
//
//        HumanoidArmorLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>, HumanoidModel<AbstractClientPlayer>> armorLayer =
//                new HumanoidArmorLayer<>(renderer, innerArmor, outerArmor);
//
//        poseStack.pushPose();
//        poseStack.translate(x, y, 20.0D); // ближе = больше
//        poseStack.scale(35.0f, 35.0f, 35.0f); // большой scale
//        poseStack.mulPose(Vector3f.XP.rotationDegrees(0)); // Повернуть вверх
//        poseStack.mulPose(Vector3f.YP.rotationDegrees(180)); // Повернуть лицом
//
//        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
//        ResourceLocation skin = player.getSkinTextureLocation();
//
//        playerModel.renderToBuffer(poseStack,
//                buffer.getBuffer(RenderType.entityCutoutNoCull(skin)),
//                15728880, OverlayTexture.NO_OVERLAY,
//                1.0F, 1.0F, 1.0F, 1.0F);
//
//        armorLayer.render(poseStack,
//                buffer,
//                15728880,
//                player,
//                0.0F, 0.0F, partialTicks,
//                0.0F, 0.0F, 0.0F);
//
//        buffer.endBatch();
//        poseStack.popPose();
//    }


    private void renderEntityPreview(PoseStack poseStack, int x, int y, float scale, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = (AbstractClientPlayer) mc.player;
        if (player == null) return;

        poseStack.pushPose();

        // Поднимаем модель на экране
        poseStack.translate(x, y - 150, 100.0D);
        poseStack.scale(scale, scale, scale);

        poseStack.translate(0.0D, 0.9D, 0.0D);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationYaw + 180F)); // лицом вперед
        poseStack.mulPose(Vector3f.XP.rotationDegrees(rotationPitch));      // наклон мышкой
        poseStack.translate(0.0D, -0.9D, 0.0D);

        EntityModelSet modelSet = mc.getEntityModels();
        PlayerModel<AbstractClientPlayer> playerModel = new PlayerModel<>(modelSet.bakeLayer(ModelLayers.PLAYER), false);
        HumanoidModel<AbstractClientPlayer> innerArmor = new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        HumanoidModel<AbstractClientPlayer> outerArmor = new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));

        innerArmor.head.xScale = 0.73f;
        innerArmor.head.yScale = 0.73f;
        innerArmor.head.zScale = 0.73f;

        outerArmor.head.xScale = 0.73f;
        outerArmor.head.yScale = 0.73f;
        outerArmor.head.zScale = 0.73f;

        player.yBodyRot = 0;
        player.yBodyRotO = 0;
        player.yHeadRot = 0;
        player.yHeadRotO = 0;
        playerModel.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        ResourceLocation skin = player.getSkinTextureLocation();

        playerModel.head.xScale = 0.73f;
        playerModel.head.yScale = 0.73f;
        playerModel.head.zScale = 0.73f;
        playerModel.hat.xScale = 0.73f;
        playerModel.hat.yScale = 0.73f;
        playerModel.hat.zScale = 0.73f;

        playerModel.renderToBuffer(poseStack,
                buffer.getBuffer(RenderType.entityTranslucent(skin)),
                15728880,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);

        PlayerRenderer renderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
        HumanoidArmorLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>, HumanoidModel<AbstractClientPlayer>> armorLayer =
                new HumanoidArmorLayer<>(renderer, innerArmor, outerArmor);

        armorLayer.render(poseStack,
                buffer,
                15728880,
                player,
                0.0F, 0.0F, partialTicks,
                0.0F, 0.0F, 0.0F);

        buffer.endBatch();
        poseStack.popPose();
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(0);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.disableBlend();

        int modelBgWidth = 150;
        int modelBgHeight = 180;
        int modelBgX = this.width / 2 - modelBgWidth / 2;
        int modelBgY = 60;

        fill(poseStack, modelBgX, modelBgY, modelBgX + modelBgWidth, modelBgY + modelBgHeight, 0xAA000000);

        drawCenteredString(poseStack, font, currentClassName.getString(), centerX(), TITLE_Y, 0xFFFFFF);
        drawCenteredString(poseStack, font, currentRaceName.getString(), centerX(), SUBTITLE_Y, 0xCCCCCC);


        drawInfoBox(poseStack, currentClassDesc.getString(), this.width - INFO_BOX_W - INFO_BOX_PAD, CLASS_BOX_Y, CLASS_BOX_H);
        drawInfoBox(poseStack, currentRaceDesc.getString(), this.width - INFO_BOX_W - INFO_BOX_PAD, RACE_BOX_Y, RACE_BOX_H);


        renderEntityPreview(poseStack, this.width / 2, this.height / 2, 115f, partialTicks);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int modelBgWidth = 150;
        int modelBgHeight = 180;
        int modelBgX = this.width / 2 - modelBgWidth / 2;
        int modelBgY = 60;

        boolean inBounds = mouseX >= modelBgX && mouseX <= modelBgX + modelBgWidth &&
                mouseY >= modelBgY && mouseY <= modelBgY + modelBgHeight;

        if (button == 0 && inBounds) {
            dragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && button == 0) {
            float deltaX = (float) (mouseX - lastMouseX);
            float deltaY = (float) (mouseY - lastMouseY);
            rotationYaw += deltaX * 0.7f;
            rotationPitch += deltaY * 0.7f;
            rotationPitch = Mth.clamp(rotationPitch, -90, 90);
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

}
