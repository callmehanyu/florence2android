package zhy.florence2_android.model.tokenizer

import android.content.Context
import com.google.gson.Gson
import org.json.JSONObject
import zhy.florence2_android.helper.Tokens
import zhy.florence2_android.helper.readAssetsFile
import java.util.regex.Pattern


class Florence2Tokenizer(
    val context: Context,
    val vocabulary: Map<String, Int>,
    val AddedTokens: Map<String, AddedToken>,
    val Tokens: Tokens,
) {

    // 只读属性，使用val关键字
    protected var _vocabulary: MutableList<String?> = MutableList(51289) { null }
    protected var _vocabularyDict: Map<String, Int> = hashMapOf()

    // 可变属性，使用var关键字
    var specialTokens: Set<String> = hashSetOf()

    val ByteToUnicode:  Map<Byte, Char>

    private val _regex =
        Pattern.compile("/'s|'t|'re|'ve|'m|'ll|'d| ?\\p{L}+| ?\\p{N}+| ?[^\\s\\p{L}\\p{N}]+|\\s+(?!\\S)|\\s+/")

    val unicodeToBytes: Map<Char, Byte> = mapOf(
        '0' to 48.toByte(),
        '1' to 49.toByte(),
        '2' to 50.toByte(),
        '3' to 51.toByte(),
        '4' to 52.toByte(),
        '5' to 53.toByte(),
        '6' to 54.toByte(),
        '7' to 55.toByte(),
        '8' to 56.toByte(),
        '9' to 57.toByte(),
        'Ā' to 0.toByte(),
        'ā' to 1.toByte(),
        'Ă' to 2.toByte(),
        'ă' to 3.toByte(),
        'Ą' to 4.toByte(),
        'ą' to 5.toByte(),
        'Ć' to 6.toByte(),
        'ć' to 7.toByte(),
        'Ĉ' to 8.toByte(),
        'ĉ' to 9.toByte(),
        'Ċ' to 10.toByte(),
        'ċ' to 11.toByte(),
        'Č' to 12.toByte(),
        'č' to 13.toByte(),
        'Ď' to 14.toByte(),
        'ď' to 15.toByte(),
        'Đ' to 16.toByte(),
        'đ' to 17.toByte(),
        'Ē' to 18.toByte(),
        'ē' to 19.toByte(),
        'Ĕ' to 20.toByte(),
        'ĕ' to 21.toByte(),
        'Ė' to 22.toByte(),
        'ė' to 23.toByte(),
        'Ę' to 24.toByte(),
        'ę' to 25.toByte(),
        'Ě' to 26.toByte(),
        'ě' to 27.toByte(),
        'Ĝ' to 28.toByte(),
        'ĝ' to 29.toByte(),
        'Ğ' to 30.toByte(),
        'ğ' to 31.toByte(),
        'Ġ' to 32.toByte(),
        '!' to 33.toByte(),
        '\"' to 34.toByte(),
        '#' to 35.toByte(),
        '$' to 36.toByte(),
        '%' to 37.toByte(),
        '&' to 38.toByte(),
        '\'' to 39.toByte(),
        '(' to 40.toByte(),
        ')' to 41.toByte(),
        '*' to 42.toByte(),
        '+' to 43.toByte(),
        ',' to 44.toByte(),
        '-' to 45.toByte(),
        '.' to 46.toByte(),
        '/' to 47.toByte(),
        ':' to 58.toByte(),
        ';' to 59.toByte(),
        '<' to 60.toByte(),
        '=' to 61.toByte(),
        '>' to 62.toByte(),
        '?' to 63.toByte(),
        '@' to 64.toByte(),
        'A' to 65.toByte(),
        'B' to 66.toByte(),
        'C' to 67.toByte(),
        'D' to 68.toByte(),
        'E' to 69.toByte(),
        'F' to 70.toByte(),
        'G' to 71.toByte(),
        'H' to 72.toByte(),
        'I' to 73.toByte(),
        'J' to 74.toByte(),
        'K' to 75.toByte(),
        'L' to 76.toByte(),
        'M' to 77.toByte(),
        'N' to 78.toByte(),
        'O' to 79.toByte(),
        'P' to 80.toByte(),
        'Q' to 81.toByte(),
        'R' to 82.toByte(),
        'S' to 83.toByte(),
        'T' to 84.toByte(),
        'U' to 85.toByte(),
        'V' to 86.toByte(),
        'W' to 87.toByte(),
        'X' to 88.toByte(),
        'Y' to 89.toByte(),
        'Z' to 90.toByte(),
        '[' to 91.toByte(),
        '\\' to 92.toByte(),
        ']' to 93.toByte(),
        '^' to 94.toByte(),
        '_' to 95.toByte(),
        '`' to 96.toByte(),
        'a' to 97.toByte(),
        'b' to 98.toByte(),
        'c' to 99.toByte(),
        'd' to 100.toByte(),
        'e' to 101.toByte(),
        'f' to 102.toByte(),
        'g' to 103.toByte(),
        'h' to 104.toByte(),
        'i' to 105.toByte(),
        'j' to 106.toByte(),
        'k' to 107.toByte(),
        'l' to 108.toByte(),
        'm' to 109.toByte(),
        'n' to 110.toByte(),
        'o' to 111.toByte(),
        'p' to 112.toByte(),
        'q' to 113.toByte(),
        'r' to 114.toByte(),
        's' to 115.toByte(),
        't' to 116.toByte(),
        'u' to 117.toByte(),
        'v' to 118.toByte(),
        'w' to 119.toByte(),
        'x' to 120.toByte(),
        'y' to 121.toByte(),
        'z' to 122.toByte(),
        '{' to 123.toByte(),
        '|' to 124.toByte(),
        '}' to 125.toByte(),
        '~' to 126.toByte(),
        'ġ' to 127.toByte(),
        'Ģ' to 128.toByte(),
        'ģ' to 129.toByte(),
        'Ĥ' to 130.toByte(),
        'ĥ' to 131.toByte(),
        'Ħ' to 132.toByte(),
        'ħ' to 133.toByte(),
        'Ĩ' to 134.toByte(),
        'ĩ' to 135.toByte(),
        'Ī' to 136.toByte(),
        'ī' to 137.toByte(),
        'Ĭ' to 138.toByte(),
        'ĭ' to 139.toByte(),
        'Į' to 140.toByte(),
        'į' to 141.toByte(),
        'İ' to 142.toByte(),
        'ı' to 143.toByte(),
        'Ĳ' to 144.toByte(),
        'ĳ' to 145.toByte(),
        'Ĵ' to 146.toByte(),
        'ĵ' to 147.toByte(),
        'Ķ' to 148.toByte(),
        'ķ' to 149.toByte(),
        'ĸ' to 150.toByte(),
        'Ĺ' to 151.toByte(),
        'ĺ' to 152.toByte(),
        'Ļ' to 153.toByte(),
        'ļ' to 154.toByte(),
        'Ľ' to 155.toByte(),
        'ľ' to 156.toByte(),
        'Ŀ' to 157.toByte(),
        'ŀ' to 158.toByte(),
        'Ł' to 159.toByte(),
        'ł' to 160.toByte(),
        '¡' to 161.toByte(),
        '¢' to 162.toByte(),
        '£' to 163.toByte(),
        '¤' to 164.toByte(),
        '¥' to 165.toByte(),
        '¦' to 166.toByte(),
        '§' to 167.toByte(),
        '¨' to 168.toByte(),
        '©' to 169.toByte(),
        'ª' to 170.toByte(),
        '«' to 171.toByte(),
        '¬' to 172.toByte(),
        'Ń' to 173.toByte(),
        '®' to 174.toByte(),
        '¯' to 175.toByte(),
        '°' to 176.toByte(),
        '±' to 177.toByte(),
        '²' to 178.toByte(),
        '³' to 179.toByte(),
        '´' to 180.toByte(),
        'µ' to 181.toByte(),
        '¶' to 182.toByte(),
        '·' to 183.toByte(),
        '¸' to 184.toByte(),
        '¹' to 185.toByte(),
        'º' to 186.toByte(),
        '»' to 187.toByte(),
        '¼' to 188.toByte(),
        '½' to 189.toByte(),
        '¾' to 190.toByte(),
        '¿' to 191.toByte(),
        'À' to 192.toByte(),
        'Á' to 193.toByte(),
        'Â' to 194.toByte(),
        'Ã' to 195.toByte(),
        'Ä' to 196.toByte(),
        'Å' to 197.toByte(),
        'Æ' to 198.toByte(),
        'Ç' to 199.toByte(),
        'È' to 200.toByte(),
        'É' to 201.toByte(),
        'Ê' to 202.toByte(),
        'Ë' to 203.toByte(),
        'Ì' to 204.toByte(),
        'Í' to 205.toByte(),
        'Î' to 206.toByte(),
        'Ï' to 207.toByte(),
        'Ð' to 208.toByte(),
        'Ñ' to 209.toByte(),
        'Ò' to 210.toByte(),
        'Ó' to 211.toByte(),
        'Ô' to 212.toByte(),
        'Õ' to 213.toByte(),
        'Ö' to 214.toByte(),
        '×' to 215.toByte(),
        'Ø' to 216.toByte(),
        'Ù' to 217.toByte(),
        'Ú' to 218.toByte(),
        'Û' to 219.toByte(),
        'Ü' to 220.toByte(),
        'Ý' to 221.toByte(),
        'Þ' to 222.toByte(),
        'ß' to 223.toByte(),
        'à' to 224.toByte(),
        'á' to 225.toByte(),
        'â' to 226.toByte(),
        'ã' to 227.toByte(),
        'ä' to 228.toByte(),
        'å' to 229.toByte(),
        'æ' to 230.toByte(),
        'ç' to 231.toByte(),
        'è' to 232.toByte(),
        'é' to 233.toByte(),
        'ê' to 234.toByte(),
        'ë' to 235.toByte(),
        'ì' to 236.toByte(),
        'í' to 237.toByte(),
        'î' to 238.toByte(),
        'ï' to 239.toByte(),
        'ð' to 240.toByte(),
        'ñ' to 241.toByte(),
        'ò' to 242.toByte(),
        'ó' to 243.toByte(),
        'ô' to 244.toByte(),
        'õ' to 245.toByte(),
        'ö' to 246.toByte(),
        '÷' to 247.toByte(),
        'ø' to 248.toByte(),
        'ù' to 249.toByte(),
        'ú' to 250.toByte(),
        'û' to 251.toByte(),
        'ü' to 252.toByte(),
        'ý' to 253.toByte(),
        'þ' to 254.toByte(),
        'ÿ' to 255.toByte()
    )

    init {
        // Populate _vocabulary array from vocabulary map
        for ((token, index) in vocabulary) {
            if (_vocabulary[index] != null) {
                throw Exception("InvalidOperationException")
            }
            _vocabulary[index] = token
        }

        // Add addedTokens to _vocabulary
        for ((key, addedToken) in AddedTokens) {
            val id = key.toInt()
            if (_vocabulary[id] != null && _vocabulary[id] != addedToken.content) {
                throw Exception("InvalidOperationException")
            }
            _vocabulary[id] = addedToken.content
        }

        // Check if _vocabulary is empty
        if (_vocabulary.all { it == null }) {
            throw Exception("vocab empty")
        }

        // Create _vocabularyDict from _vocabulary array
        _vocabularyDict = _vocabulary.indices.associate { i -> _vocabulary[i]!! to i }

        // Create set of special tokens
        specialTokens = AddedTokens.values.filter { it.special }.map { it.content }.toSet()

        // Create byte to unicode map
        ByteToUnicode = unicodeToBytes.entries.associate { (key, value) -> value to key }
    }

    companion object {
        fun Init(context: Context): Florence2Tokenizer {
            val fileVocab = readAssetsFile(context, "vocab.json")
            val vocabJson = JSONObject(fileVocab)
            val vocab: MutableMap<String, Int> = HashMap()
            val keys = vocabJson.keys()
            while (keys.hasNext()) {
                val next = keys.next()
                vocab[next] = vocabJson.getInt(next)
            }

            val fileTokenizerConfig = readAssetsFile(context, "tokenizer_config.json")
            val tokenizerConfig = Gson().fromJson(fileTokenizerConfig, TokenizerConfig::class.java)

            val maxID = vocab.maxOf { kv -> kv.value }
            val vocabList = Array<String?>(maxID + 1) { null }
            vocab.forEach { k, v ->
                vocabList[v] = k
            }
            if (vocabList.any{v -> v == null}) throw Exception("missing token")

            val token = Tokens(
                Padding = tokenizerConfig.PadToken,
                Unknown = tokenizerConfig.UnkToken,
                Classification = tokenizerConfig.ClsToken,
                Separation = tokenizerConfig.SepToken,
                Mask = tokenizerConfig.MaskToken,
                EndOfSequence = tokenizerConfig.EosToken,
                BeginningOfSequence = tokenizerConfig.BosToken
            )

            return Florence2Tokenizer(context, vocab, tokenizerConfig.AddedTokensDecoder, token)
        }
    }

    fun Encode(texts: List<String>): List<Pair<List<Long>, List<Long>>> {

        val MaxTokens = 512 //Maximum token length supported by MiniLM model
        val tokenized = Tokenize(texts)

        if (tokenized.isEmpty()) {
            return emptyList()
        }
        val sequenceLength = tokenized.maxByOrNull { it.size.coerceAtMost(MaxTokens) }?.size ?: 0
        return tokenized.map { tokens ->
            val padding = LongArray(sequenceLength - MaxTokens.coerceAtMost(tokens.size)) { 0L }.toList()
            val tokenIndexes = tokens.take(MaxTokens).map { token -> token.second.toLong() } + padding
            val inputMask = tokens.take(MaxTokens).map { 0L } + padding
            tokenIndexes to inputMask
        }
    }

    fun Tokenize(texts: List<String>): List<List<Pair<String, Int>>> {
        val unkTokenId = _vocabularyDict[Tokens.Unknown]!!

        return texts
            .map { text ->
                arrayOf(Tokens.Classification) +
                        TokenizeSentence(text) +
                        arrayOf(Tokens.Separation)
            }.map { tokens ->
                tokens.map { token ->
                    Pair(token, _vocabularyDict.getOrDefault(token, unkTokenId))
                }
            }
    }

    protected fun TokenizeSentence(text: String): List<String> {
        // 使用正则表达式匹配文本
        val matcher = _regex.matcher(text)
        val tokens = mutableListOf<String>()

        while (matcher.find()) {
            // 获取匹配到的值
            val token = matcher.group() ?: continue

            // 将字节转换为Unicode字符（假设需要这样的转换）
            val unicodeToken = String(
                token
                    .encodeToByteArray()
                    .map { b: Byte ->
                        ByteToUnicode[b]
                    }.filterNotNull().toCharArray(),
//                Charsets.UTF_8
            )

            // 添加转换后的标记到列表中
            tokens.add(unicodeToken)
        }
        return tokens
    }

    fun TokenToID(token: String?): Int {
        return _vocabulary.indexOf(token)
    }

    fun IdToToken(id: Int): String {
        return _vocabulary[id] ?: throw Exception("_vocabulary can not be null")
    }

}
