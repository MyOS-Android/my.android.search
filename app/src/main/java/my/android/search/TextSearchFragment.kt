package my.android.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.*
import android.webkit.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.android.synthetic.main.fragment_webview.*


class TextSearchFragment : Fragment(){
    lateinit var webView :WebView
    var resultLoaded = false
    lateinit var urlString :String
    override fun onCreate(savedInstanceState: Bundle?) {
        urlString = arguments?.getString(PARAM_KEY)!!

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webview)
        val progressIndicator : LinearProgressIndicator = view.findViewById(R.id.progress_indicator)

        // Prepare webView
        webView.settings.apply {
            //userAgentString = GoogleReverseImageSearchFragment.USER_AGENT_CHROME
            //cacheMode = WebSettings.LOAD_NO_CACHE
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadsImagesAutomatically = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
            setGeolocationEnabled(false)
        }
        webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        webView.isScrollbarFadingEnabled = true


        // Load links in webview
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false

            /*
            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                super.onReceivedHttpError(view, request, errorResponse)
                status.text = errorResponse?.reasonPhrase
                progressIndicator.visibility = ProgressIndicator.GONE
                webView.visibility = WebView.GONE
                status.visibility = TextView.VISIBLE
            }
             */

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                // Reload the page when ERROR_TIMEOUT or ERROR_HOST_LOOKUP happens on base url
                if (error?.errorCode == WebViewClient.ERROR_TIMEOUT || error?.errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
                    if (urlString.contains(request?.url?.host.toString())) view?.reload()
                }
                //Log.e("===================================", "${error?.errorCode} ${error?.description} ${request?.url}")
            }
        }

        webView.setOnLongClickListener { v ->
            val hitTestResult = (v as WebView).hitTestResult
            when (hitTestResult.type) {
                WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
                    startActivity(Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(hitTestResult.extra)
                    })
                    true
                }
                WebView.HitTestResult.IMAGE_TYPE, WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {
                    startActivity(Intent().apply {
                        // Open IMAGE Link On External Browser
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, hitTestResult.extra)
                    })
                    true
                }
                else -> {
                    false
                }
            }
        }

        // Display loading progress
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                if (progress < 100 && progressIndicator.visibility == LinearProgressIndicator.GONE) {
                    progressIndicator.visibility = LinearProgressIndicator.VISIBLE
                }
                progressIndicator.progress = progress
                if (progress == 100) {
                    progressIndicator.visibility = ProgressBar.GONE
                    resultLoaded = true
                } else resultLoaded = false
            }
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult): Boolean {
                result.cancel()
                return true
            }

            override fun onJsBeforeUnload(view: WebView?, url: String?, message: String?, result: JsResult): Boolean {
                result.cancel()
                return true
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult): Boolean {
                result.cancel()
                return true
            }

            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult): Boolean {
                result.cancel()
                return true
            }
        }

        if (savedInstanceState != null) {
            resultLoaded = savedInstanceState.getBoolean(RESULT_LOADED)
            webView.restoreState(savedInstanceState)
            progressIndicator.visibility = LinearProgressIndicator.GONE
        }

        if (!resultLoaded) {
            status.visibility = TextView.GONE
            webView.visibility = WebView.VISIBLE
            webView.loadUrl(urlString)
        }
    }

    override fun onResume() {
        super.onResume()

        webView.isFocusableInTouchMode = true
        webView.requestFocus()
        webView.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (event.action != KeyEvent.ACTION_UP) return@OnKeyListener true
                if (resultLoaded) {
                    if (webView.canGoBack()) {
                        webView.goBack()

                        return@OnKeyListener true
                    } else activity?.onBackPressed()
                } else {
                    webView.stopLoading()
                    return@OnKeyListener true
                }
            }
            false
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(RESULT_LOADED, resultLoaded)
        webView.saveState(outState)
    }


    fun reload(newUrl: String) {
        urlString = newUrl
        webView.loadUrl(urlString)
    }

    companion object {
        const val RESULT_LOADED = "RESULT_LOADED"
        const val PARAM_KEY = "URL"
        fun newInstance(arg: String) = TextSearchFragment().apply {arguments = Bundle().apply {putString(PARAM_KEY, arg)}}
    }
}