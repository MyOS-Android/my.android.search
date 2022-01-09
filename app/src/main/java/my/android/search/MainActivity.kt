package my.android.search

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.directsearch_activity.*

class MainActivity : AppCompatActivity() {
    private lateinit var query: TextInputEditText
    private lateinit var queryLayout: TextInputLayout
    private lateinit var searchButton: MaterialButton
    private lateinit var selectImageButton: MaterialButton

    private val getResultsFromFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                startActivity(Intent().apply {
                    action = ReverseImageSearchActivity.INNER_SEARCH
                    putExtra(Intent.EXTRA_STREAM, it)
                    type = "image/*"
                })
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.directsearch_activity)

        searchButton = findViewById(R.id.search)
        queryLayout = findViewById(R.id.edit_layout)
        selectImageButton = findViewById(R.id.select_image)

        query = findViewById<TextInputEditText>(R.id.edit_query).apply {
            requestFocus()
            setOnEditorActionListener { _, id, _ ->
                if (id == EditorInfo.IME_ACTION_SEARCH || id == EditorInfo.IME_NULL)
                    if (searchButton.isChecked) {
                        searchIt(query.text.toString(),metasearch.isChecked)
                    } else
                        translateIt(query.text.toString())
                else false
            }
        }

        metasearch_type.apply { check(if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getString(R.string.meta_search_key), true)) R.id.metasearch else R.id.browser_query) }

        findViewById<MaterialButton>(R.id.search).setOnClickListener {
            queryLayout.isVisible = true
            queryLayout.hint = getString(R.string.edit_search_hint)
            metasearch_type.isVisible = true
            searchbtn.isVisible = true
            selectImageButton.isVisible = false
        }
        findViewById<MaterialButton>(R.id.translate).setOnClickListener {
            queryLayout.isVisible = true
            queryLayout.hint = getString(R.string.edit_translate_hint)
            metasearch_type.isVisible = false
            searchbtn.isVisible = true
            selectImageButton.isVisible = false
        }
        findViewById<MaterialButton>(R.id.image_search).setOnClickListener {
            queryLayout.isVisible = false
            metasearch_type.isVisible = false
            searchbtn.isVisible = false
            selectImageButton.isVisible = true
        }

        findViewById<MaterialButton>(R.id.select_image).setOnClickListener {
            getResultsFromFile.launch("image/*")
        }

        findViewById<MaterialButton>(R.id.searchbtn).setOnClickListener {
            if (query.text!!.isNotEmpty())
                if (searchButton.isChecked) {
                    searchIt(query.text.toString(),metasearch.isChecked)
                } else
                    translateIt(query.text.toString())
        }

        findViewById<MaterialButton>(R.id.settingsbtn).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun translateIt(query: String) : Boolean {
        if (query.isNotBlank()) {
            // Hide soft keyboard
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(edit_query.windowToken, 0)
            startActivity(Intent(this, TranslationActivity::class.java).putExtra(TranslationActivity.KEY_QUERY, query).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
            return true
        }
        else return false
    }

    private fun searchIt(query: String, meta: Boolean) : Boolean {
        if (query.isNotBlank()) {
            // Hide soft keyboard
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(edit_query.windowToken, 0)

            if (meta) {
                if (android.util.Patterns.WEB_URL.matcher(query).matches()) {
                    val url = if (query.startsWith("http")) query else "https://$query"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                else startActivity(Intent(this, WebSearchActivity::class.java)
                        .putExtra(Intent.EXTRA_PROCESS_TEXT, query).putExtra(WebSearchActivity.META, true)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            } else {
                val sp = PreferenceManager.getDefaultSharedPreferences(this)
                val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(sp.getString(getString(R.string.search_engine_key), getString(R.string.url_duck))+query))
                startActivity(searchIntent)
            }
            finish()
            return true
        }
        else return false
    }
}
