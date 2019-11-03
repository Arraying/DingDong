package de.arraying.dingdong;

import de.arraying.kotys.JSONField;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public final class Message implements Runnable {

    @JSONField(key = "time") private String timestamp;
    @JSONField(key = "message") private String content;
    @JSONField(key = "channel") private Long channel;

    /**
     * Runs the task.
     */
    @Override
    public void run() {
        MessageChannel channel = Bot.INSTANCE.getJda().getTextChannelById(this.channel);
        if(channel == null) {
            throw new RuntimeException("channel " + this.channel + " does not exist");
        }
        String message = content.length() > 2000 ? content.substring(0, 2000) : content;
        if(message.startsWith("file ")) {
            String fileName = message.substring(5);
            File file = new File(fileName);
            if(!file.exists()) {
                System.out.println("Could not run task as file " + fileName + " does not exist");
            } else {
                channel.sendFile(file).queue(success -> System.out.println("Message sent for " + toString()), Throwable::printStackTrace);
            }
        } else {
            channel.sendMessage(message).queue();
        }
        System.out.println("Ran task " + toString());
    }

    /**
     * Converts the message to string.
     * @return The string representation.
     */
    @Override
    public String toString() {
        return timestamp + " w/ " + content;
    }

    /**
     * Gets the number of milliseconds until the event.
     * @return The number of milliseconds.
     */
    long getMillisUntil() {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
            return date.getTime() - System.currentTimeMillis();
        } catch(ParseException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    /**
     * Whether or not the message is valid.
     * @return True if it is, false otherwise.
     */
    boolean isValid() {
        return timestamp != null && !timestamp.isEmpty()
                && content != null && !content.isEmpty()
                && channel != null && channel > 0;
    }

}
