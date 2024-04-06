import adapter.ClonedRepoAdapter
import adapter.GHApiAdapter
import pairsChartCalculator.calculateChart

suspend fun main_1() {
    val adapter = GHApiAdapter()
    val m = adapter.iterateThroughCommits("master")
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
    val clonedRepoAdapter = ClonedRepoAdapter("/Users/electfreak/Desktop/thesis/phantomuserland")
    val branches = clonedRepoAdapter.getBranches()
    println(branches)

    val m = clonedRepoAdapter.iterateThroughCommits(clonedRepoAdapter.getBranches()[2])

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


