package com.example.data.format

import android.util.Base64
import com.example.data.model.PEDAL_MODULES
import com.example.data.model.PresetData
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

object GP180PresetFormat {

    private const val BASE64_TEMPLATE =
        "ETBkBGQAAAAQMFgEABWlJ///DAAQDA8DAwMEECgAAMggMFAAeAAyAAAAAABIeXN0ZXJpYUZ1enoAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwMDwDBQAB" +
        "AgMEBgcICQoLAQAAAAAAAAAAAEhCAABIQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAwAABgAAyEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAEAAAAiAAADAACMQgAASEIAAIxCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAYAAMhCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAcwAABwAAIEIAAHBCAABIQgAAgD8AAEhCAACgQg" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAAdAAAAAAAMQgAAyEEAAHBCAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAABAAABoAAAAAAABIQgAAAAAA" +
        "AAAAAAAAAAAASEIAwNpFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAABgAAyEIAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAAGAADIQgAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAAYAADIQgAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAYAADIQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAADAAAGAADIQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAEAAAADAAAGAADIQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAUDAMAAwAAAANAAAA//8AAGAwcAALAAAAAAAAAAAAyEL//wAAAAAAAAAAyEL//wAA" +
        "AAAAAAAAyEL//wAAAAAAAAAAyEL//wAAAAAAAAAAyEL//wAAAAAAAAAAyEL//wAAAAAAAAAAyEL//wAAAAAA" +
        "AAAAyEL//wAAAAAAAAAAyEL//wAAAAAAAAAAyEIAAAAAcDAEAAAAAACAMCQAdQgAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAA"

    private val FI = listOf(1, 2, 3, 4, 5, 0, 6, 7, 8, 9, 10, 11)
    private const val PH = 0x06000003
    private const val NRC = 0x1d
    private const val DSC = 0x03000022

    val defaultTemplateBytes: ByteArray by lazy {
        Base64.decode(BASE64_TEMPLATE, Base64.DEFAULT)
    }

    data class BuildResult(
        val bytes: ByteArray,
        val filename: String,
        val activeModules: List<String>,
        val skippedModules: List<String>
    )

    fun build(preset: PresetData, customTemplate: ByteArray? = null): BuildResult {
        val baseBytes = (customTemplate ?: defaultTemplateBytes).copyOf()
        val buffer = ByteBuffer.wrap(baseBytes).order(ByteOrder.LITTLE_ENDIAN)

        // Sanitize preset name to 16 ASCII chars
        val cleanName = preset.name
            .replace(Regex("[^ -~]"), "?")
            .take(16)
            .ifBlank { "Preset" }

        // Clear name area [0x2c .. 0x73]
        for (i in 0x2c until 0x74) {
            baseBytes[i] = 0
        }
        val nameBytes = cleanName.toByteArray(Charsets.US_ASCII)
        System.arraycopy(nameBytes, 0, baseBytes, 0x2c, nameBytes.size)

        val activeList = mutableListOf<String>()
        val skippedList = mutableListOf<String>()

        for (f in 0 until 12) {
            val offset = 0x84 + 68 * f
            val moduleIndex = FI[f]
            val moduleDef = PEDAL_MODULES[moduleIndex]
            val moduleState = preset.modules[moduleIndex]

            val code = buffer.getInt(offset + 4)
            if (code == PH && f != 11) {
                if (moduleState.isOn) {
                    skippedList.add(moduleDef.id)
                }
                continue
            }

            buffer.putInt(offset, if (moduleState.isOn) 1 else 0)
            activeList.add(moduleDef.id)

            if (code == NRC || code == DSC) {
                for (k in 0 until 3) {
                    val value = moduleState.values.getOrNull(k) ?: 50f
                    buffer.putFloat(offset + 8 + 4 * k, value.roundToInt().toFloat())
                }
            }
        }

        val filename = cleanName.replace(Regex("[\\\\/:*?\"<>|]"), "").trim().ifBlank { "preset" } + ".prst"
        return BuildResult(
            bytes = baseBytes,
            filename = filename,
            activeModules = activeList,
            skippedModules = skippedList
        )
    }

    fun parse(bytes: ByteArray, currentPreset: PresetData): Pair<PresetData, ByteArray> {
        if (bytes.size < 0x3b4) {
            throw IllegalArgumentException("Ukuran file terlalu kecil untuk preset GP-180")
        }
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val magic0 = buffer.getShort(0).toInt() and 0xFFFF
        val magic74 = buffer.getShort(0x74).toInt() and 0xFFFF

        if (magic0 != 0x3011 || magic74 != 0x3030) {
            throw IllegalArgumentException("Header file bukan preset GP-180 yang valid")
        }

        // Read name
        val nameSb = StringBuilder()
        for (i in 0x2c until 0x74) {
            val b = bytes[i].toInt()
            if (b == 0) break
            nameSb.append(b.toChar())
        }
        val parsedName = nameSb.toString().trim().ifBlank { currentPreset.name }

        val newModules = currentPreset.modules.map { it.copy() }.toMutableList()

        for (q in 0 until 12) {
            val offset = 0x84 + 68 * q
            val moduleIndex = FI[q]
            val isModuleOn = buffer.getInt(offset) == 1
            val code = buffer.getInt(offset + 4)

            val currentMod = newModules[moduleIndex]
            val newValues = currentMod.values.toMutableList()

            if (code == NRC || code == DSC) {
                for (k in 0 until 3) {
                    val rawVal = buffer.getFloat(offset + 8 + 4 * k)
                    val clamped = rawVal.coerceIn(0f, 100f)
                    if (k < newValues.size) {
                        newValues[k] = clamped
                    }
                }
            }

            newModules[moduleIndex] = currentMod.copy(
                isOn = isModuleOn,
                values = newValues
            )
        }

        val updatedPreset = currentPreset.copy(
            name = parsedName,
            modules = newModules
        )
        return Pair(updatedPreset, bytes.copyOf())
    }
}
