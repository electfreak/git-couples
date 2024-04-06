package adapter

import pairsChartCalculator.Developer
import pairsChartCalculator.FileChangeCommitCountMap

abstract class Adapter {
    abstract suspend fun iterateThroughCommits(branchName: String): Map<Developer, FileChangeCommitCountMap>

    protected fun MutableMap<Developer, FileChangeCommitCountMap>.incContributionToFiles(
        dev: Developer,
        filePaths: List<String>
    ) {
        val fileChangeCommitCountMap =
            this.getOrPut(dev) { filePaths.associateWith { 0 }.toMutableMap() }

        filePaths.forEach {
            fileChangeCommitCountMap[it] = fileChangeCommitCountMap.getOrDefault(it, 0) + 1
        }
    }

    protected fun MutableMap<Developer, FileChangeCommitCountMap>.incContributionToFile(
        dev: Developer,
        filePath: String
    ) {
        val fileChangeCommitCountMap =
            this.getOrPut(dev) { mutableMapOf(filePath to 0) }

        fileChangeCommitCountMap[filePath] = fileChangeCommitCountMap.getOrDefault(filePath, 0) + 1
    }

    abstract suspend fun getBranches(): List<String>
}