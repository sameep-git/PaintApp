package edu.tcu.sameepshah.paint

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val backgroundIv = findViewById<ImageView>(R.id.background_iv)

        setUpBackgroundPicker(backgroundIv)

        findViewById<ImageView>(R.id.save_tool).setOnClickListener {setUpSave()}

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
                drawingView.setPathWidth(15)
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.width2).setOnClickListener {
                drawingView.setPathWidth(10)
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.width3).setOnClickListener {
                drawingView.setPathWidth(6)
                dialog.dismiss()
            }
        }
    }

    private fun setUpBackgroundPicker(backgroundIv: ImageView) {
        val pickMedia = registerForActivityResult(PickVisualMedia()) { // it is URI
            it?.let {Glide.with(this).load(it).into(backgroundIv)}
        }
        // Use a listener to launch the sheet
        findViewById<ImageView>(R.id.image_tool).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }

    private fun setUpSave() {
        val dialog = showInProgress()
        lifecycleScope.launch(Dispatchers.IO) {
//            delay(5000)
            val bitmap = findViewById<FrameLayout>(R.id.drawing_fl).drawToBitmap()
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString().substring(2, 11) + ".jpeg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            lifecycleScope.launch(Dispatchers.IO) {
                uri?.let{
                    contentResolver.openOutputStream(it).use { image ->
                        image?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, image) }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                dialog.dismiss()
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    // Example: content://com.google.android.apps.photos.contentprovider/...
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/jpeg"
                }
                startActivity(Intent.createChooser(shareIntent, null))
            }
        }
    }

    private fun showInProgress(): Dialog {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.in_progress)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }
}


