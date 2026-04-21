package pl.wsei.pam.lab03

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import java.util.Random

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout
    private lateinit var completionPlayer: MediaPlayer
    private lateinit var negativePLayer: MediaPlayer
    private var isSound: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        mBoard = findViewById(R.id.memory_board)
        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)
        val rows = size[0]
        val cols = size[1]

        val mBoardModel = MemoryBoardView(mBoard, cols, rows)

        mBoardModel.setOnGameChangeListener { e ->
            runOnUiThread {
                when (e.state) {
                    GameStates.Matching -> {
                        e.tiles.forEach { it.revealed = true }
                    }
                    GameStates.Match -> {
                        e.tiles.forEach { it.revealed = true }
                        if (isSound) completionPlayer.start()
                        mBoardModel.setEnabled(false)
                        var animationsComplete = 0
                        e.tiles.forEach { tile ->
                            animatePairedButton(tile.button) {
                                tile.removeOnClickListener()
                                animationsComplete++
                                if (animationsComplete == e.tiles.size) {
                                    mBoardModel.setEnabled(true)
                                }
                            }
                        }
                    }
                    GameStates.NoMatch -> {
                        e.tiles.forEach { it.revealed = true }
                        if (isSound) negativePLayer.start()
                        mBoardModel.setEnabled(false)
                        animateNoPairButtons(e.tiles[0].button, e.tiles[1].button) {
                            e.tiles.forEach { it.revealed = false }
                            mBoardModel.setEnabled(true)
                        }
                    }
                    GameStates.Finished -> {
                        e.tiles.forEach { it.revealed = true }
                        if (isSound) completionPlayer.start()
                        Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePLayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }

    override fun onPause() {
        super.onPause()
        completionPlayer.release()
        negativePLayer.release()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.board_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.board_activity_sound -> {
                isSound = !isSound
                if (isSound) {
                    Toast.makeText(this, "Sound turn on", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_volume_up_24)
                } else {
                    Toast.makeText(this, "Sound turn off", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_volume_off_24)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun animatePairedButton(button: ImageButton, action: Runnable) {
        val set = AnimatorSet()
        val random = Random()
        button.pivotX = random.nextFloat() * 200f
        button.pivotY = random.nextFloat() * 200f

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 1080f)
        val scallingX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 4f)
        val scallingY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 4f)
        val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)
        set.startDelay = 500
        set.duration = 2000
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation, scallingX, scallingY, fade)
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 0.0f
                action.run()
            }
        })
        set.start()
    }

    private fun animateNoPairButtons(button1: ImageButton, button2: ImageButton, action: Runnable) {
        val shake1 = ObjectAnimator.ofFloat(button1, "rotation", 0f, -15f, 15f, -10f, 10f, 0f)
        val shake2 = ObjectAnimator.ofFloat(button2, "rotation", 0f, -15f, 15f, -10f, 10f, 0f)

        val combined = AnimatorSet()
        combined.playTogether(shake1, shake2)
        combined.duration = 600
        combined.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                action.run()
            }
        })
        combined.start()
    }
}