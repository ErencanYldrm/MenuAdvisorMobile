import com.google.gson.annotations.SerializedName

data class ReviewRequest(
    @SerializedName("description")
    val description: String,
    @SerializedName("rate")
    val rate: Int,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("price")
    val price: Int? = null,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("createdBy")
    val createdBy: String
) 