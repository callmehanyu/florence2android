package zhy.florence2_android

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        findViewById<Button>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "执行 florence2 ", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
            runTaskListBook(this)
        }
        findViewById<Button>(R.id.fab2).setOnClickListener { view ->
            Snackbar.make(view, "获取本地图片", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab2).show()
            localMediaRepo.getAllImage()
        }
        findViewById<Button>(R.id.fab3).setOnClickListener { view ->
            Snackbar.make(view, "插入并执行", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab3).show()
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