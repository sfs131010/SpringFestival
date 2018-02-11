/*
 * Copyright (c) 2018 CovertDragon Team.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.covertdragon.springfestival.internal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IGuiAccessible {
    Object getServerGuiObject(World world, EntityPlayer player);

    Object getClientGuiObject(World world, EntityPlayer player);
}
