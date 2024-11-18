package zhy.florence2_android.model.tokenizer

import com.google.gson.annotations.SerializedName

data class TokenizerConfig(
    @SerializedName("add_prefix_space")             val AddPrefixSpace: Boolean = false,
    @SerializedName("added_tokens_decoder")         val AddedTokensDecoder: HashMap<String, AddedToken> = hashMapOf(),
    @SerializedName("additional_special_tokens")    val AdditionalSpecialTokens: Array<String> = arrayOf(),
    @SerializedName("bos_token")                    val BosToken: String = "",
    @SerializedName("clean_up_tokenization_spaces") val CleanUpTokenizationSpaces: Boolean = false,
    @SerializedName("cls_token")                    val ClsToken: String = "",
    @SerializedName("eos_token")                    val EosToken: String = "",
    @SerializedName("errors")                       val Errors: String = "",
    @SerializedName("mask_token")                   val MaskToken: String = "",
    @SerializedName("model_max_length")             val ModelMaxLength: Long = 0L,
    @SerializedName("pad_token")                    val PadToken: String = "",
    @SerializedName("processor_class")              val ProcessorClass: String = "",
    @SerializedName("sep_token")                    val SepToken: String = "",
    @SerializedName("tokenizer_class")              val TokenizerClass: String = "",
    @SerializedName("trim_offsets")                 val TrimOffsets: Boolean = false,
    @SerializedName("unk_token")                    val UnkToken: String = "",
)  