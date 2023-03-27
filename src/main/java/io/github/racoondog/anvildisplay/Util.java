package io.github.racoondog.anvildisplay;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;

import java.util.Optional;

public class Util {
    public static int costToUses(int cost) {
        return log2(cost + 1);
    }

    public static boolean isBookEmpty(NbtCompound tag) {
        return !tag.contains("StoredEnchantments");
    }

    public static int getRarity(Enchantment enchantment, boolean book) {
        int rarity = switch (enchantment.getRarity()) {
            case COMMON -> 1;
            case UNCOMMON -> 2;
            case RARE -> 4;
            case VERY_RARE -> 8;
        };
        if (book) rarity = Math.max(1, rarity / 2);
        return rarity;
    }

    public static int getBaseCost(NbtList enchantments, boolean book) {
        int cost = 0;
        for(int i = 0; i < enchantments.size(); ++i) {
            NbtCompound nbtCompound = enchantments.getCompound(i);
            Optional<Enchantment> enchantment = Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound));
            if (enchantment.isEmpty()) continue;
            cost += getRarity(enchantment.get(), book) * EnchantmentHelper.getLevelFromNbt(nbtCompound);
        }
        return cost;
    }

    public static int log2(int x) {
        return 31 - Integer.numberOfLeadingZeros(x);
    }

    public static boolean isHideEnchants(int flags) {
        return (flags & ItemStack.TooltipSection.ENCHANTMENTS.getFlag()) != 0;
    }
}
