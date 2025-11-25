package com.diba.katuni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.diba.katuni.ui.KatuniApp
import com.diba.katuni.ui.theme.KatuniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KatuniTheme {
                KatuniApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KatuniPreview() {
    KatuniTheme {
        KatuniApp()
    }
}