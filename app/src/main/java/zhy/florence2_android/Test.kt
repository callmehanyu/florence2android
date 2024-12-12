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
 *
 */
fun runTaskListBook(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)
        TaskTypes.entries
//            .filterNot {
//                it in listOf(
//                    TaskTypes.REFERRING_EXPRESSION_SEGMENTATION,
//                    TaskTypes.REGION_TO_SEGMENTATION
//                )
//            }
            .forEach { task ->
                val resultsBook = modelSession.Run(task, "dama.png", "mom")
                Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")
            }
    }

}

/**
 *
 */
fun runTaskListCar(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)
        TaskTypes.entries
            .filterNot {
                it in listOf(
                    TaskTypes.OCR_WITH_REGION,
                    TaskTypes.DETAILED_CAPTION,
                    TaskTypes.REGION_TO_SEGMENTATION,
                    TaskTypes.REGION_TO_CATEGORY,
                )
            }
            .forEach { task ->
                val resultsCar = modelSession.Run(task, "car.jpg", "window")
                Log.d(TEST, "$task : ${Gson().toJson(resultsCar)}")
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

/**
 *
 */
fun runCaptionTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.CAPTION
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 *
 */
fun runDetailedCaptionTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.DETAILED_CAPTION
        val resultsBook = modelSession.Run(task, "car.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 *
 */
fun runMoreDetailedCaptionTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.MORE_DETAILED_CAPTION
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 *
 */
fun runODTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.OD
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 * todo demo book 也是空
 */
fun runDENSE_REGION_CAPTIONTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.DENSE_REGION_CAPTION
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 *
 */
fun runCAPTION_TO_PHRASE_GROUNDINGTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.CAPTION_TO_PHRASE_GROUNDING
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 * todo not work
 */
fun runREFERRING_EXPRESSION_SEGMENTATIONTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.REFERRING_EXPRESSION_SEGMENTATION
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 * todo not work
 */
fun runREGION_TO_SEGMENTATIONTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.REGION_TO_SEGMENTATION
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 *
 */
fun runOPEN_VOCABULARY_DETECTIONTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.OPEN_VOCABULARY_DETECTION
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}


/**
 *
 */
fun runREGION_TO_CATEGORYTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.REGION_TO_CATEGORY
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 *
 */
fun runREGION_TO_DESCRIPTIONTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.REGION_TO_DESCRIPTION
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}
/**
 *
 */
fun runREGION_TO_OCRTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.REGION_TO_OCR
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}

/**
 *
 */
fun runREGION_PROPOSALTask(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val modelSession = Florence2Model(context)

        val task = TaskTypes.REGION_PROPOSAL
        val resultsBook = modelSession.Run(task, "book.jpg", "DUANE")
        Log.d(TEST, "$task : ${Gson().toJson(resultsBook)}")

    }

}