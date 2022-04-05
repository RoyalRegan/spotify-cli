package com.jaju.spotify.cli.command

import com.github.ajalt.clikt.core.CliktCommand

object RootCommand : CliktCommand(name = "spotify-cli") {
    override fun run() = Unit
}