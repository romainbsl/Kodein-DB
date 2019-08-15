@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.kodein.db

import org.kodein.memory.Closeable
import org.kodein.memory.io.*
import kotlin.reflect.KClass


sealed class Key<out T : Any>(val type: KClass<out T>, val bytes: ReadBuffer) {
    abstract fun asHeapKey(): Key<T>
    class Heap<out T : Any>(type: KClass<out T>, bytes: ReadBuffer) : Key<T>(type, bytes) {
        override fun asHeapKey(): Key<T> = this
    }
    class Native<out T : Any>(type: KClass<out T>, private val alloc: Allocation) : Key<T>(type, alloc), Closeable {
        override fun close() { alloc.close() }
        override fun asHeapKey(): Key<T> = Heap(type, KBuffer.wrap(bytes.getBytesHere()))
    }

    override fun hashCode(): Int = bytes.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Key<*>) return false
        return bytes == other.bytes
    }
}

inline class TransientKey<out T : Any>(val transientKey: Key<T>) {
    fun copyToHeap() = Key.Heap(transientKey.type, KBuffer.wrap(transientKey.bytes.getBytes(0)))
}

inline class TransientBytes(val bytes: ReadBuffer) {
    fun copyToHeap() = KBuffer.wrap(bytes.getBytes(0))
}