package survival.labs.bot;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class AutoResponseHandler {

    private JSONObject autoResponses;
    private boolean cooldown = false;

    public AutoResponseHandler() {
        autoResponses = readFromConfig();
    }

    public void handleResponse(GuildMessageReceivedEvent event, int delay) {
        if (cooldown) return;

        String message = event.getMessage().getContentRaw().toLowerCase().trim();
        JSONObject equalMessage = (JSONObject) autoResponses.get("equal");
        if (equalMessage.containsKey(message)) {
            event.getChannel().sendMessage(equalMessage.get(message).toString()).queue();
            addCooldown(delay);
            return;
        }

        JSONObject includeMessage = (JSONObject) autoResponses.get("include");
        Object[] keys = includeMessage.keySet().toArray();
        Arrays.stream(keys).parallel().forEach(key -> {
            if (message.contains(key.toString())) {
                event.getChannel().sendMessage(includeMessage.get(key.toString()).toString()).queue();
                addCooldown(delay);
            }
        });
    }

    private void writeToConfig(JSONObject autoResponses) {
        try {
            FileWriter file = new FileWriter("plugins/survivalBOT/autoresponses.json");
            file.write(autoResponses.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject readFromConfig() {
        JSONParser parser = new JSONParser();
        JSONObject autoResponses = null;
        File file = new File("plugins/survivalBOT/autoresponses.json");

        try {
            if (!file.exists()) {
                file.createNewFile();
                autoResponses = (JSONObject) parser.parse("{ \"equal\": {}, \"include\": {} }");
                writeToConfig(autoResponses);
            } else {
                autoResponses = (JSONObject) parser.parse(new FileReader(file));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return autoResponses;
    }

    private void addCooldown(int delay) {
        cooldown = true;
        setTimeout(() -> cooldown = false, delay);
    }

    private void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public JSONObject getAutoResponses() {
        return autoResponses;
    }

    public void setAutoResponses(JSONObject autoResponses) {
        this.autoResponses = autoResponses;
        writeToConfig(autoResponses);
    }
}
