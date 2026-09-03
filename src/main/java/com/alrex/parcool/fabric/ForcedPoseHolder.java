package com.alrex.parcool.fabric;

import net.minecraft.world.entity.Pose;

import javax.annotation.Nullable;

/**
 * Замена Entity#setForcedPose/getForcedPose из NeoForge. Лежит вне пакета миксинов намеренно:
 * утиный интерфейс внутри mixin-пакета даёт IllegalClassLoadError.
 */
public interface ForcedPoseHolder {
    @Nullable
    Pose parCool$getForcedPose();

    void parCool$setForcedPose(@Nullable Pose pose);
}
