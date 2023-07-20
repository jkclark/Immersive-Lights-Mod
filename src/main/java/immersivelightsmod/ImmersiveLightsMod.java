package immersivelightsmod;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class LightModEventHandler {
    private static Biome.Category prevBiomeCategory;
    private static int prevHour;
    private static Date lastRequest;
    private static final int minTimeBtwnRequests = 10000;  // In milliseconds

    @SubscribeEvent
    public void onBiomeChanged(TickEvent.PlayerTickEvent event) {
        World world = event.player.world;
        Biome biome = world.getBiome(event.player.getPosition());
        Biome.Category currBiomeCategory = biome.getCategory();
        int gameHour = (int) world.getDayTime() / 1000;

        // This only applies to the first tick of the session
        if (prevBiomeCategory == null) {
            // Set the lights for the first time
            changeLights(currBiomeCategory, gameHour);

            // Mark the time of this request
            Calendar cal = Calendar.getInstance();
            lastRequest = cal.getTime();

            // Update the biome
            prevBiomeCategory = currBiomeCategory;

            // Update the in-game hour
            prevHour = gameHour;
        }

        // Every tick of the session after the first one
        else {
            // Only send a request if it's been at least minTimeBtwnRequests milliseconds since the last one
            Calendar cal = Calendar.getInstance();
            Date rightNow = cal.getTime();
            long diff = rightNow.getTime() - lastRequest.getTime();
            boolean minTimePassed = diff > minTimeBtwnRequests;

            if (minTimePassed) {
                // Now that we've waited long enough, only send a request if:
                //  - The biome category has changed since the last time we sent a request
                //  OR
                //  - The in-game hour has changed

                // Has the biome category changed?
                boolean biomeHasChanged = prevBiomeCategory != currBiomeCategory;

                // Has the in-game hour changed?
                boolean inGameHourChanged = prevHour != gameHour;

                if (biomeHasChanged || inGameHourChanged) {
                    // Set the lights
                    changeLights(currBiomeCategory, gameHour);

                    // Mark the time of this request
                    lastRequest = cal.getTime();

                    // Update the biome
                    // By only updating the biome if it has been minTimeBtwnRequests milliseconds (and if it's different
                    // than the last one) we guarantee the light changing to the appropriate color for the current biome.
                    // (If we did it every tick, it wouldn't necessarily be considered different after the 30 second
                    // pause)
                    prevBiomeCategory = currBiomeCategory;
                }

                // Update the in-game hour
                prevHour = gameHour;
            }
        }
    }

    public static class Request implements Callable<Response> {
        private URL url;

        private Request(URL url) {
            this.url = url;
        }

        @Override
        public Response call() throws Exception {
            return new Response(url.openStream());
        }
    }

    public static class Response {
        private InputStream body;

        private Response(InputStream body) {
            this.body = body;
        }

        public InputStream getBody() {
            return body;
        }
    }

    private void changeLights(Biome.Category newBiomeCategory, int hour) {
        // TODO: Somehow get this from a secret somewhere...
        final String iftttKEY = "...";
        String url = "https://maker.ifttt.com/trigger/minecraft_biome_changed/with/key/" + iftttKEY +
                      "?value1=" + newBiomeCategory.toString() + "&value2=" + Integer.toString(hour);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        try {
            Future<Response> response = executor.submit(new Request(new URL(url)));
            executor.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


@Mod("immersivelightsmod")
public class ImmersiveLightsMod
{
    public ImmersiveLightsMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new LightModEventHandler());
    }
}
