/*
 * Copyright (c) 2018 CovertDragon Team.
 * Copyright (c) 2018 Contributors of SpringFestival.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.covertdragon.springfestival.module.decoration.fudoor;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import team.covertdragon.springfestival.SpringFestivalConstants;
import team.covertdragon.springfestival.module.decoration.DecorationRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockFuDoor extends BlockDoor {

    public BlockFuDoor() {
        super(Material.WOOD);
        setHarvestLevel("axe", 0);
        setHardness(1.5F);
        setUnlocalizedName(SpringFestivalConstants.MOD_ID + ".fu_door");
        setRegistryName(SpringFestivalConstants.MOD_ID, "fu_door");
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? DecorationRegistry.FU : Items.AIR;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(HALF) == EnumDoorHalf.UPPER;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return state.getValue(HALF) == EnumDoorHalf.UPPER ? new TileFuDoor() : null;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        BlockPos position = pos;
        //TileEntity will be null if the block is the lower part of the door
        if (state.getValue(HALF) == EnumDoorHalf.LOWER) {
            position = position.up();
        }
        TileFuDoor te = (TileFuDoor) world.getTileEntity(position);
        if (te != null) {
            if (!world.isRemote && player.getHeldItem(hand).isEmpty()) {
                if (player.isSneaking()) {
                    if (te.getOriginalBlockStateUpper() != null) {
                        //Set door
                        ItemDoor.placeDoor(world, position.add(0, -1, 0), te.getOriginalBlockStateUpper().getValue(BlockDoor.FACING), te.getOriginalBlockStateUpper().getBlock(), te.getOriginalBlockStateUpper().getValue(BlockDoor.HINGE) == BlockDoor.EnumHingePosition.RIGHT);
                        //Drop Fu
                        EntityItem entityItem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(DecorationRegistry.FU, 1));
                        entityItem.setDefaultPickupDelay();
                        world.spawnEntity(entityItem);
                        return true;
                    }
                }
            } else {
                IBlockState upper = te.getOriginalBlockStateUpper();
                if (upper != null) {
                    upper.getBlock().onBlockActivated(world, pos, upper, player, hand, facing, hitX, hitY, hitZ);
                }
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);

        if (te == null) {
            te = world.getTileEntity(pos.add(0, 1, 0));
        }

        if (te == null) {
            throw new NullPointerException();
        }

        harvesters.set(player);

        List<ItemStack> drops = new ArrayList<>();
        drops.add(((TileFuDoor) te).getOriginalDoor());
        drops.add(new ItemStack(DecorationRegistry.FU, 1));

        for (ItemStack drop : drops) {
            EntityItem entity = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), drop);
            entity.setDefaultPickupDelay();
            world.spawnEntity(entity);
        }

        harvesters.set(null);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return ItemStack.EMPTY; // Technical block should not have item form
    }
}
