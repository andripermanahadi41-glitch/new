package com.example.data.model

data class ModuleState(
    val isOn: Boolean = true,
    val modelIndex: Int = 0,
    val values: List<Float>
)

data class PresetData(
    val name: String = "Solo Tone - GP180",
    val modules: List<ModuleState> = createDefaultModules(),
    val chainOrder: List<Int> = defaultChainOrder(),
    val selectedModuleIndex: Int = 5 // default to AMP
) {
    val selectedModule: ModuleDefinition
        get() = PEDAL_MODULES[selectedModuleIndex.coerceIn(0, PEDAL_MODULES.lastIndex)]

    val selectedModuleState: ModuleState
        get() = modules[selectedModuleIndex.coerceIn(0, modules.lastIndex)]

    val selectedModuleChainPosition: Int
        get() = chainOrder.indexOf(selectedModuleIndex).coerceAtLeast(0)

    fun updateModuleState(index: Int, update: (ModuleState) -> ModuleState): PresetData {
        val newModules = modules.toMutableList()
        newModules[index] = update(newModules[index])
        return copy(modules = newModules)
    }

    fun updateSelectedModule(update: (ModuleState) -> ModuleState): PresetData {
        return updateModuleState(selectedModuleIndex, update)
    }

    fun moveModuleInChain(fromIndex: Int, toIndex: Int): PresetData {
        if (fromIndex !in chainOrder.indices || toIndex !in chainOrder.indices || fromIndex == toIndex) {
            return this
        }
        val newOrder = chainOrder.toMutableList()
        val item = newOrder.removeAt(fromIndex)
        newOrder.add(toIndex, item)
        return copy(chainOrder = newOrder)
    }

    fun shiftModuleByModuleIndex(moduleIndex: Int, direction: Int): PresetData {
        val currentPos = chainOrder.indexOf(moduleIndex)
        if (currentPos < 0) return this
        val targetPos = (currentPos + direction).coerceIn(0, chainOrder.lastIndex)
        return moveModuleInChain(currentPos, targetPos)
    }

    fun shiftSelectedModule(direction: Int): PresetData {
        return shiftModuleByModuleIndex(selectedModuleIndex, direction)
    }

    fun resetChainOrder(): PresetData {
        return copy(chainOrder = defaultChainOrder())
    }

    companion object {
        fun defaultChainOrder(): List<Int> = List(PEDAL_MODULES.size) { it }

        fun createDefaultModules(): List<ModuleState> {
            val list = PEDAL_MODULES.map { m ->
                ModuleState(
                    isOn = true,
                    modelIndex = 0,
                    values = List(m.parameters.size) { 50f }
                )
            }.toMutableList()

            // Matches default state from GP180 Tone Builder:
            // S[5] (AMP): v=[65, 63, 23, 32, 45, 55]
            if (list.size > 5) {
                list[5] = list[5].copy(values = listOf(65f, 63f, 23f, 32f, 45f, 55f))
            }
            // S[3] (DST) = off
            if (list.size > 3) list[3] = list[3].copy(isOn = false)
            // S[2] (WAH) = off
            if (list.size > 2) list[2] = list[2].copy(isOn = false)
            // S[8] (MOD) = off
            if (list.size > 8) list[8] = list[8].copy(isOn = false)

            return list
        }
    }
}
