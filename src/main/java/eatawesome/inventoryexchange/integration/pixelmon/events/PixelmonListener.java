package eatawesome.inventoryexchange.integration.pixelmon.events;

import java.math.BigInteger;
import java.util.List;
import com.pixelmonmod.pixelmon.api.events.ApricornEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent.SuccessfulCapture;
import com.pixelmonmod.pixelmon.api.events.DropEvent;
import com.pixelmonmod.pixelmon.api.events.PickupEvent;
import com.pixelmonmod.pixelmon.api.events.PixelmonKnockoutEvent;
import com.pixelmonmod.pixelmon.api.events.PokeLootEvent;
import com.pixelmonmod.pixelmon.api.events.PokeStopEvent;
import com.pixelmonmod.pixelmon.api.events.ShopEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DroppedItem;

import eatawesome.inventoryexchange.api.capabilities.IKnowledgeProvider;
import eatawesome.inventoryexchange.api.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.config.InventoryExchangeServerConfig;
import eatawesome.inventoryexchange.utils.PlayerHelper;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public class PixelmonListener {

    public PixelmonListener() {
    }
    
    @SubscribeEvent
    public void onKnockout(PixelmonKnockoutEvent.Post event) {
        ServerPlayer serverPlayer = event.getSource().getOwnerPlayer();
        Pokemon pokemon = event.getPokemon();
        if (serverPlayer != null && pokemon != null) {
            int cp = calculateCP(pokemon);
            IKnowledgeProvider knowledge = serverPlayer.getCapability(InventoryExchangeCapabilities.KNOWLEDGE_CAPABILITY);
            BigInteger currentEmc = knowledge.getEmc();
            double multiplier = InventoryExchangeServerConfig.pixelmon_knockout_multiplier();
            BigInteger newEmc = currentEmc.add(BigInteger.valueOf(Math.round(cp * multiplier)));
            knowledge.setEmc(newEmc);
            knowledge.syncEmc(serverPlayer);
            Component message = InventoryExchangeLang.PIXELMON_KNOCKOUT.translate(Math.round(cp * multiplier), pokemon.getPokemonLevel(), cp, pokemon.getSpecies().getTranslatedName());
            serverPlayer.sendSystemMessage(message);
        }
    }
    
    @SubscribeEvent
    public void onCapture(SuccessfulCapture event) {
        ServerPlayer serverPlayer = event.getPlayer();
        Pokemon pokemon = event.getPokemon();
        if (serverPlayer != null && pokemon != null) {
            int cp = calculateCP(pokemon);
            IKnowledgeProvider knowledge = serverPlayer.getCapability(InventoryExchangeCapabilities.KNOWLEDGE_CAPABILITY);
            BigInteger currentEmc = knowledge.getEmc();
            double multiplier = InventoryExchangeServerConfig.pixelmon_capture_multiplier();
            BigInteger newEmc = currentEmc.add(BigInteger.valueOf(Math.round(cp * multiplier)));
            knowledge.setEmc(newEmc);
            knowledge.syncEmc(serverPlayer);
            Component message = InventoryExchangeLang.PIXELMON_CAPTURE.translate(Math.round(cp * multiplier), pokemon.getPokemonLevel(), cp, pokemon.getSpecies().getTranslatedName());
            serverPlayer.sendSystemMessage(message);
        }
    }
    
    @SubscribeEvent
    public void onApricornPick(ApricornEvent.Pick event) {
        ServerPlayer serverPlayer = event.getPlayer();
        ItemStack itemStack = event.getPickedStack();
        if (serverPlayer != null && itemStack != null) {
            PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
        }
    }
    
    @SubscribeEvent
    public void onDrop(DropEvent event) {
        ServerPlayer serverPlayer = event.player;
        for (DroppedItem droppedItem : event.getDrops()) {
            ItemStack itemStack = droppedItem.item;
            if (serverPlayer != null && itemStack != null) {
                PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
            }
        }
    }
    
    @SubscribeEvent
    public void onPickup(PickupEvent event) {
        ServerPlayer serverPlayer = event.player.player;
        ItemStack itemStack = event.stack;
        if (serverPlayer != null && itemStack != null) {
            PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
        }
    }
    
    @SubscribeEvent
    public void onPokeLoot(PokeLootEvent.GetDrops event) {
        ServerPlayer serverPlayer = event.player;
        ItemStack[] itemStacks = event.getDrops();
        for (ItemStack itemStack : itemStacks) {
            if (serverPlayer != null && itemStack != null) {
                PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
            }
        }
    }
    
    @SubscribeEvent
    public void onPokeStop(PokeStopEvent.Drops.Post event) {
        Player player = event.getPlayer();
        if (player instanceof ServerPlayer serverPlayer) {
            List<ItemStack> itemStacks = event.getDrops();
            for (ItemStack itemStack : itemStacks) {
                if (serverPlayer != null && itemStack != null) {
                    PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onShopPurchase(ShopEvent.Purchase event) {
        ServerPlayer serverPlayer = event.getPlayer();
        ItemStack itemStack = event.getItem();
        if (serverPlayer != null && itemStack != null) {
            PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
        }
    }
    
    private int calculateCP(Pokemon pokemon) {
        double hp = pokemon.getStats().getHP();
        double attack = pokemon.getStats().getAttack();
        double defense = pokemon.getStats().getDefense();
        double specialAttack = pokemon.getStats().getSpecialAttack();
        double specialDefense = pokemon.getStats().getSpecialDefense();
        double speed = pokemon.getStats().getSpeed();
        double level = pokemon.getPokemonLevel();

        double cp = ((hp + attack + defense + specialAttack + specialDefense + speed) * level * 6) / 100;

        return (int) Math.floor(cp);
    }
}