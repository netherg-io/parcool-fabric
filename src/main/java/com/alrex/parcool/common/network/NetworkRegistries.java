package com.alrex.parcool.common.network;

import com.alrex.parcool.common.network.payload.*;
import com.alrex.parcool.fabric.IPayloadContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;

public class NetworkRegistries {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(StartBreakfallEventPayload.TYPE, StartBreakfallEventPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StartBreakfallEventPayload.TYPE, StartBreakfallEventPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ActionStatePayload.TYPE, ActionStatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ActionStatePayload.TYPE, ActionStatePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(LimitationPayload.TYPE, LimitationPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LimitationPayload.TYPE, LimitationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ClientInformationPayload.TYPE, ClientInformationPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClientInformationPayload.TYPE, ClientInformationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StaminaPayload.TYPE, StaminaPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StaminaPayload.TYPE, StaminaPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StaminaProcessOnServerPayload.TYPE, StaminaProcessOnServerPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StaminaProcessOnServerPayload.TYPE, StaminaProcessOnServerPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StaminaBroadcastPayload.TYPE, StaminaBroadcastPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ActionStateBroadcastPayload.TYPE, ActionStateBroadcastPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(StartBreakfallEventPayload.TYPE, server(StartBreakfallEventPayload::handleServer));
        ServerPlayNetworking.registerGlobalReceiver(ActionStatePayload.TYPE, server(ActionStatePayload::handleServer));
        ServerPlayNetworking.registerGlobalReceiver(LimitationPayload.TYPE, server(LimitationPayload::handleServer));
        ServerPlayNetworking.registerGlobalReceiver(ClientInformationPayload.TYPE, server(ClientInformationPayload::handleServer));
        ServerPlayNetworking.registerGlobalReceiver(StaminaPayload.TYPE, server(StaminaPayload::handleServer));
        ServerPlayNetworking.registerGlobalReceiver(StaminaProcessOnServerPayload.TYPE, server(StaminaProcessOnServerPayload::handleServer));
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(StartBreakfallEventPayload.TYPE, client(StartBreakfallEventPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(ActionStatePayload.TYPE, client(ActionStatePayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(LimitationPayload.TYPE, client(LimitationPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(ClientInformationPayload.TYPE, client(ClientInformationPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(StaminaPayload.TYPE, client(StaminaPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(StaminaProcessOnServerPayload.TYPE, client(StaminaProcessOnServerPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(StaminaBroadcastPayload.TYPE, client(StaminaBroadcastPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(ActionStateBroadcastPayload.TYPE, client(ActionStateBroadcastPayload::handleClient));
    }

    private interface Handler<T> {
        void handle(T payload, IPayloadContext context);
    }

    // Обработчики Fabric API уже выполняются в игровом потоке, поэтому enqueueWork -- просто вызов.
    private static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload>
    ServerPlayNetworking.PlayPayloadHandler<T> server(Handler<T> handler) {
        return (payload, context) -> handler.handle(payload, wrap(context.player()));
    }

    @Environment(EnvType.CLIENT)
    private static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload>
    ClientPlayNetworking.PlayPayloadHandler<T> client(Handler<T> handler) {
        return (payload, context) -> handler.handle(payload, wrap(context.player()));
    }

    private static IPayloadContext wrap(Player player) {
        return new IPayloadContext() {
            @Override
            public Player player() {
                return player;
            }

            @Override
            public void enqueueWork(Runnable work) {
                work.run();
            }
        };
    }
}
