package pl.wsei.pam.lab03

import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import pl.wsei.pam.lab01.R
import java.util.Stack

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: List<Int> = listOf(
        android.R.drawable.ic_menu_add, android.R.drawable.ic_menu_delete,
        android.R.drawable.ic_menu_edit, android.R.drawable.ic_menu_save,
        android.R.drawable.ic_menu_share, android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_search,
        android.R.drawable.ic_menu_close_clear_cancel, android.R.drawable.ic_menu_view,
        android.R.drawable.ic_menu_send, android.R.drawable.ic_menu_help,
        android.R.drawable.ic_menu_info_details, android.R.drawable.ic_menu_mylocation,
        android.R.drawable.ic_menu_compass, android.R.drawable.ic_menu_agenda,
        android.R.drawable.ic_menu_call, android.R.drawable.ic_menu_directions
    )
    private val deckResource: Int = R.drawable.deck
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    init {
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))
            it.shuffle()
        }

        gridLayout.columnCount = cols
        gridLayout.rowCount = rows

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val btn = ImageButton(gridLayout.context).also {
                    it.tag = "${row}x${col}"
                    val layoutParams = GridLayout.LayoutParams()
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    it.layoutParams = layoutParams
                    gridLayout.addView(it)
                }
                addTile(btn, shuffledIcons.removeAt(0))
            }
        }
    }

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag]
        // Bug fix: prevent clicking the same card twice to "match" it with itself
        if (matchedPair.contains(tile)) return
        matchedPair.push(tile)
        val matchResult = logic.process {
            tile?.tileResource ?: -1
        }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList().filterNotNull(), matchResult))
        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun setEnabled(enabled: Boolean) {
        tiles.values.forEach { it.button.isEnabled = enabled }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)
        tiles[button.tag.toString()] = tile
    }

    fun getState(): IntArray {
        val state = IntArray(rows * cols)
        var i = 0
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val tile = tiles["${row}x${col}"]
                state[i++] = if (tile?.revealed == true) tile.tileResource else -1
            }
        }
        return state
    }

    fun setState(state: IntArray) {
        var i = 0
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val tile = tiles["${row}x${col}"]
                val resource = state[i++]
                if (resource != -1) {
                    tile?.revealed = true
                }
            }
        }
    }
}
