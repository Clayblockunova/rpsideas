package com.kamefrede.rpsideas.items;

import com.kamefrede.rpsideas.RPSIdeas;
import com.kamefrede.rpsideas.gui.GuiHandler;
import com.kamefrede.rpsideas.items.base.RPSItem;
import com.kamefrede.rpsideas.util.helpers.SpellHelpers;
import com.kamefrede.rpsideas.util.libs.LibItemNames;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.ISpellContainer;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.ItemSpellDrive;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemFlashRing extends RPSItem implements ISpellContainer {
    public ItemFlashRing() {
        super(LibItemNames.FLASH_RING);
        setMaxStackSize(1);

        addPropertyOverride(new ResourceLocation(RPSIdeas.MODID, "active"), (stack, world, ent) -> containsSpell(stack) ? 1f : 0f);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (!player.isSneaking() && containsSpell(held)) {
            PlayerDataHandler.PlayerData data = SpellHelpers.getPlayerData(player);
            ItemStack cad = PsiAPI.getPlayerCAD(player);
            boolean did = false;
            if (data != null && !cad.isEmpty()) {
                did = ItemCAD.cast(world, player, data, held, cad, (int) (getCostModifier(held) * 15), 25, 0.5f, null);
            }
            return new ActionResult<>(did ? EnumActionResult.SUCCESS : EnumActionResult.PASS, held);
        } else if (player.isSneaking()) {
            if (world.isRemote) {
                player.openGui(RPSIdeas.INSTANCE, GuiHandler.GUI_FLASH_RING, world, 0, 0, 0);

            }
            return new ActionResult<>(EnumActionResult.SUCCESS, held);
        }

        return new ActionResult<>(EnumActionResult.PASS, held);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag mistake) {
        String cost = TextFormatting.AQUA + I18n.format("rpsideas.misc.cost") + " " + TextFormatting.GRAY + (int) (getCostModifier(stack) * 100) + "%";
        tooltip.add(cost);
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        if (containsSpell(stack)) {
            Spell spell = getSpell(stack);

            if (spell != null && !spell.name.isEmpty()) return spell.name;
        }

        return super.getItemStackDisplayName(stack);
    }

    @Override
    public Spell getSpell(ItemStack stack) {
        return ItemSpellDrive.getSpell(stack);
    }

    @Override
    public void setSpell(EntityPlayer entityPlayer, ItemStack stack, Spell spell) {
        ItemSpellDrive.setSpell(stack, spell);
    }

    @Override
    public boolean containsSpell(ItemStack stack) {
        Spell s = getSpell(stack);
        return s != null && !s.grid.isEmpty();
    }

    @Override
    public void castSpell(ItemStack stack, SpellContext context) {
        context.cspell.safeExecute(context);
    }

    @Override
    public double getCostModifier(ItemStack stack) {
        return 5d;
    }

    @Override
    public boolean isCADOnlyContainer(ItemStack stack) {
        return false;
    }

    @Override
    public boolean requiresSneakForSpellSet(ItemStack stack) {
        return true; //Because the Flash Ring has shift-click behavior, this will never fire.
    }

    @Override
    public boolean hasContainerItem() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getContainerItem(@Nonnull ItemStack stack) {
        return stack.copy();
    }
}