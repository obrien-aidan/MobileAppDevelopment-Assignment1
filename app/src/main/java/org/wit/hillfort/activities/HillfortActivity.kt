package org.wit.hillfort.activities

import android.content.Intent
//import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_hillfort.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.helpers.readImage
import org.wit.hillfort.helpers.readImageFromPath
import org.wit.hillfort.helpers.showImagePicker
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.models.Location

class HillfortActivity : AppCompatActivity(), AnkoLogger {

    var hillfort = HillfortModel()
    lateinit var app: MainApp
    val IMAGE_REQUEST = 1
    val LOCATION_REQUEST = 2
    //var location = Location(52.245696, -7.139102, 15f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort)
        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)
        info("Hillfort Activity started..")

        app = application as MainApp

        var edit = false

        if (intent.hasExtra("hillfort_edit")) {
            edit = true
            hillfort = intent.extras?.getParcelable<HillfortModel>("hillfort_edit")!!
            hillfortTitle.setText(hillfort.title)
            description.setText(hillfort.description)
            hillfortImage.setImageBitmap(readImageFromPath(this, hillfort.image))
            btnAdd.setText(R.string.save_hillfort)
            hillfortImage.setImageBitmap(readImageFromPath(this, hillfort.image))
            if (hillfort.image != null) {
              chooseImage.setText(R.string.change_hillfort_image)
                }
        }

        btnAdd.setOnClickListener() {
            hillfort.title = hillfortTitle.text.toString()
            hillfort.description = description.text.toString()
            if (hillfort.title.isEmpty()) {
                toast(R.string.enter_hillfort_title)
            } else {
                if (edit) {
                    app.hillforts.update(hillfort.copy())
                } else {
                    app.hillforts.create(hillfort.copy())
                }
            }
            info("add Button Pressed: $hillfortTitle")
            setResult(AppCompatActivity.RESULT_OK)
            finish()
        }

        chooseImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST);
        }
        hillfortLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (hillfort.zoom != 0f) {
                location.lat =  hillfort.lat
                location.lng = hillfort.lng
                location.zoom = hillfort.zoom
            }
            startActivityForResult(intentFor<MapActivity>().putExtra("location", location), LOCATION_REQUEST)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_hillfort, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_cancel -> {
                finish()
            }
            R.id.item_delete -> {
                app.hillforts.delete(hillfort)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
                    hillfort.image = data.getData().toString()
                    hillfortImage.setImageBitmap(readImage(this, resultCode, data))
                    chooseImage.setText(R.string.change_hillfort_image)
                }
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    val location = data.extras?.getParcelable<Location>("location")!!
                    hillfort.lat = location.lat
                    hillfort.lng = location.lng
                    hillfort.zoom = location.zoom
                }
            }
        }
    }
}

