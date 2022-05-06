package com.cassiokf.IndustrialRenewal.tileentity;

import com.cassiokf.IndustrialRenewal.blocks.BlockElectricPump;
import com.cassiokf.IndustrialRenewal.init.ModTileEntities;
import com.cassiokf.IndustrialRenewal.tileentity.abstracts.TileEntitySyncable;
import com.cassiokf.IndustrialRenewal.util.CustomEnergyStorage;
import com.cassiokf.IndustrialRenewal.util.CustomFluidTank;
import com.cassiokf.IndustrialRenewal.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TileEntityElectricPump extends TileEntitySyncable implements ICapabilityProvider, ITickableTileEntity {


    public CustomFluidTank tank = new CustomFluidTank(1000)
    {
        @Override
        protected void onContentsChanged()
        {
            TileEntityElectricPump.this.setChanged();
        }
    };

    private LazyOptional<IEnergyStorage> energyStorage = LazyOptional.of(this::createEnergy);
    private LazyOptional<CustomFluidTank> tankHandler = LazyOptional.of(()->tank);

    private int index = -1;
    //private int everyXtick = 10;
    private int tick;
    private int energyPerTick = 10;
    private Direction facing;

    private List<BlockPos> fluidSet = new ArrayList<>();

    //TODO: Add to config
    private int maxRadius = 16;

    //IEnergyStorage motorEnergy = null;

    private boolean isRunning = false;
    //private boolean oldIsRunning = false;
    private boolean starting = false;
    //private boolean oldStarting = false;

    private IEnergyStorage createEnergy() {
        return new CustomEnergyStorage(200, 200, 200) {
            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public void onEnergyChange() {
                TileEntityElectricPump.this.sync();
            }
        };
    }

    public TileEntityElectricPump() {
        super(ModTileEntities.ELECTRIC_PUMP_TILE.get());
    }

    public TileEntityElectricPump(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

//    public LazyOptional<IEnergyStorage> debugEnergyStorage(){
//        return energyStorage;
//    }

    @Override
    public void tick() {
        if (!level.isClientSide && getIdex() == 1)
        {
            if (isRunning = consumeEnergy())
            {
                GetFluidDown();
                passFluidUp();
            }
        }
//        else
//        {
//            TODO: add sound handler
//            if (getIdex() == 1)
//            {
//                handleSound();
//            }
//        }
    }

    private int getIdex()
    {
        if (index != -1) return index;
        BlockState state = level.getBlockState(worldPosition);
        index = state.getBlock() instanceof BlockElectricPump ? state.getValue(BlockElectricPump.INDEX) : -1;
        return index;
    }

//    private void handleSound()
//    {
//        if (!level.isClientSide) return;
//        if (isRunning && !starting)
//        {
//            IRSoundHandler.playSound(world, SoundsRegistration.PUMP_START.get(), IRConfig.Main.pumpVolume.get().floatValue() + 0.5f, 1.0F, pos);
//            starting = true;
//            oldStarting = true;
//            Sync();
//        } else if (isRunning)
//        {
//            IRSoundHandler.playRepeatableSound(this, SoundsRegistration.PUMP_ROTATION.get(), IRConfig.MAIN.pumpVolume.get().floatValue(), 1.0F);
//        } else
//        {
//            IRSoundHandler.stopTileSound(pos);
//            starting = false;
//            if (oldStarting)
//            {
//                oldStarting = false;
//                Sync();
//            }
//        }
//    }

//    public boolean drainEnergy(int amount, boolean simulated){
//        IEnergyStorage storage = energyStorage.orElse(null);
//        if (storage.getEnergyStored() >= amount){
////            energyStorage.ifPresent(cap-> {
////                Utils.debug("\nCanExtract, amount, Energy drained, current energy, max energy\n",
////                        cap.canExtract(), amount, cap.extractEnergy(amount, simulated), cap.getEnergyStored(), cap.getMaxEnergyStored(), worldPosition);
////                sync();
////            });
//            storage.extractEnergy(amount, false);
//            //Utils.debug("extraction, simulated, storage", extraction, simulated, storage.getEnergyStored());
//            return true;
//        }
//        return false;
//    }

    private boolean consumeEnergy()
    {
        TileEntityElectricPump motor = getMotor();
        IEnergyStorage e = motor.energyStorage.orElse(null);
//        //Utils.debug("extract amount, max, current", , e.getMaxEnergyStored(), e.getEnergyStored());
        return e.getEnergyStored() > energyPerTick && e.extractEnergy(energyPerTick, false) > 0;

        //motor.energyStorage.ifPresent(cap->cap.extractEnergy(energyPerTick, false));
        //Utils.debug("motor pos", motor.getBlockPos());
//        if (motor != null && motor.drainEnergy(energyPerTick, false))
//        {
//            isRunning = true;
//            sync();
//            //Utils.debug("consume energy return true");
//            return true;
//        } else
//        {
//            isRunning = false;
//            starting = false;
//            sync();
//            //Utils.debug("consume energy return false");
//            return false;
//        }
//        if (oldIsRunning != isRunning || oldStarting != starting)
//        {
//            oldIsRunning = isRunning;
//            oldStarting = starting;
//            sync();
//        }
    }

    private void GetFluidDown()
    {
        if (tank.getFluidAmount() <= 0 && isRunning)
        {
            //TODO: Add to Config
            boolean config = true;
            boolean replaceCobbleConfig = true;
            if (config
            //if (IRConfig.Main.pumpInfinityWater.get()
                    && level.getBlockState(worldPosition.below()).getBlock().equals(Blocks.WATER))
            {
                tank.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
            }
            //Utils.debug("fluid set", getFluidSet() != null, !getFluidSet().isEmpty());
            if (getFluidSet() != null && !getFluidSet().isEmpty())
            {
                BlockPos fluidPos = getFluidSet().get(0);
                //Utils.debug("fluid pos", fluidPos);

                while (!instanceOf(fluidPos, true))
                {
                    getFluidSet().remove(fluidPos);
                    if (getFluidSet() == null || getFluidSet().isEmpty()) return;
                    fluidPos = getFluidSet().get(0);
                }

                FluidState state = level.getFluidState(fluidPos);
                //IFluidHandler downFluid = Utils.wrapFluidBlock(state, level, fluidPos);
                //IFluidHandler downFluid = FluidBlockWrapper(state.getType().)

                boolean consumeFluid = !(state.getType().equals(Fluids.WATER) && config);
                        //&& IRConfig.Main.pumpInfinityWater.get());

                //Utils.debug("downFluid", downFluid.drain(1000, IFluidHandler.FluidAction.SIMULATE).getRawFluid());
                if(state.isSource()){
                    if(tank.fill(new FluidStack(state.getType(), 1000), IFluidHandler.FluidAction.EXECUTE) > 0){
                        if(state.getType().equals(Fluids.LAVA) && replaceCobbleConfig) {
                            level.setBlock(fluidPos, Blocks.COBBLESTONE.defaultBlockState(), Constants.BlockFlags.DEFAULT);
                            getFluidSet().remove(fluidPos);
                        }
                        else if(consumeFluid){
                            level.setBlockAndUpdate(fluidPos, Blocks.AIR.defaultBlockState());
                            getFluidSet().remove(fluidPos);
                        }
                    }
                }

//                if (tank.fill(downFluid.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE) > 0)
//                {
//                    FluidStack stack = downFluid.drain(Integer.MAX_VALUE, consumeFluid ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE);
//                    //Utils.debug("stack", stack);
//                    if (replaceCobbleConfig && stack != null && stack.getFluid().equals(Fluids.LAVA))
//                        level.setBlock(fluidPos, Blocks.COBBLESTONE.defaultBlockState(), Constants.BlockFlags.DEFAULT);
//                    tank.fill(stack, IFluidHandler.FluidAction.EXECUTE);
//                }
//                getFluidSet().remove(fluidPos);
            }
        }
    }

    private List<BlockPos> getFluidSet()
    {
        if (fluidSet.isEmpty()) getAllFluids();
        return fluidSet;
    }

    private void getAllFluids()
    {
        if (level.getBlockState(worldPosition.below()).getBlock() instanceof FlowingFluidBlock)
        {
            Stack<BlockPos> traversingFluids = new Stack<>();
            List<BlockPos> flowingPos = new ArrayList<>();
            traversingFluids.add(worldPosition.below());
            while (!traversingFluids.isEmpty())
            {
                BlockPos fluidPos = traversingFluids.pop();
                if (instanceOf(fluidPos, true)) fluidSet.add(fluidPos);
                else flowingPos.add(fluidPos);

                for (Direction d : Direction.values())
                {
                    BlockPos newPos = fluidPos.relative(d);
                    if (instanceOf(newPos, false) && !fluidSet.contains(newPos) && !flowingPos.contains(newPos))
                    {
                        traversingFluids.add(newPos);
                    }
                }
            }
        }
    }

    private boolean instanceOf(BlockPos pos, boolean checkLevel)
    {
        if (pos == null) return false;
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof FlowingFluidBlock
                && (!checkLevel || state.getValue(FlowingFluidBlock.LEVEL) == 0)
                && Utils.getDistanceSq(worldPosition, pos.getX(), pos.getY(), pos.getZ()) <= maxRadius * maxRadius;
    }

    private void passFluidUp()
    {
        IFluidHandler upTank = GetTankUp();
        if (upTank != null)
        {
            if (upTank.fill(tank.drain(tank.getCapacity(), IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) > 0)
            {
                upTank.fill(tank.drain(tank.getCapacity(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    @Override
    public void setRemoved() {
        //if (world.isRemote) IRSoundHandler.stopTileSound(pos);
        starting = false;
        energyStorage.invalidate();
        tankHandler.invalidate();
        super.setRemoved();
    }

    private TileEntityElectricPump getMotor(){
        TileEntity te = level.getBlockEntity(worldPosition.relative(getBlockState().getValue(BlockElectricPump.FACING).getOpposite()));
        if(te != null && te instanceof TileEntityElectricPump)
            if(((TileEntityElectricPump) te).index == 0)
                return (TileEntityElectricPump)te;
        return null;
    }

//    private IEnergyStorage GetEnergyContainer()
//    {
//        //if (getIdex() == 0) return energyStorage.orElse(null);
//        //if (motorEnergy != null) return motorEnergy;
//        BlockState state = getBlockState();
//        if (state.getBlock() instanceof BlockElectricPump)
//        {
//            Direction facing = state.getValue(BlockElectricPump.FACING);
//            TileEntityElectricPump te = (TileEntityElectricPump) level.getBlockEntity(worldPosition.relative(facing.getOpposite()));
//            if (te != null)
//            {
//                return te.energyStorage.orElse(null);
//            }
//        }
//        return null;
//    }

    private IFluidHandler GetTankUp()
    {
        TileEntity upTE = level.getBlockEntity(worldPosition.above());
        if (upTE != null)
        {
            return upTE.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);
        }
        return null;
    }

    private Direction getBlockFacing()
    {
        if (facing != null) return facing;
        BlockState state = level.getBlockState(worldPosition);
        facing = state.getValue(BlockElectricPump.FACING);
        return facing;
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction facing)
    {
        int index = getIdex();
        //Utils.debug("index, capability, facing", index, capability, facing);
        if (index == 1 && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == Direction.UP)
            return LazyOptional.of(() -> tank).cast();
        Direction face = getBlockFacing();
        if (index == 0 && capability == CapabilityEnergy.ENERGY && facing == face.getOpposite())
            return energyStorage.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        CompoundNBT tag = new CompoundNBT();
        tank.writeToNBT(tag);
        compound.put("fluid", tag);
        compound.putBoolean("isRunning", isRunning);
        compound.putBoolean("starting", starting);
        energyStorage.ifPresent(h ->
        {
            CompoundNBT tag2 = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            compound.put("energy", tag2);
        });

        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        CompoundNBT tag = compound.getCompound("fluid");
        tank.readFromNBT(tag);
        isRunning = compound.getBoolean("isRunning");
        starting = compound.getBoolean("starting");
        energyStorage.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(compound.getCompound("energy")));

        super.load(state, compound);
    }
}