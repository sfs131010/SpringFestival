/*
 * Copyright (c) 2018 CovertDragon Team.
 * Copyright (c) 2018 Contributors of SpringFestival.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.covertdragon.springfestival.internal.time;

import java.time.LocalDate;

public class SpringFestivalTimeProviderLocal implements ISpringFestivalTimeProvider {

    public static final SpringFestivalTimeProviderLocal INSTANCE = new SpringFestivalTimeProviderLocal();

    private SpringFestivalTimeProviderLocal() {}

    @Override
    public boolean isDuringSpringFestival() {
        LocalDate date = LocalDate.now();
        LunarCalendar lunarCalendar = new LunarCalendar(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth(), false, false);
        return lunarCalendar.getMonth() == 1 && lunarCalendar.getDate() == 1;
    }
}
