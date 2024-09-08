package com.example.grckikino.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.grckikino.R
import com.example.grckikino.utils.LIVE_DRAWING_URL

class DrawingAnimationFragment : BaseFragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drawing_animation, container, false)
        loadAnimation(view)
        return view
    }

    private fun loadAnimation(view: View) {
        webView = view.findViewById(R.id.drawing_animation_webview)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(LIVE_DRAWING_URL)
    }

}