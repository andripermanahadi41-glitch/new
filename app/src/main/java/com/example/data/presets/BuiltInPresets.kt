package com.example.data.presets

import com.example.data.model.ModuleState
import com.example.data.model.PEDAL_MODULES
import com.example.data.model.PresetData

data class StorePresetItem(
    val name: String,
    val description: String,
    val tag: String,
    val isBass: Boolean,
    val preset: PresetData
)

object BuiltInPresets {

    val items: List<StorePresetItem> by lazy {
        listOf(
            createStoreItem(
                name = "Solo Tone - GP180",
                desc = "Lead, high gain with smooth delay and noise gate",
                tag = "Rock / Lead",
                isBass = false,
                activeIds = listOf("NR", "AMP", "CAB", "EQ", "DLY", "RVB"),
                models = mapOf(
                    "AMP" to "Marshall JCM800",
                    "CAB" to "4x12 Marshall V30",
                    "DLY" to "Digital Delay",
                    "RVB" to "Hall"
                ),
                vals = mapOf(
                    "AMP" to listOf(65f, 63f, 23f, 32f, 45f, 55f),
                    "CAB" to listOf(50f, 20f, 80f, 60f),
                    "DLY" to listOf(40f, 35f, 50f, 30f),
                    "RVB" to listOf(45f, 25f, 50f, 25f)
                )
            ),
            createStoreItem(
                name = "Clean Sparkle",
                desc = "Lush stereo chorus + ambient plate reverb",
                tag = "Clean / Ambient",
                isBass = false,
                activeIds = listOf("PRE", "AMP", "CAB", "MOD", "RVB"),
                models = mapOf(
                    "PRE" to "S-Comp",
                    "AMP" to "Fender 65 Twin Reverb",
                    "CAB" to "2x12",
                    "MOD" to "Analog Chorus",
                    "RVB" to "Plate"
                ),
                vals = mapOf(
                    "PRE" to listOf(45f, 30f, 55f, 60f),
                    "AMP" to listOf(28f, 55f, 48f, 68f, 60f, 70f),
                    "MOD" to listOf(35f, 45f, 60f, 40f),
                    "RVB" to listOf(55f, 30f, 55f, 38f)
                )
            ),
            createStoreItem(
                name = "Blues Crunch",
                desc = "Warm edge of breakup with TS808 green overdrive",
                tag = "Blues / Classic",
                isBass = false,
                activeIds = listOf("DST", "AMP", "CAB", "RVB"),
                models = mapOf(
                    "DST" to "Green OD (TS808)",
                    "AMP" to "Fender Tweed Deluxe",
                    "CAB" to "1x12",
                    "RVB" to "Spring"
                ),
                vals = mapOf(
                    "DST" to listOf(35f, 60f, 70f),
                    "AMP" to listOf(52f, 50f, 60f, 55f, 45f, 65f),
                    "RVB" to listOf(40f, 15f, 45f, 28f)
                )
            ),
            createStoreItem(
                name = "Ambient Pad",
                desc = "Ethereal space delay + shimmering cloud reverb",
                tag = "Ambient / Post-Rock",
                isBass = false,
                activeIds = listOf("PRE", "AMP", "CAB", "DLY", "RVB"),
                models = mapOf(
                    "PRE" to "Limiter",
                    "AMP" to "Roland JC-120",
                    "CAB" to "2x12",
                    "DLY" to "Tape Delay",
                    "RVB" to "Shimmer"
                ),
                vals = mapOf(
                    "AMP" to listOf(25f, 50f, 50f, 60f, 55f, 65f),
                    "DLY" to listOf(60f, 55f, 45f, 45f),
                    "RVB" to listOf(75f, 40f, 60f, 60f)
                )
            ),
            createStoreItem(
                name = "Djent Heavy 5150",
                desc = "Ultra tight modern high-gain with noise reduction and boost",
                tag = "Modern Metal",
                isBass = false,
                activeIds = listOf("NR", "PRE", "AMP", "CAB", "EQ"),
                models = mapOf(
                    "NR" to "Gate 2 (Modern Gate)",
                    "PRE" to "Fortin Grind",
                    "AMP" to "Peavey 5150",
                    "CAB" to "4x12 Mesa V30",
                    "EQ" to "Guitar EQ 7-band"
                ),
                vals = mapOf(
                    "NR" to listOf(68f, 15f, 20f),
                    "PRE" to listOf(50f, 50f, 75f, 65f),
                    "AMP" to listOf(72f, 58f, 42f, 66f, 65f, 60f),
                    "CAB" to listOf(45f, 25f, 75f, 65f)
                )
            ),
            createStoreItem(
                name = "Slap Bass Funk",
                desc = "Punchy attack, tight lows with Ampeg SVT and Bass EQ",
                tag = "Bass / Funk",
                isBass = true,
                activeIds = listOf("PRE", "AMP", "CAB", "EQ"),
                models = mapOf(
                    "PRE" to "S-Comp",
                    "AMP" to "Ampeg SVT (Bass)",
                    "CAB" to "4x12 Marshall V30",
                    "EQ" to "Bass EQ"
                ),
                vals = mapOf(
                    "PRE" to listOf(60f, 25f, 65f, 70f),
                    "AMP" to listOf(42f, 70f, 55f, 68f, 62f, 72f),
                    "EQ" to listOf(65f, 45f, 70f, 50f, 20f, 85f, 60f)
                )
            ),
            createStoreItem(
                name = "Bass Dark Drive",
                desc = "Modern gritty bass overdrive with Darkglass B7K style grit",
                tag = "Bass / Rock",
                isBass = true,
                activeIds = listOf("DST", "AMP", "CAB", "EQ"),
                models = mapOf(
                    "DST" to "Black Bass (B7K)",
                    "AMP" to "Ampeg SVT (Bass)",
                    "CAB" to "4x12 Mesa V30",
                    "EQ" to "Bass EQ"
                ),
                vals = mapOf(
                    "DST" to listOf(55f, 65f, 60f),
                    "AMP" to listOf(48f, 65f, 48f, 64f, 55f, 68f)
                )
            ),
            createStoreItem(
                name = "Pink Solo Lead",
                desc = "Rich sustaining fuzz lead with subtle tape delay",
                tag = "Classic Rock",
                isBass = false,
                activeIds = listOf("DST", "AMP", "CAB", "MOD", "DLY", "RVB"),
                models = mapOf(
                    "DST" to "Lazaro (Big Muff)",
                    "AMP" to "Matchless Chieftain",
                    "CAB" to "4x12 Marshall V30",
                    "MOD" to "Phase 90",
                    "DLY" to "Analog Delay",
                    "RVB" to "Hall"
                ),
                vals = mapOf(
                    "DST" to listOf(68f, 52f, 65f),
                    "AMP" to listOf(45f, 58f, 62f, 60f, 50f, 65f),
                    "MOD" to listOf(25f, 35f, 50f, 30f),
                    "DLY" to listOf(52f, 40f, 45f, 35f)
                )
            )
        )
    }

    private fun createStoreItem(
        name: String,
        desc: String,
        tag: String,
        isBass: Boolean,
        activeIds: List<String>,
        models: Map<String, String>,
        vals: Map<String, List<Float>>
    ): StorePresetItem {
        val modules = PEDAL_MODULES.mapIndexed { idx, modDef ->
            val isActive = modDef.id in activeIds
            val chosenModel = models[modDef.id]
            val modelIndex = if (chosenModel != null) {
                val found = modDef.models.indexOf(chosenModel)
                if (found >= 0) found else 0
            } else 0

            val paramVals = vals[modDef.id] ?: List(modDef.parameters.size) { 50f }
            val clampedVals = modDef.parameters.indices.map { pIdx ->
                paramVals.getOrNull(pIdx) ?: 50f
            }

            ModuleState(
                isOn = isActive,
                modelIndex = modelIndex,
                values = clampedVals
            )
        }

        return StorePresetItem(
            name = name,
            description = desc,
            tag = tag,
            isBass = isBass,
            preset = PresetData(
                name = name,
                modules = modules,
                selectedModuleIndex = 5 // AMP
            )
        )
    }
}
