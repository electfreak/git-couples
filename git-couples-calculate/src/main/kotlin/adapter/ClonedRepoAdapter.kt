package adapter

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import pairsChartCalculator.Developer
import java.io.File
import java.io.IOException


class ClonedRepoAdapter(pathToRepo: String) : RepoAdapter() {
    private val repositoryBuilder = FileRepositoryBuilder()
    private val repository = repositoryBuilder.setGitDir(File("$pathToRepo/.git"))
        .readEnvironment()
        .findGitDir()
        .build()

    private val git = Git(repository)
    override suspend fun getContributionByDeveloper(branchName: String): Map<Developer, FileChangeCommitCountMap> {
        val developerToFileChanges = mutableMapOf<String, FileChangeCommitCountMap>()
        val commits = git.log().add(repository.resolve(branchName)).call()
        for (commit in commits) {
            developerToFileChanges.incContributionToFiles(
                commit.authorIdent.emailAddress,
                commit.authorIdent.name,
                listChangedFiles(commit)
            )
        }

        return developerToFileChanges.toContributionByDeveloper()
    }

    @Throws(IOException::class, GitAPIException::class)
    fun listChangedFiles(commit: RevCommit): MutableList<String> {
        val files = mutableListOf<String>()
        RevWalk(repository).use { revWalk ->
            if (commit.parentCount == 0) {
                TreeWalk(repository).use { treeWalk ->
                    treeWalk.addTree(commit.tree)
                    treeWalk.isRecursive = true
                    while (treeWalk.next()) {
                        files.add(treeWalk.pathString)
                    }

                    return files
                }
            }

            val parentCommit = revWalk.parseCommit(commit.getParent(0).id)

            TreeWalk(repository).use { treeWalk ->
                treeWalk.addTree(parentCommit.tree)
                treeWalk.addTree(commit.tree)
                treeWalk.isRecursive = true
                while (treeWalk.next()) {
                    if (treeWalk.pathString.isBlank()) {
                        continue
                    }

                    if (treeWalk.isSubtree) {
                        treeWalk.enterSubtree()
                        continue
                    }

                    val path = treeWalk.pathString
                    val objectId =
                        treeWalk.getObjectId(1)
                    val prevObjectId =
                        treeWalk.getObjectId(0)

                    if (prevObjectId == null || !prevObjectId.equals(objectId)) {
                        files.add(path)
                    }
                }
            }
        }

        return files
    }

    override suspend fun getBranches() = git.branchList().call().map { it.name }
}