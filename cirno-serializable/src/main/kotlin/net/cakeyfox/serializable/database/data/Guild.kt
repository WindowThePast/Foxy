package net.cakeyfox.serializable.database.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import net.cakeyfox.serializable.database.utils.MongoDateSerializer

@Serializable
data class Guild(
    val _id: String,
    val GuildJoinLeaveModule: WelcomerModule,
    val AutoRoleModule: AutoRoleModule,
    val antiRaidModule: AntiRaidModule,
    val premiumKeys: List<Key> = emptyList(),
    val guildSettings: GuildSettings,
    val dashboardLogs: List<DashboardLog> = emptyList(),
)

@Serializable
data class WelcomerModule(
    val isEnabled: Boolean = false,
    val joinMessage: String? = null,
    val alertWhenUserLeaves: Boolean = false,
    val leaveMessage: String? = null,
    val joinChannel: String? = null,
    val leaveChannel: String? = null,
)

@Serializable
data class AntiRaidModule(
    val isEnabled: Boolean = false,
    val handleJoin: Boolean = false,
    val messagesThreshold: Int = 8,
    val newUsersThreshold: Int = 5,
    val alertChannel: String? = null,
    val action: String = "TIMEOUT",
    val timeoutDuration: Long = 10000,
    val banDuration: Int = 7,
    val whitelistedChannels: List<String> = emptyList(),
    val whitelistedRoles: List<String> = emptyList(),
)

@Serializable
data class AutoRoleModule(
    val isEnabled: Boolean = false,
    val roles: List<String> = emptyList(),
)

@Serializable
data class GuildSettings(
    val prefix: String = "f!",
    val language: String = "pt-BR",
    val disabledCommands: List<String> = emptyList(),
    val blockedChannels: List<String> = emptyList(),
    val sendMessageIfChannelIsBlocked: Boolean = false,
    val deleteMessageIfCommandIsExecuted: Boolean = false,
    val usersWhoCanAccessDashboard: List<String> = emptyList(),
)

@Serializable
data class DashboardLog(
    val _id: String,
    val user: String,
    val action: String,
    @Serializable(with = MongoDateSerializer::class)
    val date: Instant
)