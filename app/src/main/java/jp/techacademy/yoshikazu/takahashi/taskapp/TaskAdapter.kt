package jp.techacademy.yoshikazu.takahashi.taskapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(context: Context): BaseAdapter() {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    var mTaskList = mutableListOf<Task>()


    override fun getCount(): Int {
        return mTaskList.size
    }

    override fun getItem(p0: Int): Any {
        return mTaskList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = p1 ?: mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null)
        val textView1 = view.findViewById<TextView>(android.R.id.text1)
        val textView2 = view.findViewById<TextView>(android.R.id.text2)

        textView1.text = mTaskList[p0].title

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPANESE)
        val date = mTaskList[p0].date
        textView2.text = simpleDateFormat.format(date)

        return view

    }
}