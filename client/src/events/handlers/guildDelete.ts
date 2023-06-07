import { bot } from "../../";
import config from "../../../config.json";

const setGuildDeleteEvent = (): void => {
    bot.events.guildDelete = async (_, guild) => {
        const guildId = guild;
        await bot.database.removeGuild(guildId).then((document) => {
            if (document) {
                setTimeout(() => {
                    bot.helpers.sendWebhookMessage(config.webhooks.join_leave_guild.id, config.webhooks.join_leave_guild.token, {
                        embeds: [{
                            title: `<:emoji:${bot.emotes.FOXY_CRY}> **|** Fui removida de um servidor!`,
                            description: `**ID:** ${guild}`,
                            footer: {
                                text: `Servidor removido do banco de dados!`
                            }
                        }]
                    });
                }, 500);
            }
        });
    }
}

export { setGuildDeleteEvent }