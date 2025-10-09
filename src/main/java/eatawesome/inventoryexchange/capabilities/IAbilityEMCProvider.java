package eatawesome.inventoryexchange.capabilities;

import net.minecraft.server.level.ServerPlayer;

import java.math.BigInteger;

import org.jetbrains.annotations.NotNull;

public interface IAbilityEMCProvider {

	BigInteger getEmc();
    void setEmc(BigInteger emc);
    void sync(@NotNull ServerPlayer player);
}