package adapter

import pairsChartCalculator.Developer

typealias FileChangeCommitCountMap = MutableMap<String, Int>

abstract class RepoAdapter {
    abstract suspend fun getContributionByDeveloper(branchName: String): Map<Developer, FileChangeCommitCountMap>
    abstract suspend fun getBranches(): List<String>
    private val emailToNames: HashMap<String, MutableSet<String>> = hashMapOf()

    protected fun MutableMap<String, FileChangeCommitCountMap>.incContributionToFiles(
        email: String,
        name: String,
        filePaths: List<String>
    ) {
        emailToNames.getOrPut(email) { hashSetOf() }.add(name)

        val fileChangeCommitCountMap =
            getOrPut(email) { filePaths.associateWith { 0 }.toMutableMap() }

        filePaths.forEach {
            fileChangeCommitCountMap[it] = fileChangeCommitCountMap.getOrDefault(it, 0).inc()
        }
    }

    protected fun MutableMap<String, FileChangeCommitCountMap>.toContributionByDeveloper() =
        this.mapKeys { Developer(it.key, emailToNames.getOrDefault(it.key, setOf())) }

    protected fun MutableMap<String, FileChangeCommitCountMap>.incContributionToFile(
        email: String,
        name: String,
        filePath: String
    ) = incContributionToFiles(email, name, listOf(filePath))

}