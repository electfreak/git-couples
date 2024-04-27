package com.example.plugin

import adapter.ClonedRepoAdapter
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pairsChartCalculator.calculateChart

class PluginCalculatorWrapper(private val pathToRepo: String) {
    private val adapter = ClonedRepoAdapter(pathToRepo)

    fun getChart(branch: String): String {
        val chart: String
        runBlocking {
            chart = Json.encodeToString(calculateChart(ClonedRepoAdapter(pathToRepo).getCommitCountMap(branch)))
        }

        return chart
    }

    fun getBranches(): String {
        val branches: String
        runBlocking {
            branches = Json.encodeToString(adapter.getBranches())
        }

        return branches
    }
}