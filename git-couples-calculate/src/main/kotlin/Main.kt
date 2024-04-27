import adapter.ClonedRepoAdapter
import adapter.GHApiAdapter
import pairsChartCalculator.calculateChart

suspend fun main_1() {
    val adapter = GHApiAdapter()
    val m = adapter.getCommitCountMap("master")
    calculateChart(m).filter {
        it.intersectedContribution.isNotEmpty()
    }.forEach {
        println("Devs ${it.devA} and ${it.devB}")
        println("Contributed mostly to")
        it.intersectedContribution.forEach {
            println("${it.filePath} changed ${it.fileChangeCommitCount} times")
        }

        println()
    }
}

suspend fun main() {
    val clonedRepoAdapter = ClonedRepoAdapter("/Userssadfsdf/electfreak/Desktop/frontend-trip-summary/src")
    val branches = clonedRepoAdapter.getBranches()
    println(branches)

    val m = clonedRepoAdapter.getCommitCountMap(clonedRepoAdapter.getBranches()[2])

    calculateChart(m).filter {
        it.intersectedContribution.isNotEmpty()
    }.forEach {
        println("Devs ${it.devA.name} and ${it.devB.name}")
        println("Contributed mostly to")

        it.intersectedContribution.forEach {
            println("${it.filePath} changed ${it.fileChangeCommitCount} times")
        }

        println()
    }
}


