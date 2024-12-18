package net.cakeyfox.foxy.welcomer.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User

class WelcomerJSONParser {
    val objectMapper = ObjectMapper()

    fun parseDiscordJsonMessage(
        jsonString: String,
        placeholders: Map<String, String?>
    ): Pair<String, List<MessageEmbed>> {
        val rootNode: JsonNode = objectMapper.readTree(jsonString)

        val content = rootNode.get("content")?.asText("")?.let {
            replacePlaceholders(it, placeholders)
        } ?: ""

        val embedsList = rootNode.get("embeds")?.let { embedsNode ->
            if (embedsNode.isArray) {
                embedsNode.mapNotNull { embedObj ->
                    if (embedObj is JsonNode) {
                        EmbedBuilder().apply {
                            embedObj.get("title")?.let {
                                if (!it.isNull) {
                                    it.asText()?.takeIf { it.isNotEmpty() }?.let {
                                        setTitle(replacePlaceholders(it, placeholders))
                                    }
                                }
                            }

                            embedObj.get("description")?.let {
                                if (!it.isNull) {
                                    it.asText()?.takeIf { it.isNotEmpty() }?.let {
                                        setDescription(replacePlaceholders(it, placeholders))
                                    }
                                }
                            }

                            embedObj.get("color")?.asInt()?.takeIf { it != 0 }?.let {
                                setColor(it)
                            }

                            embedObj.get("footer")?.get("text")?.let {
                                if (!it.isNull) {
                                    it.asText()?.takeIf { it.isNotEmpty() }?.let {
                                        setFooter(replacePlaceholders(it, placeholders), null)
                                    }
                                }
                            }

                            embedObj.get("image")?.get("url")?.let {
                                if (!it.isNull) {
                                    it.asText()?.takeIf { it.isNotEmpty() }?.let { url ->
                                        val replacedUrl = replacePlaceholders(url, placeholders)
                                        setImage(replacedUrl)
                                    }
                                }
                            }

                            embedObj.get("thumbnail")?.get("url")?.let {
                                if (!it.isNull) {
                                    it.asText()?.takeIf { it.isNotEmpty() }?.let { url ->
                                        val replacedUrl = replacePlaceholders(url, placeholders)
                                        setThumbnail(replacedUrl)
                                    }
                                }
                            }

                        }.build()
                    } else null
                }
            } else {
                emptyList()
            }
        } ?: emptyList()

        return Pair(content, embedsList)
    }


    fun getPlaceholders(guild: Guild, user: User): Map<String, String?> {
        return mapOf(
            "{user}" to user.globalName,
            "{@user}" to user.asMention,
            "{user.username}" to user.name,
            "{user.id}" to user.id,
            "{guild.name}" to guild.name,
            "{guild.id}" to guild.id,
            "{guild.memberCount}" to guild.memberCount.toString(),
            "{user.avatar}" to user.avatarUrl,
            "{guild.icon}" to guild.iconUrl
        )
    }

    fun replacePlaceholders(message: String, placeholders: Map<String, String?>): String {
        var formattedMessage = message

        placeholders.forEach { (key, value) ->
            formattedMessage = formattedMessage.replace(key, value ?: "")
        }

        return formattedMessage
    }
}