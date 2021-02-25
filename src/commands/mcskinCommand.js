const colors = require('../structures/color');

module.exports = {
  name: 'mcskin',
  aliases: ['mcskin'],
  cooldown: 5,
  guildOnly: false,

  async run(client, message, args) {
    const user = args.join(' ');

    if (!user) return message.channel.send('<:Minecraft:804858374780878868> **|** Especifique um usuário');
    const skin = `https://mc-heads.net/skin/${user}`;

    const discord = require('discord.js');

    const embed = new discord.MessageEmbed()
      .setColor('BLUE')
      .setTitle(`<:Minecraft:804858374780878868> Skin de ${user}`)
      .setImage(skin)
      .setColor(colors.mine);
    await message.channel.send(embed);
  },
};
