package com.icapps.build.gradle.plugin.tasks.bitbucket

import com.cdancy.bitbucket.rest.BitbucketClient
import com.cdancy.bitbucket.rest.domain.pullrequest.MinimalRepository
import com.cdancy.bitbucket.rest.domain.pullrequest.ProjectKey
import com.cdancy.bitbucket.rest.domain.pullrequest.Reference
import com.cdancy.bitbucket.rest.options.CreatePullRequest
import org.junit.Test

/**
 * @author Koen Van Looveren
 */
open class BitbucketTest {

    @Test
    fun testPr() {

        val repoName = "android_gradle_demo"
        val repoSlug = "android_gradle_demo"
        val project = "android_build"

        val client = BitbucketClient.builder()
                .endPoint("https://icapps.atlassian.net")
                .token("UVSympusXNYT6Xj6CeQY")
                .build()
        println(client.api().systemApi().version().buildDate())
        println(client.api().systemApi().version().buildNumber())
        println(client.api().systemApi().version().displayName())
        println(client.api().systemApi().version().version())
/*
        val currentBranch = "feature/bitbucket-integration"

        val prTitle = "Test From Gradle Plugin" // eerst commit die afwijkt van branch waar ge in wilt mergen
        val prDescription = "Description From Gradle Pugin" // alle commits

        val branchToMerge = BitbucketPrTask.branchPrefix + currentBranch
        val branchMergeInto = BitbucketPrTask.branchPrefix + BitbucketPrTask.branchName

        val projKey = ProjectKey.create(project)
        val repo = MinimalRepository.create(repoSlug, null, projKey)

        val fromRef = Reference.create(branchToMerge, repo, branchToMerge)
        val toRef = Reference.create(null, repo)

        val pr = CreatePullRequest.create(prTitle, prDescription, fromRef, toRef, null, null)
        val result = client.api().pullRequestApi().create(project, repoName, pr)
        */
    }
}