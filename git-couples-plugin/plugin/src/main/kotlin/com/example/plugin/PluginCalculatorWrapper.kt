package com.example.plugin

import adapter.ClonedRepoAdapter
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pairsChartCalculator.CommonPairContribution
import pairsChartCalculator.calculateChart

class PluginCalculatorWrapper(private val pathToRepo: String) {
    data class Chart(val pairs: List<CommonPairContribution>, val branch: String)

    private val adapter = ClonedRepoAdapter(pathToRepo)
    private var chart: Chart? = null
    private var branches: List<String>? = null

    fun getChart(branch: String): String {
        if (chart == null || chart?.branch != branch) {
            runBlocking {
                chart = Chart(calculateChart(ClonedRepoAdapter(pathToRepo).getContributionByDeveloper(branch)), branch)
            }
        }

        return Json.encodeToString(chart?.pairs)
    }

    fun getBranches(): String {
        runBlocking {
            branches = adapter.getBranches()
        }

        return Json.encodeToString(branches)
    }

    fun getIntersectedContribution(branch: String, id: Int): String {
        if (chart == null || chart?.branch != branch) {
            runBlocking {
                chart = Chart(calculateChart(ClonedRepoAdapter(pathToRepo).getContributionByDeveloper(branch)), branch)
            }
        }

        return Json.encodeToString(chart?.pairs?.getOrNull(id)?.getIntersectedContribution())
    }
}