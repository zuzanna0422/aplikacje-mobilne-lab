package pl.wsei.pam.lab01

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var mLayout: LinearLayout
    lateinit var mTitle: TextView
    lateinit var mProgress: ProgressBar
    var mBoxes: MutableList<CheckBox> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mLayout = findViewById(R.id.main)

        mTitle = TextView(this)
        mTitle.text = "Laboratorium 1"
        mTitle.textSize = 24f
        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.setMargins(20, 20, 20, 20)
        mTitle.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        mTitle.layoutParams = params
        mLayout.addView(mTitle)

        for (i in 1..6) {
            val row = LinearLayout(this).also {
                it.layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                it.orientation = LinearLayout.HORIZONTAL
            }

            val checkBox = CheckBox(this).also {
                it.text = "Zadanie $i"
                it.isEnabled = false
                it.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LayoutParams.WRAP_CONTENT,
                    1f
                )
            }
            mBoxes.add(checkBox)

            val mButton = Button(this).also {
                it.text = "Testuj"
                it.setOnClickListener {
                    runTest(i)
                }
            }

            row.addView(checkBox)
            row.addView(mButton)
            mLayout.addView(row)
        }

        // Dodanie elementu dystansującego, aby wypchnąć pasek postępu na dół
        val spacer = View(this).also {
            it.layoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        mLayout.addView(spacer)

        mProgress = ProgressBar(
            this,
            null,
            androidx.appcompat.R.attr.progressBarStyle,
            androidx.appcompat.R.style.Widget_AppCompat_ProgressBar_Horizontal
        ).also {
            it.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        mLayout.addView(mProgress)
    }

    private fun updateProgress() {
        val checkedCount = mBoxes.count { it.isChecked }
        mProgress.progress = (checkedCount * 100) / 6
    }

    private fun runTest(taskNum: Int) {
        when (taskNum) {
            1 -> {
                if (task11(4, 6) in 0.666665..0.666667 &&
                    task11(7, -6) in -1.1666667..-1.1666665
                ) {
                    mBoxes[0].isChecked = true
                }
            }
            2 -> {
                if (task12(7U, 6U) == "7 + 6 = 13" &&
                    task12(12U, 15U) == "12 + 15 = 27"
                ) {
                    mBoxes[1].isChecked = true
                }
            }
            3 -> {
                if (task13(0.0, 5.4f) && !task13(7.0, 5.4f) &&
                    !task13(-6.0, -1.0f) && task13(6.0, 9.1f) &&
                    !task13(6.0, -1.0f) && task13(1.0, 1.1f)
                ) {
                    mBoxes[2].isChecked = true
                }
            }
            4 -> {
                if (task14(-2, 5) == "-2 + 5 = 3" &&
                    task14(-2, -5) == "-2 - 5 = -7"
                ) {
                    mBoxes[3].isChecked = true
                }
            }
            5 -> {
                if (task15("DOBRY") == 4 &&
                    task15("barDzo dobry") == 5 &&
                    task15("doStateczny") == 3 &&
                    task15("Dopuszczający") == 2 &&
                    task15("NIEDOSTATECZNY") == 1 &&
                    task15("XYZ") == -1
                ) {
                    mBoxes[4].isChecked = true
                }
            }
            6 -> {
                if (task16(
                        mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                        mapOf("A" to 1U, "B" to 2U)
                    ) == 2U
                    &&
                    task16(
                        mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                        mapOf("F" to 1U, "G" to 2U)
                    ) == 0U
                    &&
                    task16(
                        mapOf("A" to 23U, "B" to 47U, "C" to 30U),
                        mapOf("A" to 1U, "B" to 2U, "C" to 4U)
                    ) == 7U
                ) {
                    mBoxes[5].isChecked = true
                }
            }
        }
        updateProgress()
    }

    private fun task11(a: Int, b: Int): Double {
        return a.toDouble() / b.toDouble()
    }

    private fun task12(a: UInt, b: UInt): String {
        return "$a + $b = ${a + b}"
    }

    fun task13(a: Double, b: Float): Boolean {
        return a >= 0 && a < b
    }

    fun task14(a: Int, b: Int): String {
        val sign = if (b >= 0) "+" else "-"
        return "$a $sign ${Math.abs(b)} = ${a + b}"
    }

    fun task15(degree: String): Int {
        return when (degree.lowercase()) {
            "bardzo dobry" -> 5
            "dobry" -> 4
            "dostateczny" -> 3
            "dopuszczający" -> 2
            "niedostateczny" -> 1
            else -> -1
        }
    }

    fun task16(store: Map<String, UInt>, asset: Map<String, UInt>): UInt {
        if (asset.isEmpty()) return 0U
        var minItems: UInt? = null
        for ((key, required) in asset) {
            if (required == 0U) continue
            val available = store[key] ?: 0U
            val count = available / required
            if (minItems == null || count < minItems) {
                minItems = count
            }
        }
        return minItems ?: 0U
    }
}
