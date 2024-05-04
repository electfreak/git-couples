package pairsChartCalculator

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.min

@Serializable
data class Developer(val email: String, val names: Set<String>, val login: String? = null)

@Serializable
data class ContributionToFile(val filePath: String, val fileChangeCommitCount: Int)

@Serializable
class CommonPairContribution(
    val devA: Developer,
    @Transient val contribByA: Map<String, Int> = mapOf(),
    val devB: Developer,
    @Transient val contribByB: Map<String, Int> = mapOf(),
) : Comparable<CommonPairContribution> {

    private val overallScore: Int

    @Transient
    private val overlappedFiles = contribByA.keys.intersect(contribByB.keys).associateWith { s ->
        min(contribByA.getValue(s), contribByB.getValue(s))
    }

    init {
        val overlappedFiles = contribByA.keys.intersect(contribByB.keys).associateWith { s ->
            min(contribByA.getValue(s), contribByB.getValue(s))
        }

        overallScore = overlappedFiles.values.sum()
    }

    fun getIntersectedContribution(): List<ContributionToFile> {
        return overlappedFiles.toList().sortedByDescending { (_, value) -> value }
            .map { (filePath, fileChangeCommitCount) -> ContributionToFile(filePath, fileChangeCommitCount) }
    }

    override operator fun compareTo(other: CommonPairContribution): Int {
        return this.overallScore - other.overallScore
    }
}

typealias FileChangeCommitCountMap = MutableMap<String, Int>

fun calculateChart(contributionByDeveloper: Map<Developer, FileChangeCommitCountMap>): List<CommonPairContribution> {
    println(contributionByDeveloper.keys)
    val pairs = contributionByDeveloper.keys.flatMapIndexed { index, i -> // TODO i,j -> to normal variable names
        contributionByDeveloper.keys.drop(index + 1).map { j -> i to j }
    }

    return pairs.map { (devA, devB) ->
        CommonPairContribution(
            devA, contributionByDeveloper.getValue(devA), devB, contributionByDeveloper.getValue(devB)
        )
    }.sortedDescending()
}