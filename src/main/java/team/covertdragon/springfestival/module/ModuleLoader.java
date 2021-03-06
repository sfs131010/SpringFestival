/*
 * Copyright (c) 2018 CovertDragon Team.
 * Copyright (c) 2018 Contributors of SpringFestival.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.covertdragon.springfestival.module;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.toposort.TopologicalSort;
import team.covertdragon.springfestival.SpringFestivalConfig;

import javax.annotation.Nullable;
import java.util.*;

public final class ModuleLoader {

    private static boolean dataHarvested = false;

    private ModuleLoader() {
        throw new UnsupportedOperationException("No instance");
    }

    public static List<ISpringFestivalModule> readASMDataTable(ASMDataTable table) {
        if (dataHarvested) {
            throw new IllegalStateException("Modules are already initialized");
        }
        dataHarvested = true;
        return new ModuleSorter(getInstances(table, SpringFestivalModule.class, ISpringFestivalModule.class)).sort();
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> getInstances(ASMDataTable asmDataTable, Class annotationClass, Class<T> instanceClass) {
        String annotationClassName = annotationClass.getCanonicalName();
        Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(annotationClassName);
        final List<String> expectedModules = Arrays.asList(SpringFestivalConfig.modules);
        List<T> instances = new LinkedList<>();
        for (ASMDataTable.ASMData asmData : asmDataSet) {
            try {
                if (!expectedModules.contains(asmData.getAnnotationInfo().get("name").toString())) {
                    continue;
                }
                if (asmData.getAnnotationInfo().get("dependencies") != null && !expectedModules.containsAll((List<String>) asmData.getAnnotationInfo().get("dependencies"))) {
                    continue;
                }
                Class<?> asmClass = Class.forName(asmData.getClassName());
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                T instance = asmInstanceClass.newInstance();
                instances.add(instance);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException("Failed to load: " + asmData.getClassName() + " " + e.toString());
            }
        }
        return instances;
    }

    static Collection<?> getDependenciesByInstance(ISpringFestivalModule instance) {
        return Arrays.asList(instance.getClass().getAnnotation(SpringFestivalModule.class).dependencies());
    }

    public static String getNameByInstance(ISpringFestivalModule instance) {
        return instance.getClass().getAnnotation(SpringFestivalModule.class).name();
    }

    @Nullable
    private static ISpringFestivalModule getInstanceByName(List<ISpringFestivalModule> modules, String name) {
        for (ISpringFestivalModule module : modules) {
            if (getNameByInstance(module).equals(name)) {
                return module;
            }
        }
        return null;
    }

    private static class ModuleSorter {
        private TopologicalSort.DirectedGraph<ISpringFestivalModule> moduleGraph;

        ModuleSorter(List<ISpringFestivalModule> modules) {
            buildGraph(modules);
        }

        private void buildGraph(List<ISpringFestivalModule> modules) {
            moduleGraph = new TopologicalSort.DirectedGraph<>();

            for (ISpringFestivalModule module : modules) {
                moduleGraph.addNode(module);
            }

            for (ISpringFestivalModule module : modules) {
                Collection<?> dependencies = getDependenciesByInstance(module);

                if (dependencies.size() == 0) {
                    continue;
                }

                dependencies.forEach(dep -> moduleGraph.addEdge(getInstanceByName(modules, (String) dep), module));
            }
        }

        List<ISpringFestivalModule> sort() {
            return TopologicalSort.topologicalSort(moduleGraph);
        }
    }
}
