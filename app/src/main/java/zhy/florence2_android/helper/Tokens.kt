package zhy.florence2_android.helper

open class Tokens(
    open val Padding: String = "",
    open val Unknown: String = "",
    open val Classification: String = "",
    open val Separation: String = "",
    open val Mask: String = "",
    open val EndOfSequence: String? = null,
    open val BeginningOfSequence: String? = null
)  
  
class SentenceTransformerTokens : Tokens() {
    override val Padding: String = ""
    override val Unknown: String = "[UNK]"
    override val Classification: String = "[CLS]"
    override val Separation: String = "[SEP]"
    override val Mask: String = "[MASK]"
    override val EndOfSequence: String? = null
    override val BeginningOfSequence: String? = null
}