package net.cakeyfox.foxy.utils.database.utils

import com.mongodb.client.MongoCollection
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.encodeToString
import net.cakeyfox.foxy.FoxyInstance
import net.cakeyfox.serializable.database.data.*
import org.bson.Document

class UserUtils(
    private val instance: FoxyInstance
) {
    fun getDiscordUser(userId: String): FoxyUser {
        val collection: MongoCollection<Document> = instance.mongoClient.database!!.getCollection("users")

        val query = Document("_id", userId)
        val existingUserDocument = collection.find(query).firstOrNull()
            ?: return createUser(userId)

        val documentToJSON = existingUserDocument.toJson()

        return instance.mongoClient.json.decodeFromString<FoxyUser>(documentToJSON)
    }


    fun updateUser(userId: String, updates: Map<String, Any>) {
        val query = Document("_id", userId)
        val update = Document("\$set", Document(updates))

        instance.mongoClient.users.updateOne(query, update)
    }

    private fun createUser(userId: String): FoxyUser {
        val newUser = FoxyUser(
            _id = userId,
            userCreationTimestamp = Clock.System.now(),
            isBanned = false,
            banDate = null,
            banReason = "",
            userCakes = UserCakes(balance = 0, lastDaily = null),
            marryStatus = MarryStatus(
                marriedWith = null,
                marriedDate = null,
                cantMarry = false
            ),
            userProfile = UserProfile(
                decoration = "",
                decorationList = emptyList(),
                background = "default",
                backgroundList = listOf("default"),
                repCount = 0,
                lastRep = null,
                layout = "default",
                layoutList = listOf("default"),
                aboutme = ""
            ),
            userPremium = UserPremium(
                premium = false,
                premiumDate = null,
                premiumType = ""
            ),
            userSettings = UserSettings(language = "pt-br"),
            petInfo = PetInfo(
                name = "",
                type = "",
                rarity = "",
                level = 0,
                hungry = 100,
                happy = 100,
                health = 100,
                lastHungry = null,
                lastHappy = null,
                isDead = false,
                isClean = true,
                food = emptyList()
            ),
            userTransactions = emptyList(),
            premiumKeys = emptyList(),
            roulette = Roulette(availableSpins = 5),
            lastVote = null,
            notifiedForVote = false,
            voteCount = 0
        )

        val documentToJSON = instance.mongoClient.json.encodeToString(newUser)
        val document = Document.parse(documentToJSON)
        document["userCreationTimestamp"] = java.util.Date.from(newUser.userCreationTimestamp.toJavaInstant())

        instance.mongoClient.users.insertOne(document)

        return newUser
    }
}