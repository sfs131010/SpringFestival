/*
 * Copyright (c) 2018 CovertDragon Team.
 * Copyright (c) 2018 Contributors of SpringFestival.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.covertdragon.springfestival;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import team.covertdragon.springfestival.internal.SpringFestivalGuiHandler;
import team.covertdragon.springfestival.internal.time.ISpringFestivalTimeProvider;
import team.covertdragon.springfestival.internal.time.SpringFestivalTimeProviderFuzzyMatch;
import team.covertdragon.springfestival.internal.time.SpringFestivalTimeProviderLocal;
import team.covertdragon.springfestival.module.ISpringFestivalModule;
import team.covertdragon.springfestival.module.ModuleLoader;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.*;

public abstract class SpringFestivalProxy {

    private static final List<ISpringFestivalTimeProvider> DATE_CHECKERS = new ArrayList<>();
    private final Map<String, ISpringFestivalModule> modules = new HashMap<>();
    private List<ISpringFestivalModule> moduleArrayList;
    private boolean isDuringSpringFestival = false, hasQueriedTime = false;

    /**
     * Determine whether the current time is falling into the Spring Festival season, based on
     * current system time.
     *
     * @return true if it is during Spring Festival; false for otherwise.
     */
    public final boolean isDuringSpringFestivalSeason() {
        if (hasQueriedTime) {
            return isDuringSpringFestival;
        }
        for (ISpringFestivalTimeProvider checker : DATE_CHECKERS) {
            this.isDuringSpringFestival |= checker.isDuringSpringFestival();
            if (isDuringSpringFestival) {
                break;
            }
        }
        this.hasQueriedTime = true;
        return isDuringSpringFestival;
    }

    public final boolean isModuleLoaded(final String module) {
        return modules.containsKey(module);
    }

    public final ISpringFestivalModule getModule(final String module) {
        return modules.get(module);
    }

    public final void onConstruct(FMLConstructionEvent event) {
        moduleArrayList = ModuleLoader.readASMDataTable(event.getASMHarvestedData());
        moduleArrayList.forEach(mod -> this.modules.put(ModuleLoader.getNameByInstance(mod), mod));
        moduleArrayList.forEach(MinecraftForge.EVENT_BUS::register);
    }

    @OverridingMethodsMustInvokeSuper
    public void onPreInit(FMLPreInitializationEvent event) {
        SpringFestivalConstants.logger = event.getModLog();
        moduleArrayList.forEach(ISpringFestivalModule::onPreInit);
        DATE_CHECKERS.add(SpringFestivalTimeProviderLocal.INSTANCE);
        DATE_CHECKERS.add(ISpringFestivalTimeProvider.fromURL("https://covertdragon.team/springfestival/date", "SpringFestival-DateQuerying"));
        if (SpringFestivalConfig.useFuzzySpringFestivalMatcher) {
            DATE_CHECKERS.add(SpringFestivalTimeProviderFuzzyMatch.INSTANCE);
        }
    }

    @OverridingMethodsMustInvokeSuper
    public void onInit(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(SpringFestival.getInstance(), new SpringFestivalGuiHandler());
        moduleArrayList.forEach(ISpringFestivalModule::onInit);
    }

    @OverridingMethodsMustInvokeSuper
    public void onServerStarting(FMLServerStartingEvent event) {
        SpringFestivalConstants.server = event.getServer();
        moduleArrayList.forEach(ISpringFestivalModule::onServerStarting);
    }

    @OverridingMethodsMustInvokeSuper
    public void onServerStopping(FMLServerStoppingEvent event) {
        moduleArrayList.forEach(ISpringFestivalModule::onServerStopping);
    }

    /**
     * Helper method to determine whether this proxy is running on a physical server environment
     *
     * @return true if this proxy object is on a physical server; false otherwise.
     */
    public abstract boolean isPhysicalServer();

    /**
     * Helper method to determine whether this proxy is running on a physical client environment
     *
     * @return true if this proxy object is on a physical client; false otherwise.
     */
    public abstract boolean isPhysicalClient();

    @Nullable
    public abstract EntityPlayer getPlayerByUUID(UUID uuid);

    public abstract void scheduleTask(Runnable task);
}
