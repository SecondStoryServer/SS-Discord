package me.syari.ss.discord.data

import me.syari.ss.discord.data.DataContainer.Companion.convertType
import me.syari.ss.discord.data.DataContainer.Companion.toJson
import kotlin.reflect.KClass

internal class DataArray(init: List<Any?> = emptyList()): Iterable<Any?> {
    val data = init.toMutableList()

    val size
        get() = data.size

    private fun get(index: Int): Any? {
        return data[index]
    }

    @Suppress("UNCHECKED_CAST")
    private fun getContainer(index: Int): DataContainer? {
        return try {
            get(index, Map::class) as? Map<String, Any?>
        } catch (ex: ClassCastException) {
            ex.printStackTrace()
            null
        }?.let { DataContainer(it) }
    }

    fun getContainerOrThrow(index: Int): DataContainer {
        return getContainer(index) ?: orThrow(index, "DataContainer")
    }

    private fun getLong(index: Int): Long? {
        return get(index, Long::class, { it.toLong() }, { it.toLong() })
    }

    fun getLongOrThrow(index: Int): Long {
        return getLong(index) ?: orThrow(index, "Long")
    }

    private fun <T: Any> get(
        index: Int, type: KClass<T>, stringParse: ((String) -> T)? = null, numberParse: ((Number) -> T)? = null
    ): T? {
        return convertType(index.toString(), get(index), type, stringParse, numberParse)
    }

    fun add(value: Any?) {
        data.add(
            when (value) {
                is DataContainer -> value.data
                is DataArray -> value.data
                else -> value
            }
        )
    }

    private fun orThrow(index: Int, typeName: String): Nothing {
        throw IllegalStateException("Unable to resolve value at $index to type $typeName: ${data[index]}")
    }

    override fun iterator() = data.iterator()

    override fun toString(): String {
        return toJson(data)
    }
}