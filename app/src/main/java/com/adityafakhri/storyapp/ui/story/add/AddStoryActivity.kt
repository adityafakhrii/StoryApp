package com.adityafakhri.storyapp.ui.story.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.data.source.local.AuthPreferences
import com.adityafakhri.storyapp.data.source.local.dataStore
import com.adityafakhri.storyapp.data.viewmodel.ViewModelAuthFactory
import com.adityafakhri.storyapp.data.viewmodel.ViewModelGeneralFactory
import com.adityafakhri.storyapp.databinding.ActivityAddStoryBinding
import com.adityafakhri.storyapp.ui.auth.AuthViewModel
import com.adityafakhri.storyapp.utils.Const
import com.adityafakhri.storyapp.utils.createCustomTempFile
import com.adityafakhri.storyapp.utils.reduceFileImage
import com.adityafakhri.storyapp.utils.uriToFile
import com.google.android.material.snackbar.Snackbar
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private var _binding: ActivityAddStoryBinding? = null
    private val binding get() = _binding!!

    private var viewModel: AddStoryViewModel? = null

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var tokenKey = ""

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
                    getString(R.string.permission_denied),
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
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.add_story)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val pref = AuthPreferences.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this, ViewModelAuthFactory(pref))[AuthViewModel::class.java]

        viewModel = ViewModelProvider(this, ViewModelGeneralFactory(this))[AddStoryViewModel::class.java]

        authViewModel.getUserPreferences(Const.UserPreferences.Token.name).observe(this) { token ->
            tokenKey = StringBuilder("Bearer ").append(token).toString()
        }

        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }

        binding.btnUpload.setOnClickListener {
            val file = reduceFileImage(getFile as File)
            var isFilled = false

            if (binding.etDescription.text.isNotEmpty() || (getFile != null)) {
                isFilled = true
            }

            if (isFilled) {
                uploadImage(file, binding.etDescription.text.toString())
            } else {
                Snackbar.make(binding.root, getString(R.string.add_img_desc), Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel?.apply {
            loading.observe(this@AddStoryActivity) {
                binding.progressBar.visibility = it
            }

            error.observe(this@AddStoryActivity) {
                if (it.isNotEmpty()) Toast.makeText(
                    applicationContext,
                    getString(R.string.upload_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }

            isSuccessUpload.observe(this@AddStoryActivity) {
                if (it) {
                    Toast.makeText(applicationContext, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.adityafakhri.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                getFile = file
                binding.previewImg.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }

        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.previewImg.setImageURI(uri)
            }
        }
    }

    private fun uploadImage(image: File, description: String) {
        viewModel?.uploadNewStory(tokenKey, image, description)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return true
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}