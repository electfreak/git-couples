package com.example.plugin

import com.example.plugin.jcef.StaticServer
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.jcef.JBCefBrowser
import org.cef.browser.CefBrowser
import org.jetbrains.ide.BuiltInServerManager

class MainPluginPanelFactory : ToolWindowFactory {
    private val windowBrowser: JBCefBrowser = JBCefBrowser()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("builtin server port: ${BuiltInServerManager.getInstance().port}")

        val url = StaticServer.instance.getIndexUrl()
        println("url: $url")
        windowBrowser.loadURL("$url?serverUrl=${StaticServer.instance.serverUrl}")
        toolWindow.component.add(windowBrowser.component)

        val projectDir = project.guessProjectDir()?.path ?: return
        StaticServer.instance.setCalculatorWrapper(projectDir)

        val devTools: CefBrowser = windowBrowser.getCefBrowser().getDevTools()
        val devToolsBrowser = JBCefBrowser.createBuilder()
            .setCefBrowser(devTools)
            .setClient(windowBrowser.getJBCefClient())
            .build()

    }
}