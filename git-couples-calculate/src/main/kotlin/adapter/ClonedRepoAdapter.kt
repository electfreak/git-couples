package adapter

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import pairsChartCalculator.Developer
import pairsChartCalculator.FileChangeCommitCountMap
import java.io.File

class ClonedRepoAdapter(pathToRepo: String) : RepoAdapter() {
    private val repositoryBuilder = FileRepositoryBuilder()
    private val repository = repositoryBuilder.setGitDir(File("$pathToRepo/.git"))
        .readEnvironment()
        .findGitDir()
        .build()

    private val git = Git(repository)
    override suspend fun getCommitCountMap(branchName: String): Map<Developer, FileChangeCommitCountMap> {
        val developerToFileChanges = mutableMapOf<Developer, FileChangeCommitCountMap>()
        val commits = git.log().add(repository.resolve(branchName)).call()
        for (commit in commits) {
            val dev = Developer(commit.authorIdent.name, null)

            TreeWalk(repository).use { treeWalk ->
                treeWalk.addTree(commit.tree)
                treeWalk.isRecursive = true
                while (treeWalk.next()) {
                    developerToFileChanges.incContributionToFile(dev, treeWalk.pathString)
                }
            }
        }

        return developerToFileChanges
    }

    override suspend fun getBranches() = git.branchList().call().map { it.name }
}