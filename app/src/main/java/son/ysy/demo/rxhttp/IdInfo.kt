package son.ysy.demo.rxhttp

import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
data class IdInfo(
    @Json(name = "id")
    val id: Int
)