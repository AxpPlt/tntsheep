package com.tntsheep;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Plugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.DIAMOND && event.getAction().name().contains("RIGHT")) {
            Sheep sheep = (Sheep) player.getWorld().spawnEntity(player.getLocation(), EntityType.SHEEP);
            sheep.setVelocity(player.getLocation().getDirection().multiply(1.5)); // Запускаем овцу вперед

            final Sheep finalSheep = sheep; // Объявляем овцу как final

            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (!finalSheep.isDead()) {
                        if (ticks < 100) { // Если прошло меньше 100 тиков (2 секунды), то крутим овцу
                            Vector direction = player.getLocation().toVector().subtract(finalSheep.getLocation().toVector()).normalize();
                            finalSheep.setVelocity(direction.multiply(0.1)); // Задаем скорость вращения
                            ticks++;
                        } else { // Иначе, останавливаем овцу и создаем взрыв
                            finalSheep.setVelocity(new Vector(0, 0, 0)); // Останавливаем овцу
                            finalSheep.getWorld().createExplosion(finalSheep.getLocation(), 4.0f); // Создаем взрыв в месте овцы
                            finalSheep.remove(); // Удаляем овцу после взрыва
                            cancel(); // Останавливаем задачу
                        }
                    } else {
                        cancel(); // Если овца мертва, останавливаем задачу
                    }
                }
            }.runTaskTimer(this, 0, 1); // Запускаем задачу каждый тик
        }
    }
}