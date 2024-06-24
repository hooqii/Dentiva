package com.example.dentiva.view.scan

import RetrofitScanClient
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.dentiva.R
import com.example.dentiva.data.remote.response.UploadResponse
import com.example.dentiva.databinding.FragmentScanBinding
import com.example.dentiva.util.getImageUri
import com.example.dentiva.util.uriToFile
import com.example.dentiva.util.reduceFileImage
import com.example.dentiva.view.result.ResultScanActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private var file: File? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display a default GIF image when no image is selected
        Glide.with(this)
            .asGif()
            .load(R.drawable.ellipse_vector)
            .into(binding.ivPreview)

        binding.apply {
            gallery.setOnClickListener {
                startGallery()
            }
            camera.setOnClickListener {
                startCamera()
            }
            btnUpload.setOnClickListener {
                if (file != null) {
                    // Reduce the file size before uploading
                    file = file?.reduceFileImage()
                    uploadImage(file!!)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please select or capture an image first",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            Glide.with(this)
                .load(it)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.ivPreview)
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            currentImageUri?.let {
                file = uriToFile(it, requireContext())
                // Reduce the file size after selecting from gallery
                file = file?.reduceFileImage()
            }
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let {
                file = uriToFile(it, requireContext())
                // Reduce the file size after taking a picture
                file = file?.reduceFileImage()
            }
            showImage()
        }
    }

    private fun uploadImage(file: File) {
        progressDialog = ProgressDialog(requireContext()).apply {
            setMessage("Please wait, Dentiva is processing your image...")
            setCancelable(false)
            show()
        }

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        RetrofitScanClient.apiService.uploadImage(body).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    response.body()?.let { uploadResponse ->
                        // Handle successful upload response
                        val intent =
                            Intent(requireContext(), ResultScanActivity::class.java).apply {
                                putExtra("result", uploadResponse)
                            }
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Upload failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Upload failed: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
