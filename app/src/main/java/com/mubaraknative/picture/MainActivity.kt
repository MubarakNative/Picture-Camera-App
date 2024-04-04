package com.mubaraknative.picture

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
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
            // Todo: show educational UI why this permission needed
        } else {
            // permission granted
            executeCamera()
        }
    }


    private fun executeCamera() {

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

}