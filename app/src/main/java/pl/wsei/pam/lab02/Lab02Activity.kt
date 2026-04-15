package pl.wsei.pam.lab02

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab03.Lab03Activity

class Lab02Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab02)
    }

    fun onClickBoardSizeBtn(v: View) {
        val tag: String? = v.tag as String?
        val tokens: List<String>? = tag?.split(" ")
        if (tokens != null && tokens.size >= 2) {
            val rows = tokens[0].toInt()
            val columns = tokens[1].toInt()
            
            val intent = Intent(this, Lab03Activity::class.java)
            val size = intArrayOf(rows, columns)
            intent.putExtra("size", size)
            startActivity(intent)
        }
    }
}
