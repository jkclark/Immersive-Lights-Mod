Smartlife.lightColor.setLightBrightness("100");

var biome = MakerWebhooks.event.Value1;

switch (biome) {
  case "PLAINS":
    Smartlife.lightColor.setLightColor("#7ecc0a");
    break;
  case "FOREST":
    Smartlife.lightColor.setLightColor("#18b300");
    break;
  case "DESERT":
    Smartlife.lightColor.setLightColor("#ffff00");
    break;
  case "RIVER":
    Smartlife.lightColor.setLightColor("#0073ff");
    break;
  case "OCEAN":
    Smartlife.lightColor.setLightColor("#003fd4");
    break;
  case "TAIGA":
    Smartlife.lightColor.setLightColor("#009e7e");
    break;
  case "EXTREME_HILLS":
    Smartlife.lightColor.setLightColor("#49cc9c");
    break;
  case "SWAMP":
    Smartlife.lightColor.setLightColor("#338a54");
    break;
  case "BEACH":
    Smartlife.lightColor.setLightColor("#82add1");
    break;
  case "SAVANNA":
    Smartlife.lightColor.setLightColor("#d1e05c");
    break;
  case "JUNGLE":
    Smartlife.lightColor.setLightColor("#00700f");
    break;
  case "NETHER":
    Smartlife.lightColor.setLightColor("#f22400");
    break;
  case "ICY":
    Smartlife.lightColor.setLightColor("#8ffffb");
    break;
  case "THEEND":
    Smartlife.lightColor.setLightColor("#790094");
    break;
  case "MESA":
    Smartlife.lightColor.setLightColor("#d98600");
    break;
  case "MUSHROOM":
    Smartlife.lightColor.setLightColor("#817185");
    break;
  default:
    Smartlife.lightColor.setLightColor("#ff00e6");
    break;
}