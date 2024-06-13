package com.example.dentiva.view.result

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dentiva.data.remote.response.UploadResponse
import com.example.dentiva.databinding.ActivityResultScanBinding

class ResultScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScanBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Retrieve the upload response from the intent
        val uploadResponse = intent.getSerializableExtra("result") as? UploadResponse
        uploadResponse?.let {
            // Display the data in the TextViews
            binding.tvJenisPenyakit.text = it.jenis_penyakit
            binding.tvSaran.text = it.saran
            binding.tvTingkatAkurat.text = it.tingkat_akurat
        }
    }
}
