package com.cassiokf.IndustrialRenewal.util;

import com.cassiokf.IndustrialRenewal.industrialrenewal;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Utils {
    public static boolean debugMsg = false;
    private static final Random RANDOM = new Random();
    private static final DecimalFormat form = new DecimalFormat("0.0");

    public static final Direction[] VALUES = Direction.values();
    public static final Direction[] BY_HORIZONTAL_INDEX = Arrays.stream(VALUES)
            .filter((direction) -> direction.getAxis().isHorizontal())
            .sorted(Comparator.comparingInt(Direction::get2DDataValue))
            .toArray(Direction[]::new);

    public static void debug(String msg, Object ... objects){
        StringBuilder s = new StringBuilder("DEBUG: ");
        s.append(msg).append(" ");
        for(Object obj : objects){
            if(obj == null)
                s.append("EMPTY ");
            else
                s.append(obj.toString()).append(" ");
        }
        industrialrenewal.LOGGER.info(s);
    }

    public static void sendChatMessage(PlayerEntity player, String str)
    {
        if (player == null) Minecraft.getInstance().player.sendMessage(new StringTextComponent(str), Minecraft.getInstance().player.getUUID());
        else player.sendMessage(new StringTextComponent(str), player.getUUID());
    }

    public static void sendConsoleMessage(String str)
    {
        System.out.println(str);
    }


    public static String formatEnergyString(int energy)
    {
        //industrialrenewal.LOGGER.info("formatString "+energy);
        String text = energy + " FE";
        if (energy >= 1000 && energy < 1000000)
            text = form.format((float) energy / 1000) + "K FE";
        else if (energy >= 1000000 && energy < 1000000000)
            text = form.format((float) energy / 1000000) + "M FE";
        else if (energy >= 1000000000)
            text = form.format((float) energy / 1000000000) + "B FE";
        return text;
    }

    public static float normalizeClamped(float value, float min, float max)
    {
        //industrialrenewal.LOGGER.info("normClamp "+ value + " / " + max);
        return MathHelper.clamp((value - min), 0, (max - min)) / (max - min);
    }

    public static int directionToInt(Direction d){
        switch (d){
            case DOWN: return 0;
            case UP: return 1;
            case NORTH: return 2;
            case SOUTH: return 3;
            case WEST: return 4;
            case EAST: return 5;
        }
        return 2;
    }

    public static Direction intToDir(int d){
        switch (d){
            case 0: return Direction.DOWN;
            case 1: return Direction.UP;
            case 2: return Direction.NORTH;
            case 3: return Direction.SOUTH;
            case 4: return Direction.WEST;
            case 5: return Direction.EAST;
        }
        return Direction.NORTH;
    }


    public static Direction rotateAround(Direction d, Direction.Axis axis)
    {
        switch(axis)
        {
            case X:
                if(d!=Direction.WEST&&d!=Direction.EAST)
                    return rotateX(d);
                return d;
            case Y:
                if(d!=Direction.UP&&d!=Direction.DOWN)
                    return d.getClockWise();

                return d;
            case Z:
                if(d!=Direction.NORTH&&d!=Direction.SOUTH)
                    return rotateZ(d);

                return d;
            default:
                throw new IllegalStateException("Unable to get CW facing for axis "+axis);
        }
    }

    public static Direction rotateX(Direction d)
    {
        switch(d)
        {
            case NORTH:
                return Direction.DOWN;
            case EAST:
            case WEST:
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of "+d);
            case SOUTH:
                return Direction.UP;
            case UP:
                return Direction.NORTH;
            case DOWN:
                return Direction.SOUTH;
        }
    }

    public static Direction rotateZ(Direction d)
    {
        switch(d)
        {
            case EAST:
                return Direction.DOWN;
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of "+d);
            case WEST:
                return Direction.UP;
            case UP:
                return Direction.EAST;
            case DOWN:
                return Direction.WEST;
        }
    }

    public static float lerp(float a, float b, float f)
    {
        return a + f * (b - a);
    }


    public static void dropInventoryItems(World worldIn, BlockPos pos, ItemStackHandler inventory)
    {
        for (int i = 0; i < inventory.getSlots(); ++i)
        {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                spawnItemStack(worldIn, pos, itemstack);
            }
        }
    }

    public static void dropInventoryItems(World worldIn, BlockPos pos, IItemHandler inventory)
    {
        for (int i = 0; i < inventory.getSlots(); ++i)
        {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                spawnItemStack(worldIn, pos, itemstack);
            }
        }
    }

    public static void spawnItemStack(World worldIn, BlockPos pos, ItemStack stack)
    {
        if (worldIn.isClientSide) return;
        float f = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f1 = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f2 = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (!stack.isEmpty())
        {
            ItemEntity entityitem = new ItemEntity(worldIn, pos.getX() + (double) f, pos.getY() + (double) f1, pos.getZ() + (double) f2, stack.split(RANDOM.nextInt(21) + 10));
//            entityitem.motionX = RANDOM.nextGaussian() * 0.05000000074505806D;
//            entityitem.motionY = RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D;
//            entityitem.motionZ = RANDOM.nextGaussian() * 0.05000000074505806D;
            entityitem.push(RANDOM.nextGaussian() * 0.05000000074505806D, RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D, RANDOM.nextGaussian() * 0.05000000074505806D);
            worldIn.addFreshEntity(entityitem);
            //worldIn.spawnEntity(entityitem);
        }
    }

    public static double getDistanceSq(BlockPos pos, double x, double y, double z) {
        double d0 = (double)pos.getX() + 0.5D - x;
        double d1 = (double)pos.getY() + 0.5D - y;
        double d2 = (double)pos.getZ() + 0.5D - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public static IFluidHandler wrapFluidBlock(BlockState state, World world, BlockPos pos)
    {
        return new BlockWrapper(state, world, pos);
    }
}