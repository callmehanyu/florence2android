package zhy.florence2_android.model.postprocessing

import zhy.florence2_android.model.tokenizer.AddedToken
import zhy.florence2_android.model.tokenizer.Florence2Tokenizer
  
class ByteLevelDecoder(private val addedTokens: Map<String, AddedToken>) {
  
    // 将tokens转换为字符串的私有方法  
    private fun convertTokensToString(tokenizer: Florence2Tokenizer, tokens: List<String>): String {
        val text = tokens.joinToString("")  
        val byteArray = text.map { c -> tokenizer.unicodeToBytes[c] ?: error("Character $c not found in tokenizer's Unicode to bytes mapping") }.toByteArray()  
        return String(byteArray, Charsets.UTF_8)  
    }  
  
    // 解码链方法，将tokens列表解码为子文本列表  
    fun decodeChain(tokenizer: Florence2Tokenizer, tokens: List<String>): List<String> {
        val subTexts = mutableListOf<String>()  
        val currentSubText = mutableListOf<String>()  
  
        for (token in tokens) {  
            if (addedTokens.any { it.value.content == token }) {  
                if (currentSubText.isNotEmpty()) {  
                    subTexts.add(convertTokensToString(tokenizer, currentSubText))  
                    currentSubText.clear()  
                }  
                subTexts.add(token)  
            } else {  
                currentSubText.add(token)  
            }  
        }  
  
        if (currentSubText.isNotEmpty()) {  
            subTexts.add(convertTokensToString(tokenizer, currentSubText))  
        }  
  
        // TODO: 添加spacesBetweenSpecialTokens和cleanUpTokenizationSpaces选项的处理逻辑  
  
        return subTexts  
    }  
}  