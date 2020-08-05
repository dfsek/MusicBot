/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlaylistPlayCmd extends MusicCommand
{
    public PlaylistPlayCmd(Bot bot)
    {
        super(bot);
        this.name = "play";
        this.aliases = new String[]{"p"};
        this.arguments = "<name>";
        this.help = "plays the provided playlist";
        this.beListening = true;
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
            event.reply("Playlist name not specified. Automagically:tm: playing playlist \"gamin\"");
        }
        Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getArgs().isEmpty() ? "gamin" : event.getArgs());
        if(playlist==null)
        {
            event.replyError("Unable to locate playlist `"+event.getArgs()+"`");
            return;
        }
        event.getChannel().sendMessage(" Loading playlist **"+(event.getArgs().isEmpty() ? "gamin" : event.getArgs())+"** containing **"+playlist.getItems().size()+"** items...").queue(m ->
        {
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            playlist.loadTracks(bot.getPlayerManager(), (at)->handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
                StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                        ? event.getClient().getWarning()+" No tracks were loaded!"
                        : event.getClient().getSuccess()+" Loaded **"+playlist.getTracks().size()+"** tracks!");
                if(!playlist.getErrors().isEmpty())
                    builder.append("\nThe following tracks failed to load:");
                playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex()+1).append("]` **").append(err.getItem()).append("**: ").append(err.getReason()));
                String str = builder.toString();
                if(str.length()>2000)
                    str = str.substring(0,1997)+"...";
                m.editMessage(FormatUtil.filter(str)).queue();
            });
        });
    }
}
