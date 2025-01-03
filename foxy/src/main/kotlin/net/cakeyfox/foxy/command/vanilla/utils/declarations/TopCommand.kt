package net.cakeyfox.foxy.command.vanilla.utils.declarations

import net.cakeyfox.foxy.command.structure.FoxyCommandDeclarationWrapper
import net.cakeyfox.foxy.command.vanilla.utils.TopCakesExecutor

class TopCommand : FoxyCommandDeclarationWrapper {
    override fun create() = command(
        "top",
        "top.description",
    ) {
        subCommand(
            "cakes",
            "top.cakes.description",
            baseName = this@command.name
        ) {
            executor = TopCakesExecutor()
        }

        // TODO: Implement this command later
//        subCommand(
//            "commands",
//            "top.commands.description",
//        ) {
//            executor = TopCommandsExecutor()
//        }
    }
}