package zhy.florence2_android

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import zhy.florence2_android.model.Florence2Model

private const val TEST = "Florence2test"

/**
 * todo 1 ocr 识别结果有出入
 */
fun runTaskList(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)
        TaskTypes.values().forEach { task ->
            val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
            Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")
//        var resultsCar = modelSession.Run(task, "car.jpg", "window")
        }
    }

}

/**
 *
 */
fun runOcrTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.OCR
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 *
 */
fun runOcrWithRegionTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.OCR_WITH_REGION
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}