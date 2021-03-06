/*
 * Copyright (c) 2018 CovertDragon Team.
 * Copyright (c) 2018 Contributors of SpringFestival.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.covertdragon.springfestival.module.firecracker;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Bootstrap.BehaviorDispenseOptional;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import team.covertdragon.springfestival.SpringFestival;
import team.covertdragon.springfestival.SpringFestivalConstants;
import team.covertdragon.springfestival.internal.model.ModelUtil;
import team.covertdragon.springfestival.module.AbstractSpringFestivalModule;
import team.covertdragon.springfestival.module.SpringFestivalModule;
import team.covertdragon.springfestival.module.firecracker.entity.EntityFirecracker;
import team.covertdragon.springfestival.module.firecracker.entity.ItemFirecrackerEgg;
import team.covertdragon.springfestival.module.firecracker.entity.RenderEntityFirecracker;
import team.covertdragon.springfestival.module.firecracker.firework.BlockFireworkBox;
import team.covertdragon.springfestival.module.firecracker.firework.ItemFireworkBox;
import team.covertdragon.springfestival.module.firecracker.firework.TileFireworkBox;
import team.covertdragon.springfestival.module.firecracker.hanging.BlockHangingFirecracker;
import team.covertdragon.springfestival.module.firecracker.hanging.TileHangingFirecracker;

@SpringFestivalModule(name = "firecracker", dependencies = {"material"})
public class ModuleFirecracker extends AbstractSpringFestivalModule {
    // TODO Albedo support? Are we sure on this one?
    // TODO The compatibility should be done via {@link net.minecraftforge.fml.common.Optional.Interface}
//    public static Boolean useFancyLighting;
    private static final Field FIELD_AVOID_CLASS = ReflectionHelper.findField(EntityAIAvoidEntity.class, "field_181064_i", "classToAvoid");
    private static final Field FIELD_DISPENSE_RESULT = ReflectionHelper.findField(Bootstrap.BehaviorDispenseOptional.class, "field_190911_b", "successful");

    public void onInit() {
        EntityRegistry.registerModEntity(new ResourceLocation(SpringFestivalConstants.MOD_ID, "firecracker"), EntityFirecracker.class, "Firecracker", 0, SpringFestival.getInstance(), 80, 3, true);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(FirecrackerRegistry.itemFirecrackerEgg, new BehaviourFirecrackerDispense());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(
                Items.FLINT_AND_STEEL,
                new BehaviourFlintAndSteelDispense(BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.FLINT_AND_STEEL))
        );
        FIELD_AVOID_CLASS.setAccessible(true);
        FIELD_DISPENSE_RESULT.setAccessible(true);
//        useFancyLighting = Loader.isModLoaded("albedo");
    }
    
    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityMob) {
            EntityMob mob = (EntityMob) event.getEntity();
            for (EntityAITaskEntry task : mob.tasks.taskEntries)
                try {
                    if (task.action instanceof EntityAIAvoidEntity)
                    {
                        if (FIELD_AVOID_CLASS.get(task.action) == EntityFirecracker.class)
                        {
                            return;
                        }
                    }
                } catch (Exception e) {
                    SpringFestivalConstants.logger.catching(e);
                }
                    
            mob.tasks.addTask(1, new EntityAIAvoidEntity<>(mob, EntityFirecracker.class, 6.0F, 1.0D, 1.2D));
        }
    }
    
    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event)
    {
        event.getAffectedEntities().removeIf(e -> e instanceof EntityXPOrb || e instanceof EntityItem);
    }
    
    @SubscribeEvent
    public void onBlockRegister(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity(TileFireworkBox.class, "tile_firework_box");
        GameRegistry.registerTileEntity(TileHangingFirecracker.class, "tile_hanging_firecracker");
        event.getRegistry().registerAll(
                new BlockHangingFirecracker(),
                new BlockFireworkBox()
        );
    }

    @SubscribeEvent
    public void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemFireworkBox(),
                new ItemFirecrackerEgg(),
                new ItemBlock(FirecrackerRegistry.blockHangingFireCracker).setRegistryName(SpringFestivalConstants.MOD_ID, "hanging_firecracker")
        );
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onSoundEventRegistry(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(FirecrackerRegistry.soundFirecrackerThrow);
        event.getRegistry().register(FirecrackerRegistry.soundFirecrackerExplode);
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onModelRegister(ModelRegistryEvent event) {
        ModelUtil.mapItemModel(FirecrackerRegistry.itemFireWorkBox);
        ModelUtil.mapItemModel(FirecrackerRegistry.itemFirecrackerEgg);
        ModelUtil.mapItemModel(FirecrackerRegistry.itemHangingFirecracker);
        RenderingRegistry.registerEntityRenderingHandler(EntityFirecracker.class, RenderEntityFirecracker.FACTORY);
    }
    
    public class BehaviourFirecrackerDispense extends BehaviorProjectileDispense {

        @Override
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            World world = source.getWorld();
            IPosition iposition = BlockDispenser.getDispensePosition(source);
            EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
            IProjectile iprojectile = this.getProjectileEntity(world, iposition, stack);
            iprojectile.shoot((double)enumfacing.getFrontOffsetX(), (double)((float)enumfacing.getFrontOffsetY() + 0.1F), (double)enumfacing.getFrontOffsetZ(), enumfacing != EnumFacing.UP ? 0.943F : 1.2450F , 0.233F);
            world.spawnEntity((Entity)iprojectile);
            stack.shrink(1);
            return stack;
        }
        
        @Override
        protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            return new EntityFirecracker(worldIn, position.getX(), position.getY(), position.getZ(), null);
        }
        
    }
    public class BehaviourFlintAndSteelDispense extends BehaviorDispenseOptional {
        private final IBehaviorDispenseItem behavior;
        
        protected BehaviourFlintAndSteelDispense(IBehaviorDispenseItem behavior)
        {
            this.behavior = behavior;
        }
        
        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            behavior.dispense(source, stack);
            try {
                if (!FIELD_DISPENSE_RESULT.getBoolean(behavior))
                {
                    World world = source.getWorld();
                    BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().getValue(BlockDispenser.FACING));
                    IBlockState state = world.getBlockState(blockpos);
                    if (state.getBlock() == FirecrackerRegistry.blockHangingFireCracker && state.getValue(FirecrackerRegistry.blockHangingFireCracker.COUNT) == 0)
                    {
                        FirecrackerRegistry.blockHangingFireCracker.ignite(world, blockpos, state, false, null);
                        this.successful = true;
                    }
                }
            } catch (Exception e) {
                SpringFestivalConstants.logger.catching(e);
            }
            return stack;
        }
    }
}
