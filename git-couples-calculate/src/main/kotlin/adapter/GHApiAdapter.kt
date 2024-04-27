package adapter

import pairsChartCalculator.Developer
import pairsChartCalculator.FileChangeCommitCountMap
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

typealias CommitList = List<GHApiAdapter.CommitInList>
const val repo = "torvalds/linux"

class GHApiAdapter : RepoAdapter() {

    private val client
        get() = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens("token", "token")
                    }
                }
            }
        }

    @Serializable
    data class BranchInList(val name: String)

    @Serializable
    data class CommitInList(val sha: String)

    @Serializable
    data class Commit(val sha: String, val author: Author?, val files: List<FileInCommit>, val commit: CommitInCommit) {
        @Serializable
        data class FileInCommit(val filename: String)

        @Serializable
        data class AuthorInCommit(val name: String)

        @Serializable
        data class CommitInCommit(val author: AuthorInCommit)
    }

    @Serializable
    data class Author(val login: String)

    override suspend fun getBranches(): List<String> {
        val response: HttpResponse = client.get("https://api.github.com/repos/$repo/branches")
        return response.body<List<BranchInList>>().map { it.name }.also {
            client.close()
        }
    }

    private suspend fun getCommits(): CommitList {
        val response: HttpResponse = client.get("https://api.github.com/repos/$repo/commits")
        return response.body<CommitList>().also {
            client.close()
        }
    }

    private suspend fun getCommit(sha: String): Commit {
        val response: HttpResponse = client.get("https://api.github.com/repos/$repo/commits/$sha") // TODO resolve duplicates
        try {
            return response.body<Commit>().also {
                client.close()
            }
        } catch (e: Exception) {
            println(response.bodyAsText())
            throw e
        }
    }

    override suspend fun getCommitCountMap(branchName: String): Map<Developer, FileChangeCommitCountMap> {
        val res = mutableMapOf<Developer, FileChangeCommitCountMap>() //TODO

        val commits = getCommits()

        for (commit in commits) {
            val realCommit = getCommit(commit.sha)
            res.incContributionToFiles(
                Developer(realCommit.commit.author.name, realCommit.author?.login),
                realCommit.files.map { it.filename })

        }

        return res
    }

}