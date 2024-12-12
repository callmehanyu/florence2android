package zhy.florence2_android

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zhy.florence2_android.helper.drawRedRectanglesWithCoordinates
import zhy.florence2_android.helper.getBitmapFromAsset
import zhy.florence2_android.media.LocalMediaRepo
import zhy.florence2_android.media.knowledgegraph.db.example.AppDatabase
import zhy.florence2_android.media.knowledgegraph.db.example.User
import zhy.florence2_android.permisson.requestPermissionOfWriteExternalStorage

private const val TAG = "MainActivityTag"

class MainActivity : AppCompatActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-test"
        ).build()
    }

    private val localMediaRepo by lazy { LocalMediaRepo(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionOfWriteExternalStorage(this)

        findViewById<Button>(R.id.fab_runTaskList).setOnClickListener { view ->
            runTaskList(this, "car.jpg", "")
        }
        findViewById<Button>(R.id.fab_runODTask).setOnClickListener { view ->
            val path = "damalist.png"
            lifecycleScope.launch {
                val florenceResults = withContext(Dispatchers.IO) {
                    runODTask(this@MainActivity, path)
                }

                withContext(Dispatchers.Main) {
                    val rect = florenceResults?.BoundingBoxes?.getOrNull(0)?.bBoxes?.map {
                        it.mapToRect()
                    } ?: return@withContext

                    val bitmap = path
                        .getBitmapFromAsset(this@MainActivity)
                        .drawRedRectanglesWithCoordinates(rect)
                    findViewById<ImageView>(R.id.iv_DenseRegionCaption).setImageBitmap(bitmap)
                }

            }

        }
        findViewById<Button>(R.id.fab_runDenseRegionCaptionTask).setOnClickListener { view ->
            val path = "damalist.png"
            lifecycleScope.launch {
                val florenceResults = withContext(Dispatchers.IO) {
                    runDenseRegionCaptionTask(this@MainActivity, path)
                }

                withContext(Dispatchers.Main) {
                    val rect = florenceResults?.BoundingBoxes?.getOrNull(0)?.bBoxes?.map {
                        it.mapToRect()
                    }
                    val bitmap = if (rect == null) {
                        path.getBitmapFromAsset(this@MainActivity)
                    } else {
                        path
                            .getBitmapFromAsset(this@MainActivity)
                            .drawRedRectanglesWithCoordinates(rect)
                    }
                    findViewById<ImageView>(R.id.iv_DenseRegionCaption).setImageBitmap(bitmap)
                }

            }

        }
        findViewById<Button>(R.id.fab2).setOnClickListener { view ->
            localMediaRepo.getAllImage()
        }
        findViewById<Button>(R.id.fab3).setOnClickListener { view ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val userDao = db.userDao()
                    userDao.insertAll(User(22, "zhy", "lc"))
                    val users: List<User> = userDao.getAll()
                    Log.d(TAG, users[0].firstName ?: "")
                }
            }
        }
    }
}