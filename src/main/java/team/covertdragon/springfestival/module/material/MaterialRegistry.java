/*
 * Copyright (c) 2018 CovertDragon Team.
 * Copyright (c) 2018 Contributors of SpringFestival.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package team.covertdragon.springfestival.module.material;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import team.covertdragon.springfestival.SpringFestivalConstants;

@GameRegistry.ObjectHolder(SpringFestivalConstants.MOD_ID)
public class MaterialRegistry {

    static {
        RED_PAPER = null;
        RED_PAPER_BROKEN = null;
        GLUTINOUS_RICE_CROP = null;
        GLUTINOUS_RICE = null;
        GLUTINOUS_RICE_SEED = null;
    }

    public static final Item RED_PAPER;

    public static final Item RED_PAPER_BROKEN;

    public static final Block GLUTINOUS_RICE_CROP;
    
    public static final Item GLUTINOUS_RICE;
    
    public static final Item GLUTINOUS_RICE_SEED;
    
}