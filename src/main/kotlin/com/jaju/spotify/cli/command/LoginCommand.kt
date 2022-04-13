package com.jaju.spotify.cli.command

import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.getSpotifyPkceAuthorizationUrl
import com.adamratzman.spotify.getSpotifyPkceCodeChallenge
import com.adamratzman.spotify.spotifyClientPkceApi
import com.github.ajalt.clikt.core.CliktCommand
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import kotlin.random.Random

object LoginCommand : CliktCommand(name = "login") {
    private const val clientId = "b516517b126a45ab985b52946fd9a6ad"
    private const val redirectUrl = "http://localhost:8888/callback"

    override fun run() = runBlocking {
        val codeVerifier = generateCodeVerifier()
        val code = waitForAuthorizationCode(codeVerifier)
        val api = spotifyClientPkceApi(
            clientId,
            redirectUrl,
            code,
            codeVerifier
        ).build()

        val user = api.users.getClientProfile()
        echo("Hello ${user.displayName}")
    }

    private fun generateLoginUrl(codeChallenge: String): String = getSpotifyPkceAuthorizationUrl(
        SpotifyScope.PLAYLIST_READ_PRIVATE,
        SpotifyScope.PLAYLIST_MODIFY_PRIVATE,
        SpotifyScope.USER_FOLLOW_READ,
        SpotifyScope.USER_LIBRARY_MODIFY,
        clientId = clientId,
        redirectUri = redirectUrl,
        codeChallenge = codeChallenge
    )

    private fun generateCodeVerifier(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('_', '-')
        return (0..Random.nextInt(42, 128))
            .map { allowedChars.random() }
            .joinToString("")
    }

    private suspend fun waitForAuthorizationCode(codeVerifier: String): String {
        val codeChallenge = getSpotifyPkceCodeChallenge(codeVerifier)
        val url = generateLoginUrl(codeChallenge)
        val tokenReceiver = Channel<String>()

        val server = HttpServer.create(InetSocketAddress("localhost", 8888), 0)
        server.createContext("/callback") { exchange ->
            val queryParams = exchange.requestURI.query.split("&")
                .map {
                    it.split("=")
                        .toPair()
                }
                .associateBy({ it.first }, { it.second })

            tokenReceiver.trySendBlocking(queryParams["code"]!!)
            tokenReceiver.close()

            exchange.sendResponseHeaders(200, 0)
            exchange.responseBody.close()
        }

        server.start()
        echo("Login via: $url")
        val token = tokenReceiver.receive()
        server.stop(100)
        return token
    }

    private fun <T> List<T>.toPair(): Pair<T, T> {
        return component1() to component2()
    }
}