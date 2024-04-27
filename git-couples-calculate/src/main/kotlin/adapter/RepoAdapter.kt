package adapter

import pairsChartCalculator.Developer
import pairsChartCalculator.FileChangeCommitCountMap

abstract class RepoAdapter {
    abstract suspend fun getCommitCountMap(branchName: String): Map<Developer, FileChangeCommitCountMap>

    protected fun MutableMap<Developer, FileChangeCommitCountMap>.incContributionToFiles( //typealias
        dev: Developer,
        filePaths: List<String>
    ) {
        val fileChangeCommitCountMap =
            getOrPut(dev) { filePaths.associateWith { 0 }.toMutableMap() }

        filePaths.forEach {
            fileChangeCommitCountMap[it] = fileChangeCommitCountMap.getOrDefault(it, 0).inc()
        }
    }

    protected fun MutableMap<Developer, FileChangeCommitCountMap>.incContributionToFile(
        dev: Developer,
        filePath: String
    ) = incContributionToFiles(dev, listOf(filePath))


    abstract suspend fun getBranches(): List<String>
}