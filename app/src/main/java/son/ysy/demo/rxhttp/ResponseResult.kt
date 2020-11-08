package son.ysy.demo.rxhttp

import com.squareup.moshi.*
import com.squareup.moshi.internal.Util
import java.lang.reflect.Type
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

@JsonQualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResponseResult

class ResponseResultJsonAdapter<RESPONSE>(
    type: Type,
    annotations: Set<Annotation>,
    moShi: Moshi
) : JsonAdapter<RESPONSE>() {
    companion object {
        val FACTORY = Factory { type, annotations, moShi ->
            if (annotations.any { it.annotationClass == ResponseResult::class }) {
                val primaryConstructor = ResponseResultJsonAdapter::class.primaryConstructor
                primaryConstructor?.isAccessible = true
                primaryConstructor?.call(type, annotations.filterNot {
                    it.annotationClass == ResponseResult::class
                }.toSet(), moShi)
            } else {
                null
            }
        }
    }

    private val options: JsonReader.Options =        JsonReader.Options.of("data", "errorCode", "errorMsg")

    private val responseJsonAdapter = moShi.adapter<RESPONSE>(type, annotations)

    override fun fromJson(reader: JsonReader): RESPONSE? {
        var code: Int? = null
        var message: String? = null
        val peekJsonReader = reader.peekJson()

        peekJsonReader.beginObject()
        while (peekJsonReader.hasNext()) {
            when (peekJsonReader.selectName(options)) {
                1 -> code = peekJsonReader.nextInt()
                2 -> message = peekJsonReader.nextString()
                else -> {
                    peekJsonReader.skipValue()
                }
            }
        }
        peekJsonReader.endObject()

        val realCode = code ?: throw Util.unexpectedNull("errorCode", "errorCode", reader)


        if (realCode != 0) {
            throw RuntimeException()
        }

        var response: RESPONSE? = null
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> response = responseJsonAdapter.fromJson(reader)
                else -> {
                    reader.skipValue()
                }
            }
        }

        reader.endObject()

        return response ?: throw Util.unexpectedNull("", "data", reader)
    }

    override fun toJson(writer: JsonWriter, value: RESPONSE?) {
        throw NotImplementedError()
    }
}