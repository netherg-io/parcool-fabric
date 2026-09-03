package com.alrex.parcool.common.registries;

import com.alrex.parcool.client.hud.HUDManager;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.handlers.*;
import com.alrex.parcool.common.network.ActionSynchronizationBroadcaster;
import com.alrex.parcool.common.network.StaminaSynchronizationBroadcaster;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.InputEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.ViewportEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityJoinLevelEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingAttackEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingFallEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.tick.PlayerTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * Замена EventBusForgeRegistry: у NeoForge всё висело на одной шине, под Fabric каждое событие
 * приезжает из своего источника -- Fabric API, Porting Lib или собственного миксина ParCool.
 */
public class EventRegistry {
    private static final ActionProcessor ACTION_PROCESSOR = new ActionProcessor();

    public static ActionProcessor getActionProcessor() {
        return ACTION_PROCESSOR;
    }

    public static void register() {
        LivingFallEvent.EVENT.register(PlayerDamageHandler::onFall);
        LivingAttackEvent.EVENT.register(PlayerDamageHandler::onAttack);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> LoginLogoutHandler.onLogoutInServer(handler.getPlayer()));
        ServerPlayerEvents.COPY_FROM.register(PlayerCloneHandler::onClone);
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ActionSynchronizationBroadcaster.onTick();
            StaminaSynchronizationBroadcaster.onTick();
        });
        PlayerTickEvent.Post.EVENT.register(event -> ACTION_PROCESSOR.onTick(event));
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> OpenSettingsParCoolHandler.onTick());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            EnableOrDisableParCoolHandler.onTick();
            HUDManager.getInstance().onTick();
        });
        EntityJoinLevelEvent.EVENT.register(PlayerJoinHandler::onPlayerJoin);
        InputEvent.InteractionKeyMappingTriggered.EVENT.register(InputHandler::onInput);
        ViewportEvent.ComputeCameraAngles.EVENT.register(ACTION_PROCESSOR::onViewRender);
    }
}
