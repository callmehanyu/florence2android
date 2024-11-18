package zhy.florence2_android.model

interface StoppingCriteria {
    fun call(inputIds: Array<List<Long>>, scores: DoubleArray): BooleanArray  
}  
  
// 实现MaxLengthCriteria类  
class MaxLengthCriteria(private val maxLength: Int) : StoppingCriteria {
    override fun call(inputIds: Array<List<Long>>, scores: DoubleArray): BooleanArray {  
        return inputIds.map { ids ->
            val result = ids.size >= maxLength
            if (result) {
                true
            } else {
                false
            }
        }.toBooleanArray()
    }
}  
  
// 实现EosTokenCriteria类  
class EosTokenCriteria(private val eosTokenID: LongArray) : StoppingCriteria {
  
    override fun call(inputIds: Array<List<Long>>, scores: DoubleArray): BooleanArray {  
        return inputIds.map { ids ->  
            val last = ids.lastOrNull() ?: return@map false // 如果列表为空，返回false  
            val result = eosTokenID.any { eosID -> last == eosID }
            if (result) {
                true
            } else {
                false
            }
        }.toBooleanArray()
    }  
}  