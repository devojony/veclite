package com.devo.veclite.entity

data class ContentItem(
    val id: Long? = null,
    val content: String = "",
    val vec: DoubleArray = doubleArrayOf(),
    val extra: Map<String, Any?>? = null,
    val score: Double? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContentItem

        if (id != other.id) return false
        if (content != other.content) return false
        if (!vec.contentEquals(other.vec)) return false
        if (extra != other.extra) return false
        return score == other.score
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + content.hashCode()
        result = 31 * result + vec.contentHashCode()
        result = 31 * result + (extra?.hashCode() ?: 0)
        result = 31 * result + (score?.hashCode() ?: 0)
        return result
    }


}
