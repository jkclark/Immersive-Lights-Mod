package immersivelightsmod;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
    private static Date lastRequest;
    private static final int minTimeBtwnRequests = 10000;  // In milliseconds

    @SubscribeEvent
    public void onBiomeChanged(TickEvent.PlayerTickEvent event) {
        World world = event.player.world;
        Biome biome = world.getBiome(event.player.getPosition());
        Biome.Category currBiomeCategory = biome.getCategory();

        if (prevBiomeCategory == null) {
            // First tick of the game, change the lights for the current biome
            changeLights(currBiomeCategory);
            Calendar cal = Calendar.getInstance();
            lastRequest = cal.getTime();

            // Update biome
            prevBiomeCategory = currBiomeCategory;
        }
        else {
            // Don't send a request if we've sent one in the last 30 seconds
            Calendar cal = Calendar.getInstance();
            Date rightNow = cal.getTime();
            long diff = rightNow.getTime() - lastRequest.getTime();
            if (diff > minTimeBtwnRequests) {

                // Don't send a request if the current biome's category isn't different than the previous one
                if (prevBiomeCategory != currBiomeCategory) {
                    changeLights(currBiomeCategory);
                    lastRequest = cal.getTime();

                    // By only updating the biome if it has been minTimeBtwnRequests milliseconds (and if it's different
                    // than the last one) we guarantee the light changing to the appropriate color for the current biome.
                    // (If we did it every tick, it wouldn't necessarily be considered different after the 30 second
                    // pause)
                    prevBiomeCategory = currBiomeCategory;
                }
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

    private void changeLights(Biome.Category newBiomeCategory) {
        final String iftttKEY = "YOUR IFTTT KEY GOES HERE";
        String url = "https://maker.ifttt.com/trigger/minecraft_biome_changed/with/key/" + iftttKEY + "?value1=" + newBiomeCategory.toString();
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
