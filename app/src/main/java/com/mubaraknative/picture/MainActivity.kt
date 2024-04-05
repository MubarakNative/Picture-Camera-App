package com.mubaraknative.picture

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mubaraknative.picture.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraController: LifecycleCameraController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            executeCamera()
        } else { // request permission
            activityResultLauncher.launch(permissions)
        }

    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        var isPermissionGranted: Boolean = true
        permissions.entries.forEach {
            if (it.key in this.permissions && !it.value) {
                isPermissionGranted = false
            }
        }
        if (!isPermissionGranted) {
            showEducationalDialog()
        } else {
            // permission granted
            executeCamera()
        }
    }


    private fun executeCamera() {
        val cameraPreview = binding.previewView
        cameraController = LifecycleCameraController(this)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraPreview.controller = cameraController
    }

    private fun allPermissionsGranted() = permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

        private val permissions =
            mutableListOf(
                android.Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) // this permission is only required on android 9 and below
                }
            }.toTypedArray()


    private fun showEducationalDialog(){
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_denied_title)
            .setMessage(R.string.permission_denied_msg)
            .setNegativeButton(R.string.close) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setPositiveButton(R.string.settings){dialog,_->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivity(intent)
                dialog.dismiss()
            }
            .setCancelable(false)
        dialog.show()
    }

}