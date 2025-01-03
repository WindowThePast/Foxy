package net.cakeyfox.foxy.command.vanilla.social

import net.cakeyfox.common.FoxyEmotes
import net.cakeyfox.foxy.command.FoxyInteractionContext
import net.cakeyfox.foxy.command.structure.FoxyCommandExecutor
import net.cakeyfox.foxy.utils.pretty
import net.cakeyfox.foxy.utils.profile.FoxyProfileRender
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.utils.FileUpload

class ProfileViewExecutor: FoxyCommandExecutor() {
    override suspend fun execute(context: FoxyInteractionContext) {
        context.defer()

        val user = context.getOption<User>("user") ?: context.event.user

        val profile = FoxyProfileRender(context).create(user) ?: return
        val file = FileUpload.fromData(profile, "profile.png")

        context.reply {
            content = pretty(
                FoxyEmotes.FoxyDrinkingCoffee,
                context.locale["profile.view", user.asMention]
            )

            files.plusAssign(file)
        }
    }
}