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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow protected abstract int getHideFlags();

    @Inject(method = "getTooltip", at = @At("TAIL"))
    private void onGetTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (Util.isHideEnchants(this.getHideFlags())) return; //Don't show info if enchants hideFlag is on

        Item item = stack.getItem();
        NbtCompound tag = stack.getNbt();

        if (tag == null) return;
        boolean isBook = item.equals(Items.ENCHANTED_BOOK);
        if (isBook && Util.isBookEmpty(tag)) return;
        if (!item.isEnchantable(stack) && !isBook) return;

        NbtList enchantmentList = isBook ? tag.getList("StoredEnchantments", 10) : tag.getList("Enchantments", 10);
        if (enchantmentList.isEmpty()) return;
        int repairCost = tag.contains("RepairCost") ? tag.getInt("RepairCost") : 0;
        int uses = Util.costToUses(repairCost);

        List<Text> list = cir.getReturnValue();
        list.add(Text.translatable("anvil-display.anvil_uses", Formatting.WHITE.toString() + uses + Formatting.GRAY).formatted(Formatting.GRAY));
        list.add(Text.translatable("anvil-display.base_cost", Formatting.WHITE.toString() + (Util.getBaseCost(enchantmentList, isBook) + repairCost) + Formatting.GRAY).formatted(Formatting.GRAY));
    }
}
