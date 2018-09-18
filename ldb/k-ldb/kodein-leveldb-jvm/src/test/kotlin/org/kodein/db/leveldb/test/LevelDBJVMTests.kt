package org.kodein.db.leveldb.test

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.kodein.db.leveldb.LevelDB
import org.kodein.db.leveldb.PlatformLevelDB
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LevelDBJVMTests {

    fun byteArray(vararg values: Any): ByteArray {
        val baos = ByteArrayOutputStream()
        for (value in values) {
            when (value) {
                is Number -> baos.write(value.toByte().toInt())
                is Char -> baos.write(value.toByte().toInt())
                is String -> {
                    for (i in 0 until value.length)
                        baos.write(value[i].toByte().toInt())
                }
                else -> throw IllegalArgumentException(value.javaClass.name)
            }
        }
        return baos.toByteArray()
    }

    fun byteBuffer(vararg values: Any): ByteBuffer = ByteBuffer.wrap(byteArray(*values))

    protected var ldb: LevelDB? = null

    @BeforeTest
    fun setUp() {
        platformFactory.destroy("db")
        ldb = platformFactory.open("db", platformOptions())
    }

    @AfterTest
    fun tearDown() {
        ldb?.close()
        ldb = null
        platformFactory.destroy("db")
    }


    @Test(expected = IllegalStateException::class)
    fun test_90_BadBuffer_00_Get() {
        (ldb!! as PlatformLevelDB).get(byteBuffer("key").asReadOnlyBuffer())
    }

    @Test(expected = IllegalStateException::class)
    fun test_90_BadBuffer_01_Put() {
        (ldb!! as PlatformLevelDB).put(byteBuffer("key"), byteBuffer("bad-value").asReadOnlyBuffer())
    }

    @Test(expected = IllegalStateException::class)
    fun test_90_BadBuffer_02_Delete() {
        (ldb!! as PlatformLevelDB).delete(byteBuffer("key").asReadOnlyBuffer())
    }

    @Test(expected = IllegalStateException::class)
    fun test_90_BadBuffer_03_IndirectBadGet() {
        (ldb!! as PlatformLevelDB).indirectGet(byteBuffer("one").asReadOnlyBuffer())
    }

    @Test(expected = IllegalStateException::class)
    fun test_90_BadBuffer_04_BatchPut() {
        ldb!!.newWriteBatch().use { batch ->
            batch.put(byteBuffer(1), byteBuffer("one").asReadOnlyBuffer())
        }
    }

    @Test(expected = IllegalStateException::class)
    fun test_90_BadBuffer_05_BatchDelete() {
        ldb!!.newWriteBatch().use { batch ->
            batch.delete(byteBuffer(1).asReadOnlyBuffer())
        }
    }

    @Test(expected = IllegalStateException::class)
    fun test_90_BadBuffer_06_IteratorSeek() {
        ldb!!.newIterator().use { it ->
            it.seekTo(byteBuffer(2).asReadOnlyBuffer())
        }
    }

}