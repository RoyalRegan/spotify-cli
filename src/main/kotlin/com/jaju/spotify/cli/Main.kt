@file:JvmName("Main")

package com.jaju.spotify.cli

import com.github.ajalt.clikt.core.subcommands
import com.jaju.spotify.cli.command.ConfigCommand
import com.jaju.spotify.cli.command.LoginCommand
import com.jaju.spotify.cli.command.RootCommand

fun main(args: Array<String>) = RootCommand
    .subcommands(ConfigCommand, LoginCommand)
    .main(args)