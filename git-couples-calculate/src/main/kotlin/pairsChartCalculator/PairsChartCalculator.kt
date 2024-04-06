package pairsChartCalculator

import kotlin.math.min

data class Developer(val name: String, val login: String?)
data class ContributionToFile(val filePath: String, val fileChangeCommitCount: Int)

class CommonPairContribution(
    val devA: Developer,
    contribByA: Map<String, Int>,
    val devB: Developer,
    contribByB: Map<String, Int>,
) : Comparable<CommonPairContribution> {

    val intersectedContribution: List<ContributionToFile>

    init {
        val overlappedFiles = contribByA.keys.intersect(contribByB.keys).associateWith { s ->
            min(contribByA.getValue(s), contribByB.getValue(s))
        }

        intersectedContribution = overlappedFiles.toList().sortedByDescending { (_, value) -> value }
            .map { (filePath, fileChangeCommitCount) -> ContributionToFile(filePath, fileChangeCommitCount) }
    }

    private val overallScore = intersectedContribution.sumOf { it.fileChangeCommitCount }

    override operator fun compareTo(other: CommonPairContribution): Int {
        return this.overallScore - other.overallScore
    }
}

typealias FileChangeCommitCountMap = MutableMap<String, Int>

fun calculateChart(contributionByDeveloper: Map<Developer, FileChangeCommitCountMap>): List<CommonPairContribution> {
    val pairs = contributionByDeveloper.keys.flatMapIndexed { index, i ->
        contributionByDeveloper.keys.drop(index + 1).map { j -> i to j }
    }

    return pairs.map { (devA, devB) ->
        CommonPairContribution(
            devA, contributionByDeveloper.getValue(devA), devB, contributionByDeveloper.getValue(devB)
        )
    }.sortedDescending()
}
