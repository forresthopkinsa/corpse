package de.maxhenkel.corpse.gui;

import de.maxhenkel.corpse.Main;
import de.maxhenkel.corpse.entities.EntityCorpse;
import de.maxhenkel.corpse.net.MessageSwitchPage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GUICorpse extends GuiContainer {

    private static final ResourceLocation CORPSE_GUI_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/gui_corpse.png");
    private static final int FONT_COLOR = 4210752;

    private IInventory playerInventory;
    private EntityCorpse corpse;

    private GuiButton previous;
    private GuiButton next;

    private int page;

    public GUICorpse(IInventory playerInventory, EntityCorpse corpse) {
        super(new ContainerCorpse(playerInventory, corpse));
        this.playerInventory = playerInventory;
        this.corpse = corpse;
        this.page = 0;

        xSize = 176;
        ySize = 248;
    }

    @Override
    protected void initGui() {
        super.initGui();

        buttons.clear();
        int left = (width - xSize) / 2;
        int padding = 7;
        int buttonWidth = 50;
        int buttonHeight = 20;
        previous = addButton(new GuiButton(0, left + padding, guiTop + 149 - buttonHeight, buttonWidth, buttonHeight, new TextComponentTranslation("button.previous").getFormattedText()) {
            @Override
            public void onClick(double x, double y) {
                super.onClick(x, y);
                page--;
                if (page < 0) {
                    page = 0;
                }
                Main.SIMPLE_CHANNEL.sendToServer(new MessageSwitchPage(page));
            }
        });
        next = addButton(new GuiButton(1, left + xSize - buttonWidth - padding, guiTop + 149 - buttonHeight, buttonWidth, buttonHeight, new TextComponentTranslation("button.next").getFormattedText()) {
            @Override
            public void onClick(double x, double y) {
                super.onClick(x, y);
                page++;
                if (page >= getPages()) {
                    page = getPages() - 1;
                }
                Main.SIMPLE_CHANNEL.sendToServer(new MessageSwitchPage(page));
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (page <= 0) {
            previous.enabled = false;
        } else {
            previous.enabled = true;
        }

        if (page >= getPages() - 1) {
            next.enabled = false;
        } else {
            next.enabled = true;
        }
    }

    private int getPages() {
        return corpse.getSizeInventory() / 54;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        fontRenderer.drawString(corpse.getDisplayName().getFormattedText(), 7, 7, FONT_COLOR);
        fontRenderer.drawString(playerInventory.getDisplayName().getFormattedText(), 7, ySize - 96 + 2, FONT_COLOR);

        String pageName = new TextComponentTranslation("gui.corpse.page", page + 1, getPages()).getFormattedText();
        int pageWidth = fontRenderer.getStringWidth(pageName);
        fontRenderer.drawString(pageName, xSize / 2 - pageWidth / 2, ySize - 113, FONT_COLOR);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(CORPSE_GUI_TEXTURE);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}