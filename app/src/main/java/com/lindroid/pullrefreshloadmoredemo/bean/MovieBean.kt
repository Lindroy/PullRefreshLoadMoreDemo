package com.lindroid.pullrefreshloadmoredemo.bean

/**
 * @author Lin
 * @date 2019/2/22
 * @function
 * @Description
 */
data class MovieBean(
    val count: Int = 0,
    val start: Int = 0,
    val subjects: List<Subject> = listOf(),
    val title: String = "",
    val total: Int = 0
) {
    data class Subject(
        val alt: String = "",
        val casts: List<Cast> = listOf(),
        val collect_count: Int = 0,
        val directors: List<Director> = listOf(),
        val genres: List<String> = listOf(),
        val id: String = "",
        val images: Images = Images(),
        val original_title: String = "",
        val rating: Rating = Rating(),
        val subtype: String = "",
        val title: String = "", //电影中文名
        val year: String = ""
    ) {
        data class Rating(
            val average: Double = 0.0,
            val max: Int = 0,
            val min: Int = 0,
            val stars: String = ""
        )

        data class Cast(
            val alt: String = "",
            val avatars: Avatars = Avatars(),
            val id: String = "",
            val name: String = ""
        ) {
            data class Avatars(
                val large: String = "",
                val medium: String = "",
                val small: String = ""
            )
        }

        data class Images(
            val large: String = "",
            val medium: String = "",
            val small: String = ""
        )

        data class Director(
            val alt: String = "",
            val avatars: Avatars = Avatars(),
            val id: String = "",
            val name: String = ""
        ) {
            data class Avatars(
                val large: String = "",
                val medium: String = "",
                val small: String = ""
            )
        }
    }
}

/*
data class MovieModel(
    val count: Int = 0,
    val start: Int = 0,
    val subjects: List<Subject> = listOf(),
    val title: String = "",
    val total: Int = 0
)

data class Subject(
    val alt: String = "",
    val casts: List<Cast> = listOf(),
    val collect_count: Int = 0,
    val directors: List<Director> = listOf(),
    val genres: List<String> = listOf(),
    val id: String = "",
    val images: Images = Images(),
    val original_title: String = "",
    val rating: Rating = Rating(),
    val subtype: String = "",
    val title: String = "",
    val year: String = ""
)

data class Cast(
    val alt: String = "",
    val avatars: Avatars = Avatars(),
    val id: String = "",
    val name: String = ""
)

data class Avatars(
    val large: String = "",
    val medium: String = "",
    val small: String = ""
)

data class Rating(
    val average: Double = 0.0,
    val max: Int = 0,
    val min: Int = 0,
    val stars: String = ""
)

data class Director(
    val alt: String = "",
    val avatars: Avatars = Avatars(),
    val id: String = "",
    val name: String = ""
)

data class Images(
    val large: String = "",
    val medium: String = "",
    val small: String = ""
)*/
