package io.github.racoondog.anvildisplay.mixin;

import io.github.racoondog.anvildisplay.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onGetTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info, List<Text> list) {
        ItemStack stack = (ItemStack) (Object) this;
        Item item = stack.getItem();
        NbtCompound tag = stack.getNbt();
        if (tag == null) return;
        boolean isBook = item.equals(Items.ENCHANTED_BOOK);
        if (isBook && Util.isBookEmpty(tag)) return;
        if (item.isEnchantable(stack) || isBook) {
            int repairCost = tag.contains("RepairCost") ? tag.getInt("RepairCost") : 0;
            int uses = Util.costToUses(repairCost);
            NbtList enchantmentList = isBook ? tag.getList("StoredEnchantments", 10) : tag.getList("Enchantments", 10);
            if (enchantmentList.isEmpty()) return;
            list.add(new LiteralText("%sAnvil Uses: %s%d%s.".formatted(Formatting.GRAY, Util.getFormatting(enchantmentList, uses, isBook), uses, Formatting.GRAY)));
            list.add(new LiteralText("%sBase Cost: %s%d%s.".formatted(Formatting.GRAY, Util.getFormatting(enchantmentList, uses, isBook), isBook ? Util.getBaseCost(enchantmentList) + repairCost : repairCost, Formatting.GRAY)));
        }
    }
}
