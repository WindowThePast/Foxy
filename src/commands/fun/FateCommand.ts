import Command from "../../structures/BaseCommand";
import { SlashCommandBuilder } from "@discordjs/builders";

export default class FateCommand extends Command {
    constructor(client) {
        super(client, {
            name: "fate",
            description: "What is your fate with mentioned user?",
            category: "fun",
            dev: false,
            data: new SlashCommandBuilder()
                .setName("fate")
                .setDescription("[🤖 Fun] What is your fate with mentioned user?")
                .addUserOption(option => option.setName("user").setDescription("User to check fate with").setRequired(true))
        });
    }

    async execute(interaction, t) {
        const user = interaction.options.getUser("user");

        const list = [
            t('commands:fate.couple'),
            t('commands:fate.friend'),
            t('commands:fate.lover'),
            t('commands:fate.enemy'),
            t('commands:fate.sibling'),
            t('commands:fate.parent'),
            t('commands:fate.married')
        ]

        const rand = list[Math.floor(Math.random() * list.length)];
        await interaction.editReply(t('commands:fate.result', { user: user.username, fate: rand, mention: user }));
    }
}