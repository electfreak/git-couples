package com.example.plugin.jcef

import com.example.plugin.PluginCalculatorWrapper
import com.intellij.openapi.diagnostic.logger
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.QueryStringDecoder
import org.jetbrains.ide.BuiltInServerManager
import org.jetbrains.ide.HttpRequestHandler
import org.jetbrains.io.FileResponses
import org.jetbrains.io.response
import org.jetbrains.io.send

internal class StaticServer : HttpRequestHandler() {
    val serverUrl = "http://localhost:${BuiltInServerManager.getInstance().port}/$GIT_COUPLES_RESOURCES"
    private lateinit var calculatorWrapper: PluginCalculatorWrapper
    fun setCalculatorWrapper(pathToRepo: String) {
        calculatorWrapper = PluginCalculatorWrapper(pathToRepo)
    }

    fun getIndexUrl() = "$serverUrl/$BASE_DIRECTORY/index.html"

    init {
        logger.warn("Starting static server with url: $serverUrl")
    }

    override fun process(
        urlDecoder: QueryStringDecoder,
        request: FullHttpRequest,
        context: ChannelHandlerContext
    ): Boolean {
        val appropriateRequestString = urlDecoder
            .path()
            .split(GIT_COUPLES_RESOURCES)
            .getOrNull(1) ?: return false

        if (appropriateRequestString.startsWith("/$BASE_DIRECTORY")) {
            return sendResource(appropriateRequestString, request, context)
        } else if (appropriateRequestString.startsWith("/$API")) {
            return processApiRequest(appropriateRequestString, request, context)
        }

        return false
    }

    private fun processApiRequest(
        query: String,
        request: FullHttpRequest,
        context: ChannelHandlerContext
    ): Boolean {
        if (!::calculatorWrapper.isInitialized) {
            return false
        }

        var json: String = ""

        val apiQuery = query.split(API)[1].drop(1)

        if (apiQuery.startsWith("getBranches")) {
            json = calculatorWrapper.getBranches()
        }

        if (apiQuery.startsWith("getChart")) {
            val branch = apiQuery.split("getChart/")[1]
            println("branch: $branch")
            json = calculatorWrapper.getChart(branch)
        }

        val response = response("application/json", Unpooled.wrappedBuffer(json.toByteArray()))
        response.send(context.channel(), request)
        return true
    }

    private fun sendResource(
        resourceRelativePath: String,
        request: FullHttpRequest,
        context: ChannelHandlerContext
    ): Boolean {
        val contentType = FileResponses.getContentType(resourceRelativePath)
        val url = this::class.java.getResource(resourceRelativePath) ?: return false //use getResource from kotlin class this -> StaticServer
        val resultBuffer = Unpooled.wrappedBuffer(url.readBytes())
        val response = response(contentType, resultBuffer)
        response.send(context.channel(), request)
        return true
    }

    companion object {
        private val logger = logger<StaticServer>()
        const val GIT_COUPLES_RESOURCES = "gitCouplesResources"
        const val API = "api"
        private const val BASE_DIRECTORY = "frontend" // badly hardcoded

        val instance by lazy { checkNotNull(EP_NAME.findExtension(StaticServer::class.java)) }

    }
}
