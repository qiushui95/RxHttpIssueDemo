package son.ysy.demo.rxhttp

import com.squareup.moshi.*
import com.squareup.moshi.internal.Util
import java.lang.reflect.Type
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

@JsonQualifier
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PagingResult

class PagingResultJsonAdapter<RESPONSE>(
    type: Type,
    annotations: Set<Annotation>,
    moShi: Moshi
) : JsonAdapter<RESPONSE>() {
    companion object {
        val FACTORY = Factory { type, annotations, moShi ->
            if (annotations.any { it.annotationClass == PagingResult::class }) {
                val primaryConstructor = PagingResultJsonAdapter::class.primaryConstructor
                primaryConstructor?.isAccessible = true
                primaryConstructor?.call(type, annotations.filterNot {
                    it.annotationClass == PagingResult::class
                }.toSet(), moShi)
            } else {
                null
            }
        }
    }

    private val options: JsonReader.Options = JsonReader.Options.of("size", "datas")

    private val responseJsonAdapter = moShi.adapter<RESPONSE>(type, annotations)

    override fun fromJson(reader: JsonReader): RESPONSE? {
        var size: Int = 0

        val peekJsonReader = reader.peekJson()

        peekJsonReader.beginObject()
        while (peekJsonReader.hasNext()) {
            when (peekJsonReader.selectName(options)) {
                0 -> size = peekJsonReader.nextInt()
                else -> {
                    peekJsonReader.skipValue()
                }
            }
        }
        peekJsonReader.endObject()


        if (size == 0) {
            throw RuntimeException("没有数据")
        }

        var response: RESPONSE? = null
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                1 -> response = responseJsonAdapter.fromJson(reader)
                else -> {
                    reader.skipValue()
                }
            }
        }

        reader.endObject()

        return response ?: throw Util.unexpectedNull("datas", "datas", reader)
    }

    override fun toJson(writer: JsonWriter, value: RESPONSE?) {
        throw NotImplementedError()
    }
}