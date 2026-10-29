package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ModuleAMP
import com.example.ui.theme.ModuleCAB
import com.example.ui.theme.ModuleDLY
import com.example.ui.theme.ModuleDST
import com.example.ui.theme.ModuleEQ
import com.example.ui.theme.ModuleMOD
import com.example.ui.theme.ModuleNR
import com.example.ui.theme.ModulePRE
import com.example.ui.theme.ModuleRVB
import com.example.ui.theme.ModuleSNAP
import com.example.ui.theme.ModuleVOL
import com.example.ui.theme.ModuleWAH

data class ModuleDefinition(
    val id: String,
    val name: String,
    val color: Color,
    val hexColor: String,
    val parameters: List<String>,
    val models: List<String>,
    val modelParameters: Map<String, List<String>> = emptyMap(),
    val modelDescriptions: Map<String, String> = emptyMap()
) {
    fun getParametersForModel(modelIndex: Int): List<String> {
        val modelName = models.getOrNull(modelIndex) ?: return parameters
        return modelParameters[modelName] ?: parameters
    }

    fun getDescriptionForModel(modelIndex: Int): String? {
        val modelName = models.getOrNull(modelIndex) ?: return null
        return modelDescriptions[modelName]
    }
}

val PEDAL_MODULES = listOf(
    ModuleDefinition(
        id = "NR",
        name = "Noise Gate",
        color = ModuleNR,
        hexColor = "#9aa0a6",
        parameters = listOf("THRESHOLD", "ATTACK", "RELEASE"),
        models = listOf("Gate 1 (ISP Decimator)", "Gate 2 (Modern Gate)"),
        modelParameters = mapOf(
            "Gate 1 (ISP Decimator)" to listOf("THRESHOLD"),
            "Gate 2 (Modern Gate)" to listOf("THRESHOLD", "ATTACK", "RELEASE")
        ),
        modelDescriptions = mapOf(
            "Gate 1 (ISP Decimator)" to "Berdasarkan ISP® Decimator™. Mengadopsi teknologi downward expander untuk pemotongan noise yang sangat mulus, natural, dan transparan dengan kontrol sensitivitas tunggal.",
            "Gate 2 (Modern Gate)" to "Gerbang noise modern fleksibel dengan kontrol Attack & Release manual. Ideal untuk menjaga sustain gitar agar nada tidak terpotong saat bending panjang atau solo berkecepatan tinggi."
        )
    ),
    ModuleDefinition(
        id = "PRE",
        name = "Preamp / Comp",
        color = ModulePRE,
        hexColor = "#4fc3f7",
        parameters = listOf("SUSTN/THRE", "ATTACK", "VOLUME", "TONE"),
        models = listOf(
            "S-Comp", "Keeley C4", "Dyna Comp", "Limiter", "Micro Boost",
            "Xotic AC Preamp", "Xotic RC Preamp", "Xotic BB Preamp", "Fortin Grind",
            "Acoustic Sim", "Pickup Modeler", "Touch Wah", "Step Filter"
        )
    ),
    ModuleDefinition(
        id = "WAH",
        name = "Wah / Pitch",
        color = ModuleWAH,
        hexColor = "#66d97a",
        parameters = listOf("POSITION", "RANGE", "PITCH", "MIX"),
        models = listOf("CryBaby", "Vox V847", "Petrucci Wah", "Octaver", "Detune", "Pitch Bend", "Harmonizer")
    ),
    ModuleDefinition(
        id = "DST",
        name = "Distortion / OD",
        color = ModuleDST,
        hexColor = "#ffb03b",
        parameters = listOf("GAIN", "TONE", "VOLUME"),
        models = listOf(
            "Green OD (TS808)", "OD 9 (TS9)", "Yellow OD (SD-1)", "Penesas (Klon)",
            "Force (OCD)", "TaiChi (Zendrive)", "Darktale (RAT)", "Chief (Guv'nor)",
            "Plustortion (Dist+)", "Lazaro (Big Muff)", "Red Haze (Fuzz Face)",
            "Black Bass (B7K)", "Bass Hammer"
        )
    ),
    ModuleDefinition(
        id = "SNAP",
        name = "Snapshot / NAM",
        color = ModuleSNAP,
        hexColor = "#b388ff",
        parameters = listOf("INPUT GAIN", "VOLUME", "BASS", "MIDDLE", "TREBLE"),
        models = listOf("EVH 5150 III", "Revv Generator 120", "Victory Kraken", "King of Tone", "User NAM 1")
    ),
    ModuleDefinition(
        id = "AMP",
        name = "Amp Modeler",
        color = ModuleAMP,
        hexColor = "#ff8a5c",
        parameters = listOf("GAIN", "BASS", "MID", "TREBLE", "PRESENCE", "MASTER"),
        models = listOf(
            "Fender Tweed Deluxe", "Fender 65 Twin Reverb", "Vox AC30", "Matchless Chieftain",
            "Roland JC-120", "Marshall JCM800", "Marshall JCM900", "Mesa Dual Rectifier",
            "Mesa Mark IV", "Peavey 5150", "Friedman BE-50", "Bogner", "Soldano",
            "Diezel VH4", "Ampeg SVT (Bass)", "Fender Bassman (Bass)", "Acoustic Preamp"
        )
    ),
    ModuleDefinition(
        id = "CAB",
        name = "Cabinet IR",
        color = ModuleCAB,
        hexColor = "#ffd24a",
        parameters = listOf("MIC POSITION", "LOW CUT", "HIGH CUT", "LEVEL"),
        models = listOf("1x10", "1x12", "2x12", "4x12 Marshall V30", "4x12 Mesa V30", "User IR 1")
    ),
    ModuleDefinition(
        id = "EQ",
        name = "Equalizer",
        color = ModuleEQ,
        hexColor = "#dfe3e8",
        parameters = listOf("LOW", "MID", "HIGH", "Q", "LOW CUT", "HIGH CUT", "LEVEL"),
        models = listOf("Guitar EQ 5-band", "Guitar EQ 7-band", "Bass EQ", "Parametric EQ", "HPF / LPF")
    ),
    ModuleDefinition(
        id = "MOD",
        name = "Modulation",
        color = ModuleMOD,
        hexColor = "#4dd0e1",
        parameters = listOf("RATE", "DEPTH", "TONE", "MIX"),
        models = listOf(
            "Analog Chorus", "Stereo Chorus", "Flanger", "Phase 90",
            "Tremolo", "Vibrato", "Rotary Speaker", "Uni-Vibe", "Bit Smash"
        )
    ),
    ModuleDefinition(
        id = "DLY",
        name = "Delay",
        color = ModuleDLY,
        hexColor = "#80cbc4",
        parameters = listOf("TIME", "FEEDBACK", "TONE", "MIX"),
        models = listOf("Digital Delay", "Analog Delay", "Tape Delay", "Ping Pong", "Reverse", "Dual Delay")
    ),
    ModuleDefinition(
        id = "RVB",
        name = "Reverb",
        color = ModuleRVB,
        hexColor = "#c58bff",
        parameters = listOf("DECAY", "PRE-DELAY", "TONE", "MIX"),
        models = listOf("Room", "Hall", "Church", "Plate", "Spring", "Shimmer", "Cloud/Space")
    ),
    ModuleDefinition(
        id = "VOL",
        name = "Volume Pedal",
        color = ModuleVOL,
        hexColor = "#ff6b6b",
        parameters = listOf("MIN VOLUME", "MAX VOLUME"),
        models = listOf("Linear", "Logarithmic")
    )
)
