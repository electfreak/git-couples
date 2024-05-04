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
    val serverUrl = "http://localhost:${BuiltInServerManager.getInstance().port}/$GIT_COUPLES_URL"
    private lateinit var calculatorWrapper: PluginCalculatorWrapper
    fun setCalculatorWrapper(pathToRepo: String) {
        calculatorWrapper = PluginCalculatorWrapper(pathToRepo)
    }

    fun getIndexUrl() = "$serverUrl/$BASE_DIRECTORY/index.html"

    init {
        logger.warn("Starting static server with url: $serverUrl")
    }

    private fun QueryStringDecoder.getAppropriateRequestString() = this
        .path()
        .split(GIT_COUPLES_URL)
        .getOrNull(1)

    override fun process(
        urlDecoder: QueryStringDecoder,
        request: FullHttpRequest,
        context: ChannelHandlerContext
    ): Boolean {
        val appropriateRequestString = urlDecoder.getAppropriateRequestString() ?: return false

        if (appropriateRequestString.startsWith("/$BASE_DIRECTORY")) {
            return sendResource(appropriateRequestString, request, context)
        } else if (appropriateRequestString.startsWith("/$GIT_COUPLES_API")) {
            return processApiRequest(urlDecoder, request, context)
        }

        return false
    }

    private fun processApiRequest(
        urlDecoder: QueryStringDecoder,
        request: FullHttpRequest,
        context: ChannelHandlerContext
    ): Boolean {
        val query = urlDecoder.getAppropriateRequestString() ?: return false

        if (!::calculatorWrapper.isInitialized) {
            return false
        }

        val apiQuery = query.split(GIT_COUPLES_API)[1].drop(1)

        val json = when {
            apiQuery.startsWith("getBranches") -> {
                calculatorWrapper.getBranches()
            }
            apiQuery.startsWith("getChart") -> {
                val branch = apiQuery.split("getChart/")[1]
                calculatorWrapper.getChart(branch)
            }
            apiQuery.startsWith("getIntersectedContribution") -> {
                val id = urlDecoder.parameters()["id"]?.first() ?: return false
                val branch = urlDecoder.parameters()["branch"]?.first() ?: return false
                calculatorWrapper.getIntersectedContribution(branch, id.toInt())
            }
            else -> error("Invalid request to API")
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
        val url = this::class.java.getResource(resourceRelativePath)
            ?: return false //use getResource from kotlin class this -> StaticServer
        val resultBuffer = Unpooled.wrappedBuffer(url.readBytes())
        val response = response(contentType, resultBuffer)
        response.send(context.channel(), request)
        return true
    }

    companion object {
        private val logger = logger<StaticServer>()
        const val GIT_COUPLES_URL = "gitCouplesResources"
        const val GIT_COUPLES_API = "api"
        private const val BASE_DIRECTORY = "frontend" // badly hardcoded

        val instance by lazy { checkNotNull(EP_NAME.findExtension(StaticServer::class.java)) }

    }
}
