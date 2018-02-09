/*
 * Copyright (c) 2018 CovertDragon Team.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.covertdragon.springfestival.internal.network;

final class SpringFestivalPacketFactory {

    private SpringFestivalPacketFactory() {}

    static AbstractSpringFestivalPacket getByIndex(int index) {
        // TODO Stub!
        return SpringFestivalPacketVoiding.INSTANCE; // Null-pattern
    }
}
