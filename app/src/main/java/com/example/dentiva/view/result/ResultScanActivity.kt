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
            binding.tvSaran.text = removeMarkdown(it.saran)
            binding.tvTingkatAkurat.text = it.tingkat_akurat
        }
    }

    // Function to remove Markdown formatting
    private fun removeMarkdown(markdown: String): String {
        var text = markdown

        // Remove headings
        text = text.replace(Regex("""^#{1,6}\s*""", RegexOption.MULTILINE), "")

        // Remove bold and italic
        text = text.replace(Regex("""(\*\*|__)(.*?)\1"""), "$2")  // Bold
        text = text.replace(Regex("""(\*|_)(.*?)\1"""), "$2")  // Italic

        // Remove unordered list markers
        text = text.replace(Regex("""^\s*[-+*]\s+""", RegexOption.MULTILINE), "")

        // Remove ordered list markers
        text = text.replace(Regex("""^\s*\d+\.\s+""", RegexOption.MULTILINE), "")

        // Remove links but keep the text
        text = text.replace(Regex("""\[(.*?)\]\(.*?\)"""), "$1")

        // Remove remaining markdown characters
        text = text.replace(Regex("""[`~>#+=|{}.!]"""), "")

        // Trim extra whitespace and newlines
        text = text.trim()

        return text
    }
}
