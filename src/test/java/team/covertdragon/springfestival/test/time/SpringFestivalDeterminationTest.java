/*
 * Copyright (c) 2018 CovertDragon Team.
 * Copyright (c) 2018 Contributors of SpringFestival.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.covertdragon.springfestival.test.time;

import org.junit.Assert;
import org.junit.Test;
import team.covertdragon.springfestival.internal.time.ISpringFestivalTimeProvider;
import team.covertdragon.springfestival.internal.time.LunarCalendar;

import java.time.LocalDate;

public class SpringFestivalDeterminationTest {

    @Test
    public void testImpossibleDateChecker() {
        Assert.assertFalse(ISpringFestivalTimeProvider.impossible().isDuringSpringFestival());
    }

    @Test
    public void testCacheBasedDateChecker() {
        ISpringFestivalTimeProvider provider = ISpringFestivalTimeProvider.fromURL("http://covertdragon.team/springfestival/date.txt","test");
        Assert.assertTrue(provider.isDuringSpringFestival());
    }

    @Test
    public void testLunarCalendar() {
        LocalDate date = LocalDate.of(2018, 2, 16);
        LunarCalendar lunarCalendar = new LunarCalendar(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth(), false, false);
        Assert.assertEquals(1, lunarCalendar.getMonth());
        Assert.assertEquals(1, lunarCalendar.getDate());
    }
}
