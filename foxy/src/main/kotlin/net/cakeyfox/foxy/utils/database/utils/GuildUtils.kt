package net.cakeyfox.foxy.utils.database.utils

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.cakeyfox.foxy.utils.database.MongoDBClient
import net.cakeyfox.serializable.database.data.*
import org.bson.Document
import kotlin.reflect.KClass
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import mu.KotlinLogging
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmName

class GuildUtils(
    private val db: MongoDBClient
) {
    private val logger = KotlinLogging.logger(this::class.jvmName)

    val json = Json {
        ignoreUnknownKeys = true
    }

    fun getGuild(guildId: String): Guild {
        return updateGuildWithNewFields(guildId)
    }

    fun deleteGuild(guildId: String) {
        val guilds = db.database!!.getCollection("guilds")
        val query = Document("_id", guildId)
        guilds.deleteOne(query)
    }

    private fun createGuild(guildId: String): Guild {
        val guilds = db.database!!.getCollection("guilds")

        val newGuild = Guild(
            _id = guildId,
            GuildJoinLeaveModule = WelcomerModule(
                isEnabled = false,
                joinChannel = null,
                alertWhenUserLeaves = false,
                leaveChannel = null,
                leaveMessage = null,
                joinMessage = null,
            ),
            antiRaidModule = AntiRaidModule(),
            AutoRoleModule = AutoRoleModule(
                isEnabled = false,
                roles = emptyList(),
            ),
            premiumKeys = emptyList(),
            guildSettings = GuildSettings(
                prefix = "f!",
                disabledCommands = emptyList(),
                blockedChannels = emptyList(),
                usersWhoCanAccessDashboard = emptyList(),
                deleteMessageIfCommandIsExecuted = false,
                sendMessageIfChannelIsBlocked = false,
            ),
            dashboardLogs = emptyList(),
        )

        val documentToJSON = db.json.encodeToString(newGuild)
        val document = Document.parse(documentToJSON)
        guilds.insertOne(document)

        return newGuild
    }

    // Adding missing fields if necessary

    private fun updateGuildWithNewFields(guildId: String): Guild {
        val guilds = db.database!!.getCollection("guilds")

        val query = Document("_id", guildId)
        val existingDocument = guilds.find(query).firstOrNull() ?: return createGuild(guildId)

        val documentToJSON = existingDocument.toJson()

        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(documentToJSON)

        val updatedJsonNode = ensureFields(Guild::class, jsonNode, guildId)

        val updatedGuild = db.json.decodeFromString<Guild>(updatedJsonNode.toString())

        val updatedDocument = Document.parse(db.json.encodeToString(updatedGuild))
        val update = Document("\$set", updatedDocument)
        guilds.updateOne(query, update)

        return updatedGuild
    }

    // Check missing fields and add them

    private fun ensureFields(dataClass: KClass<*>, jsonNode: JsonNode, guildId: String): JsonNode {
        val updatedJsonNode = (jsonNode as ObjectNode)

        dataClass.memberProperties.forEach { property ->
            val fieldName = property.name

            if (!updatedJsonNode.has(fieldName)) {
                logger.info { "Missing field $fieldName on guild $guildId, adding it." }
                val defaultValue = getDefaultValue(property.returnType.classifier as KClass<*>)
                updatedJsonNode.putPOJO(fieldName, defaultValue)
            }

            if (updatedJsonNode.has(fieldName) && updatedJsonNode.get(fieldName).isNull) {
                logger.info { "Field $fieldName don't have a value, adding a value." }
                val defaultValue = getDefaultValue(property.returnType.classifier as KClass<*>)
                updatedJsonNode.putPOJO(fieldName, defaultValue)
            }
        }

        return updatedJsonNode
    }

    private fun getDefaultValue(type: KClass<*>): Any? {
        return when (type) {
            String::class -> "owo what's this?"
            Int::class -> 0
            Boolean::class -> false
            Double::class -> 0.0
            Long::class -> 0L
            List::class -> emptyList<Any>()
            AntiRaidModule::class -> AntiRaidModule()
            AutoRoleModule::class -> AutoRoleModule()
            WelcomerModule::class -> WelcomerModule()
            GuildSettings::class -> GuildSettings()
            else -> null
        }
    }
}