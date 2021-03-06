/*
 * Copyright (c) 2018 CovertDragon Team.
 * Copyright (c) 2018 Contributors of SpringFestival.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.covertdragon.springfestival.internal.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import team.covertdragon.springfestival.SpringFestivalConstants;

/**
 * A packet that does nothing and thus voiding all data. Used to replace usage of null.
 */
final class SpringFestivalPacketVoiding implements AbstractSpringFestivalPacket {

    static final SpringFestivalPacketVoiding INSTANCE = new SpringFestivalPacketVoiding();

    private SpringFestivalPacketVoiding() {}

    @Override
    public void writeDataTo(ByteBuf buffer) {}

    @Override
    public void readDataFrom(ByteBuf buffer, EntityPlayer player) {
        SpringFestivalConstants.logger.warn("One of packet being ignored! This could lead to bug! Report immediately!");
    }
}
