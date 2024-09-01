package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation

import java.io.InputStream
import java.util.Collections
import kotlin.math.max

internal class SplittableInputStream : InputStream {

    private var multiSource: MultiplexedSource
    private var id: Int

    constructor(source: InputStream) {
        multiSource = MultiplexedSource(source)
        id = multiSource.addSource(-1)
    }

    private constructor(multiSource: MultiplexedSource, splitId: Int) {
        this.multiSource = multiSource
        id = multiSource.addSource(splitId)
    }

    internal class MultiplexedSource(
        private val source: InputStream
    ) {

        private val readPositions: MutableList<Int> = ArrayList()
        private var buffer: IntArray = IntArray(MIN_BUF)
        private var writePosition: Int = 0

        fun addSource(splitId: Int): Int {
            readPositions.add(if (splitId == -1) 0 else readPositions[splitId])
            return readPositions.size - 1
        }

        private fun readjustBuffer() {
            val from = Collections.min(readPositions)
            val to = Collections.max(readPositions)
            val newLength = max(
                ((to - from) * 2).toDouble(),
                MIN_BUF.toDouble()
            ).toInt()
            val newBuf = IntArray(newLength)
            System.arraycopy(buffer, from, newBuf, 0, to - from)
            for (i in readPositions.indices) readPositions[i] = readPositions[i] - from
            writePosition -= from
            buffer = newBuf
        }

        fun read(readerId: Int): Int {
            if (readPositions[readerId] >= writePosition) {
                readjustBuffer()
                buffer[writePosition++] = source.read()
            }

            val pos = readPositions[readerId]
            val b = buffer[pos]
            if (b != -1) readPositions[readerId] = pos + 1
            return b
        }

        companion object {
            var MIN_BUF: Int = 4096
        }
    }

    fun split(): SplittableInputStream = SplittableInputStream(multiSource, id)

    override fun read(): Int = multiSource.read(id)
}