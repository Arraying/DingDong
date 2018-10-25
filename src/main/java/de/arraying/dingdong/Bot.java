package de.arraying.dingdong;

import de.arraying.kotys.JSON;
import de.arraying.kotys.JSONArray;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2018 Arraying
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public enum Bot {

    /**
     * The bot instance.
     */
    INSTANCE;

    private static String token;
    private JDA jda;

    /**
     * Starts the bot.
     * @param args The arguments. The first argument is the bot token.
     */
    public static void main(String[] args) {
        if(args.length == 0
                || args[0].isEmpty()) {
            throw new IllegalArgumentException("non empty bot token expected as first runtime arg");
        }
        token = args[0];
        INSTANCE.run();
    }

    /**
     * Gets the JDA instance.
     * @return JDA.
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Boots everything.
     */
    private void run() {
        try {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
            JSONArray messages = new JSONArray(new String(Files.readAllBytes(new File("messages.json").toPath())));
            for(int i = 0; i < messages.length(); i++) {
                JSON json = messages.json(i);
                Message message = json.marshal(Message.class);
                if(!message.isValid()) {
                    continue;
                }
                long until = message.getMillisUntil();
                if(until <= 0) {
                    continue;
                }
                executorService.schedule(message, until, TimeUnit.MILLISECONDS);
                System.out.println("Scheduled " + json);
            }
            jda = new JDABuilder(token)
                    .setGame(Game.playing("the waiting game"))
                    .build()
                    .awaitReady();
        } catch(IOException | InterruptedException | LoginException exception) {
            exception.printStackTrace();
        }
    }

}
