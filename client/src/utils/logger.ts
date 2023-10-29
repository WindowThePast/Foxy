import { bot } from "../index";
import config from '../../config.json';
import { User } from "discordeno/transformers";

const logger = {
    error: (...args: any[]): void => {
        console.error(`[${new Date().toLocaleDateString()} ${new Date().toLocaleTimeString()}] \u001b[31mERROR\u001b[0m >`, ...args);
    },

    info: (...args: any[]): void => {
        console.info(`[${new Date().toLocaleDateString()} ${new Date().toLocaleTimeString()}] \u001b[94mINFO\u001b[0m >`, ...args);
    },

    warn: (...args: any[]): void => {
        console.warn(`[${new Date().toLocaleDateString()} ${new Date().toLocaleTimeString()}] \u001b[33mWARN\u001b[0m >`, ...args);
    },

    criticalError: (...args: any[]): void => {
        console.error(`[${new Date().toLocaleDateString()} ${new Date().toLocaleTimeString()}] \u001b[91mCRITICAL ERROR\u001b[0m >`, ...args);
        process.exit(1);
    },

    log: (...args: any[]): void => {
        console.log(...args);
    },

    commandLog: (command: string, author: User, guild: string, args: string): void => {
        bot.helpers.sendWebhookMessage(config.webhooks.event_log.id, config.webhooks.event_log.token, {
            embeds: [{
                title: "✨ | Comando executado",
                description: `**Comando:** ${command}\n**Autor:** ${author.username} (${author.id})\n**Servidor:** ${guild}\n**Argumentos:** ${args}`,
            }]
        });
    }
}

export { logger };