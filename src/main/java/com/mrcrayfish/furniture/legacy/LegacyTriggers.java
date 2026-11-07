package com.mrcrayfish.furniture.legacy;

import com.google.gson.JsonObject;
import com.mrcrayfish.furniture.Reference;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Custom advancement triggers.
 */
public class LegacyTriggers
{
    public static final Basic PLACE_CHAIR = new Basic("get_chair");
    public static final Basic PLACE_TABLE = new Basic("get_table");
    public static final Basic PLACE_CHAIR_OR_TABLE = new Basic("get_chair_or_table");
    public static final Basic PLACE_BLINDS_OR_CURTAINS = new Basic("get_blinds_or_curtains");
    public static final Basic PLACE_OUTDOOR_FURNITURE = new Basic("get_outdoor_furniture");
    public static final Basic PLACE_BATHROOM_FURNITURE = new Basic("get_bathroom_furniture");
    public static final Basic PLACE_APPLIANCE = new Basic("place_appliance");
    public static final Basic CREATE_COUCH_JEB = new Basic("create_couch_jeb");
    public static final Basic UNWRAP_PRESENT = new Basic("unwrap_present");
    public static final Basic OVEN_COOK = new Basic("oven_cook");
    public static final Basic FREEZER_FREEZE = new Basic("freezer_freeze");
    public static final Basic PRINTER_COPY = new Basic("printer_copy");
    public static final Basic STEREO_ACTIVATED = new Basic("stereo_activated");
    public static final Basic CLICKED_DOORBELL = new Basic("clicked_doorbell");
    public static final Basic PLAYER_ZAPPED = new Basic("player_zapped");
    public static final Basic PLAYER_SENT_MAIL = new Basic("player_sent_mail");
    public static final Basic PLAYER_GOT_MAIL = new Basic("player_got_mail");
    public static final Basic PLAYER_FART = new Basic("player_fart");
    public static final Basic PLAYER_SHOWER = new Basic("player_shower");

    private static final Basic[] ALL = {PLACE_CHAIR, PLACE_TABLE, PLACE_CHAIR_OR_TABLE, PLACE_BLINDS_OR_CURTAINS, PLACE_OUTDOOR_FURNITURE, PLACE_BATHROOM_FURNITURE, PLACE_APPLIANCE, CREATE_COUCH_JEB, UNWRAP_PRESENT, OVEN_COOK, FREEZER_FREEZE, PRINTER_COPY, STEREO_ACTIVATED, CLICKED_DOORBELL, PLAYER_ZAPPED, PLAYER_SENT_MAIL, PLAYER_GOT_MAIL, PLAYER_FART, PLAYER_SHOWER};

    public static void register()
    {
        for(Basic trigger : ALL)
        {
            CriteriaTriggers.register(trigger);
        }
    }

    public static class Basic extends SimpleCriterionTrigger<Basic.Instance>
    {
        private final ResourceLocation id;

        public Basic(String name)
        {
            this.id = new ResourceLocation(Reference.MOD_ID, name);
        }

        @Override
        public ResourceLocation getId()
        {
            return this.id;
        }

        @Override
        protected Instance createInstance(JsonObject object, ContextAwarePredicate predicate, DeserializationContext context)
        {
            return new Instance(this.id, predicate);
        }

        /** Safe to call from either side: does nothing unless the player is a server player. */
        public void trigger(Player player)
        {
            if(player instanceof ServerPlayer serverPlayer)
            {
                this.trigger(serverPlayer, instance -> true);
            }
        }

        public static class Instance extends AbstractCriterionTriggerInstance
        {
            public Instance(ResourceLocation id, ContextAwarePredicate predicate)
            {
                super(id, predicate);
            }
        }
    }
}
