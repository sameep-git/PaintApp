package edu.tcu.sameepshah.paint

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val drawingView = findViewById<DrawingView>(R.id.drawing_view)
        setUpPallet(drawingView)
        setUpPathWidthSelector(drawingView)
        findViewById<ImageView>(R.id.reverse_tool).setOnClickListener {
            drawingView.undoPath()
        }
    }

    private fun setUpPallet(drawingView: DrawingView) {
        // Use for loop
        // Use setImageResource
        // Access the background color from the pallet
        // From ImageView background to Int color
        // (imageViewVar.background as ColorDrawable).color
        val colorsList : Array<ImageView> = arrayOf(
            findViewById(R.id.black_color),
            findViewById(R.id.red_color),
            findViewById(R.id.green_color),
            findViewById(R.id.blue_color),
            findViewById(R.id.purple_color),
            findViewById(R.id.offwhite_color)
        )
        for (colorView in colorsList) {
            colorView.setOnClickListener {
                for (view in colorsList) {
                    if (view == colorView) {
                        view.setImageResource(R.drawable.path_color_selected)
                        drawingView.setPathColor((colorView.background as ColorDrawable).color)
                    } else {
                        view.setImageResource((R.drawable.path_color_normal))
                    }
                }
            }
        }
    }

    private fun setUpPathWidthSelector(drawingView: DrawingView) {
        val dialog = Dialog(this)
        findViewById<ImageView>(R.id.width_tool).setOnClickListener {
            dialog.setContentView(R.layout.path_width_selector)
            dialog.show()
            dialog.findViewById<ImageView>(R.id.width1).setOnClickListener {
                drawingView.setPathWidth(10)
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.width2).setOnClickListener {
                drawingView.setPathWidth(6)
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.width3).setOnClickListener {
                drawingView.setPathWidth(3)
                dialog.dismiss()
            }
        }
    }
}


