package jp.techacademy.yoshikazu.takahashi.taskapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*

const val EXTRA_TASK = "jp.techacademy.yoshikazu.takahashi.taskapp.TASK"

class MainActivity : AppCompatActivity() {
    private lateinit var mRealm: Realm
    private var search_flag: Boolean = false
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(t: Realm) {
            reloadListView()
        }
    }
    private lateinit var mTaskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }

        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)
        mTaskAdapter = TaskAdapter(this)

        listView1.setOnItemClickListener {
            parent, _, position, _ ->
            val task = parent.adapter.getItem(position) as Task
            val intent = Intent(this, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }

        listView1.setOnItemLongClickListener {
                parent, _, position, _ ->
            val task = parent.adapter.getItem(position) as Task
            val builder = AlertDialog.Builder(this)
            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか？")

            builder.setPositiveButton("OK"){ _, _ ->
                val results = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()
                mRealm.beginTransaction()
                results.deleteAllFromRealm()
                mRealm.commitTransaction()

                val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
                val resultPendingIntent = PendingIntent.getBroadcast(
                    this,
                    task.id,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(resultPendingIntent)

                reloadListView()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }

        search_button.setOnClickListener {
            if(search_flag == false) {
                val realm = Realm.getDefaultInstance()
                var queryText = search_text.text.toString()
                var result = realm.where(Task::class.java)
                    .equalTo("category", queryText)
                    .findAll();
                mTaskAdapter.mTaskList = mRealm.copyFromRealm(result)
                listView1.adapter = mTaskAdapter
                search_flag = true
                search_button.text = "戻る"
            }else {
                search_flag = false
                search_button.text = "検索"
                search_text.setText("", TextView.BufferType.EDITABLE)
                reloadListView()
            }
        }

        reloadListView()
    }

    private fun reloadListView() {
        val taskRealmResults = mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)
        mTaskAdapter.mTaskList = mRealm.copyFromRealm(taskRealmResults)
        listView1.adapter = mTaskAdapter
        mTaskAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

}