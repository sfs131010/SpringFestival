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
import java.time.Month;

/**
 * A implementation of ISpringFestivalTimeProvider that roughly match the spring festival time
 * when it's in January or February of Georgian Calendar.
 */
public class SpringFestivalTimeProviderFuzzyMatch implements ISpringFestivalTimeProvider {

    public static final SpringFestivalTimeProviderFuzzyMatch INSTANCE = new SpringFestivalTimeProviderFuzzyMatch();

    private SpringFestivalTimeProviderFuzzyMatch() {}

    @Override
    public boolean isDuringSpringFestival() {
        Month month = LocalDate.now().getMonth();
        return month == Month.JANUARY || month == Month.FEBRUARY;
    }
}
