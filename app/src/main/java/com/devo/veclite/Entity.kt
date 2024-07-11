package com.devo.veclite

import android.content.ContentValues

data class ContentItem(
    val id: Long? = null,
    val content: String = "",
    val vec: DoubleArray = doubleArrayOf(),
    val score: Double? = null
) {

    fun toContentValues(): ContentValues {
        val cv = ContentValues()
        if (id != null) cv.put("id", id)
        cv.put("content", content)
        cv.put("vec", vec.joinToString(","))
        return cv
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContentItem

        if (id != other.id) return false
        if (content != other.content) return false
        if (!vec.contentEquals(other.vec)) return false
        return score == other.score
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + content.hashCode()
        result = 31 * result + vec.contentHashCode()
        result = 31 * result + (score?.hashCode() ?: 0)
        return result
    }


}

enum class SearchType(val title: String) {
    L2("L2"),
    COS("COS"),
    NIP("NIP"),
}