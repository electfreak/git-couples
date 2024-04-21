package com.example.plugin

import adapter.ClonedRepoAdapter
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.jcef.JBCefBrowser
import java.awt.BorderLayout


class MainPluginPanelFactory : ToolWindowFactory {
    private val windowBrowser: JBCefBrowser = JBCefBrowser()

    init {
        windowBrowser.loadHTML("<h1>Hello world!<h1>")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
//        toolWindow.contentManager.addContent(windowBrowser)

//        val buttonPanel = JBPanel<JBPanel<*>>(FlowLayout()).apply {
//            add(backButton)
//            add(pauseButton)
//            add(nextButton)
//        }

//        val panel = JBPanel<JBPanel<*>>(BorderLayout()).apply {
//            add(windowBrowser.component)
//        }

        toolWindow.component.add(windowBrowser.component)

//        listenRedirection()
    }
}