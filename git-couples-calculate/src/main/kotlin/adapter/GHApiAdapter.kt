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

class GHApiAdapter : Adapter() {
    val repo = "torvalds/linux"

    val client
        get() = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens("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", "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")
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

    suspend fun getCommits(): CommitList {
        val response: HttpResponse = client.get("https://api.github.com/repos/$repo/commits")
        return response.body<CommitList>().also {
            client.close()
        }
    }

    suspend fun getCommit(sha: String): Commit {
        val response: HttpResponse = client.get("https://api.github.com/repos/$repo/commits/$sha")
        try {
            return response.body<Commit>().also {
                client.close()
            }
        } catch (e: Exception) {
            println(response.bodyAsText())
            throw e
        }
    }

    override suspend fun iterateThroughCommits(branchName: String): Map<Developer, FileChangeCommitCountMap> {
        val res = mutableMapOf<Developer, FileChangeCommitCountMap>()

        val commits = getCommits()

        for (sha in commits) {
            val commit = getCommit(sha.sha)
            res.incContributionToFiles(
                Developer(commit.commit.author.name, commit.author?.login),
                commit.files.map { it.filename })

        }

        return res
    }

}