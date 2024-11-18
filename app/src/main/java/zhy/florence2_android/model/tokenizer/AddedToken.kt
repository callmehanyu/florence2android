package zhy.florence2_android.model.tokenizer

import com.google.gson.annotations.SerializedName

data class AddedToken(
    @SerializedName("content")     var content: String,
    @SerializedName("lstrip")      var lstrip: Boolean = false,
    @SerializedName("normalized")  var normalized: Boolean = false,
    @SerializedName("rstrip")      var rstrip: Boolean = false,
    @SerializedName("single_word") var singleWord: Boolean = false,
    @SerializedName("special")     var special: Boolean = false  
)