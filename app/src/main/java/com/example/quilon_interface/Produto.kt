import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type


data class Produto(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String,
    @SerializedName("production_time") val production_time: String,
    @SerializedName("price") val price: Any,
    @SerializedName("stock") val stock: Any
)

class ProdutoTypeAdapter : JsonDeserializer<Produto> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Produto {
        val jsonObject = json.asJsonObject

        val id = jsonObject.get("id")?.asInt
        val title = jsonObject.get("title").asString
        val category = jsonObject.get("category").asString
        val description = jsonObject.get("description").asString
        val productionTime = jsonObject.get("production_time").asString

        val priceElement = jsonObject.get("price")
        val price = if (priceElement.isJsonPrimitive) {
            if (priceElement.asJsonPrimitive.isString) {
                priceElement.asString
            } else {
                priceElement.asDouble
            }
        } else {
            throw JsonParseException("Unexpected JSON type for price")
        }

        val stockElement = jsonObject.get("stock")
        val stock = if (stockElement.isJsonPrimitive) {
            if (stockElement.asJsonPrimitive.isString) {
                stockElement.asString
            } else {
                stockElement.asInt
            }
        } else {
            throw JsonParseException("Unexpected JSON type for stock")
        }

        return Produto(id, title, category, description, productionTime, price, stock)
    }
}

