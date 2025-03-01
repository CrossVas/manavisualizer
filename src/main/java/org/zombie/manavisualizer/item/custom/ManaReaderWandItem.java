package org.zombie.manavisualizer.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zombie.manavisualizer.ManaNumberFormatting;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.mana.ManaCollector;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.common.block.block_entity.TerrestrialAgglomerationPlateBlockEntity;
import vazkii.botania.common.item.WandOfTheForestItem;

import java.util.List;

public class ManaReaderWandItem extends WandOfTheForestItem {

    public ManaReaderWandItem(ChatFormatting formatting, Item.Properties builder) {
        super(formatting, builder);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        if (!Screen.hasShiftDown()){
            components.add(Component.translatable("item.manareader.tooltip").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(stack, level, components, flag);
    }
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity tile = world.getBlockEntity(pos);

        int current, max;

        if (context.getPlayer() instanceof ServerPlayer player) {
            if (tile instanceof ManaPool manapool) {
                current = manapool.getCurrentMana();
                max = manapool.getMaxMana();
                player.sendSystemMessage(ManaNumberFormatting.manaLevel(current, max), true);
                return InteractionResult.SUCCESS;
            }

            if (tile instanceof ManaReceiver receiver) {
                int mana = receiver.getCurrentMana();
                player.sendSystemMessage(Component.translatable("item.manareader.info.short", ManaNumberFormatting.amount(mana)), true);
            }

            if (tile instanceof TerrestrialAgglomerationPlateBlockEntity plate) {
                int mana = plate.getCurrentMana();
                int roomForMana = plate.getAvailableSpaceForMana();
                int maxMana = mana + roomForMana;
                player.sendSystemMessage(ManaNumberFormatting.manaLevel(mana, maxMana), true);

                return InteractionResult.SUCCESS;
            }

            if (tile instanceof ManaCollector collector) {
                int mana = collector.getCurrentMana();
                int maxMana = collector.getMaxMana();
                player.sendSystemMessage(ManaNumberFormatting.manaLevel(mana, maxMana), true);
                return InteractionResult.SUCCESS;
            }

            if (tile instanceof GeneratingFlowerBlockEntity flower) {
                current = flower.getMana();
                max = flower.getMaxMana();
                player.sendSystemMessage(ManaNumberFormatting.manaLevel(current, max), true);
                return InteractionResult.SUCCESS;
            }
        }

        return super.onItemUseFirst(stack, context);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        return InteractionResult.FAIL;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }
}
