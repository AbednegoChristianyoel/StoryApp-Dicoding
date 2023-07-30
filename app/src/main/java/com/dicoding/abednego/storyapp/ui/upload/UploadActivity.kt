package com.dicoding.abednego.storyapp.ui.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.abednego.storyapp.R
import com.dicoding.abednego.storyapp.data.datastore.UserPreferences
import com.dicoding.abednego.storyapp.databinding.ActivityUploadBinding
import com.dicoding.abednego.storyapp.ui.home.HomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var getFile: File? = null
    private var currentLocation: Location? = null
    private var positionLat: RequestBody? = null
    private var positionLon: RequestBody? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.no_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_upload_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userPreferences = UserPreferences(applicationContext)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getMyLocation()

        binding.switchAddLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                binding.switchAddLocation.text = getString(R.string.location_enabled)
                val location = currentLocation
                if (location != null) {
                    positionLat = location.latitude.toString().toRequestBody("text/plain".toMediaType())
                    positionLon = location.longitude.toString().toRequestBody("text/plain".toMediaType())
                }
            } else {
                binding.switchAddLocation.text = getString(R.string.location_disabled)
            }
        }

        val uploadViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[UploadViewModel::class.java]

        binding.btnUpload.setOnClickListener {
            val description = binding.etDescription.text.toString()
            if(description.isEmpty()) {
                Toast.makeText(this, getString(R.string.empty_description), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val file = getFile?.takeIf { it.exists() } ?: run {
                Toast.makeText(this, getString(R.string.empty_file), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reducedFile = reduceFileImage(file)
            val requestImageFile = reducedFile.asRequestBody(getString(R.string.media_type).toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                getString(R.string.photo),
                reducedFile.name,
                requestImageFile
            )

            userPreferences.isLogin.onEach { token ->
                if (token.isNotEmpty()) {
                    uploadViewModel.uploadStory(
                        token,
                        imageMultipart,
                        description.toRequestBody(getString(R.string.description_type).toMediaType()),
                        positionLat,
                        positionLon
                    )
                    uploadViewModel.uploadResponse.observe(this) { response ->
                        if (response != null) {
                            val intent = Intent(this@UploadActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(this, getString(R.string.toast_upload_successfully), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.launchIn(lifecycleScope)
        }

        uploadViewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        uploadViewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = getString(R.string.intent_type)
        val chooser = Intent.createChooser(intent, getString(R.string.choose_image))
        launcherIntentGallery.launch(chooser)
    }

    @Suppress("DEPRECATION")
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra(getString(R.string.picture)) as File
            val isBackCamera = it.data?.getBooleanExtra(getString(R.string.is_back_camera), true) as Boolean

            val resultFile = reduceFileImageAndroidX(myFile, isBackCamera)
            binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(resultFile.path))
            getFile = resultFile
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@UploadActivity)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}