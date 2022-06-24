package com.cassiokf.IndustrialRenewal.containers.container;

import com.cassiokf.IndustrialRenewal.containers.ContainerBase;
import com.cassiokf.IndustrialRenewal.init.ModBlocks;
import com.cassiokf.IndustrialRenewal.init.ModContainers;
import com.cassiokf.IndustrialRenewal.tileentity.TileEntityLathe;
import com.cassiokf.IndustrialRenewal.tileentity.locomotion.TileEntityCargoLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class CargoLoaderContainer extends ContainerBase {
    private final TileEntityCargoLoader tileEntity;
    private PlayerInventory playerInventory;

    private int[] xPositions = {62, 80, 98, 71, 89};
    private int[] yPositions = {20, 20, 20, 38, 38};

//    protected CargoLoaderContainer(@Nullable ContainerType<?> p_i50105_1_, int p_i50105_2_) {
//        super(p_i50105_1_, p_i50105_2_);
//    }

    public CargoLoaderContainer(int windowId, PlayerInventory playerInventory, TileEntityCargoLoader tileEntity){
        super(ModContainers.CARGO_LOADER_CONTAINER.get(), windowId);
        this.tileEntity = tileEntity;
        this.playerInventory = playerInventory;

        for(int i = 0; i < xPositions.length; i++){
            this.addSlot(new SlotItemHandler(tileEntity.getInventory(), i, xPositions[i], yPositions[i]){
                @Override
                public void setChanged()
                {
                    tileEntity.setChanged();
                    super.setChanged();
                }
            });
        }

//        this.addSlot(new SlotItemHandler(tileEntity.getInventory(), 0, 62, 20)
//        {
//            @Override
//            public void setChanged()
//            {
//                tileEntity.setChanged();
//                super.setChanged();
//            }
//        });
//        this.addSlot(new SlotItemHandler(tileEntity.getInventory(), 1, 80, 20)
//        {
//            @Override
//            public void setChanged()
//            {
//                tileEntity.setChanged();
//                super.setChanged();
//            }
//        });
//        this.addSlot(new SlotItemHandler(tileEntity.getInventory(), 2, 98, 20)
//        {
//            @Override
//            public void setChanged()
//            {
//                tileEntity.setChanged();
//                super.setChanged();
//            }
//        });
//        this.addSlot(new SlotItemHandler(tileEntity.getInventory(), 3, 71, 38)
//        {
//            @Override
//            public void setChanged()
//            {
//                tileEntity.setChanged();
//                super.setChanged();
//            }
//        });
//
//        this.addSlot(new SlotItemHandler(tileEntity.getInventory(), 4, 89, 38)
//        {
//            @Override
//            public void setChanged()
//            {
//                tileEntity.setChanged();
//                super.setChanged();
//            }
//        });

        drawPlayerInv(playerInventory);
    }

    @Override
    public boolean stillValid(PlayerEntity playerEntity) {
        return stillValid(IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos()),
                playerEntity, ModBlocks.CARGO_LOADER.get());
    }

    public TileEntityCargoLoader getTileEntity(){
        return tileEntity;
    }
}