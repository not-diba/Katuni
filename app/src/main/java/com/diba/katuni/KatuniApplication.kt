package com.diba.katuni

import android.app.Application
import com.diba.katuni.data.AppContainer
import com.diba.katuni.data.AppContainerImpl

class KatuniApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl()
    }
}