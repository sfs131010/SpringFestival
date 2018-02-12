/*
 * Copyright (c) 2018 CovertDragon Team.
 * Copyright (c) 2018 Contributors of SpringFestival.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.covertdragon.springfestival.module.material;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import team.covertdragon.springfestival.SpringFestivalConstants;
import team.covertdragon.springfestival.module.AbstractSpringFestivalModule;
import team.covertdragon.springfestival.module.SpringFestivalModule;

@SpringFestivalModule(name = "material", dependencies = {})
public class ModuleMaterial extends AbstractSpringFestivalModule {

    // A module that contains some common materials, for other modules to use

    public static Item glutinousRiceCrop;

    public static final ItemRedPaper RED_PAPER = (ItemRedPaper) new ItemRedPaper().setRegistryName(SpringFestivalConstants.MOD_ID, "item_red_paper");

    public static Item redPaperBroken;

    public static Item glutinousRice;

    public static Item glutinousRiceSeed;


    // TODO Just add more common stuff below, for tracking
    @SubscribeEvent
    public void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                RED_PAPER
        );
    }
}
