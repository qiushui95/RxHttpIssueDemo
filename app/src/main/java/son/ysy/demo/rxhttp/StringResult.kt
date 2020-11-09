package son.ysy.demo.rxhttp

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonAdapter.Factory
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

@JsonQualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class StringResult(val jsonKey: String)

class StringJsonAdapter(jsonKey: String) : JsonAdapter<String>() {
    companion object {
        val FACTORY = Factory { _, annotations, _ ->
            val stringResult = annotations.filterIsInstance<StringResult>().firstOrNull()
            if (stringResult != null) {
                StringJsonAdapter(stringResult.jsonKey)
            } else {
                null
            }
        }
    }

    private val options = JsonReader.Options.of(jsonKey)

    override fun fromJson(reader: JsonReader): String? {
        reader.beginObject()
        var value: String? = null
        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> value = reader.nextSource().readString(Charsets.UTF_8)
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return value
    }

    override fun toJson(writer: JsonWriter, value: String?) {
        throw NotImplementedError()
    }
}