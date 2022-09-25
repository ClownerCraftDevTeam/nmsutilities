package me.nikl.nmsutilities;

import com.google.gson.stream.JsonReader;
import net.minecraft.server.v1_10_R1.ChatMessage;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.ItemStack;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import net.minecraft.server.v1_10_R1.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Created by niklas on 10/17/16.
 *
 * nms utility for 1.10.R1
 */
public class NmsUtility_1_10_R1 implements NmsUtility {
    @Override
    public void updateInventoryTitle(Player player, String newTitle) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, "minecraft:chest", new ChatMessage(ChatColor.translateAlternateColorCodes('&', newTitle)), player.getOpenInventory().getTopInventory().getSize());
        ep.playerConnection.sendPacket(packet);
        ep.updateInventory(ep.activeContainer);
    }

    @Override
    public void sendJSON(Player player, String json) {
        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(json);
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, (byte) 0);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void sendJSON(Player player, Collection<String> json) {
        for (String message : json) {
            IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(message);
            PacketPlayOutChat packet = new PacketPlayOutChat(comp, (byte) 0);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void sendJSON(Collection<Player> players, String json) {
        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(json);
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, (byte) 0);
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void sendJSON(Collection<Player> players, Collection<String> json) {
        for (String message : json) {
            IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(message);
            PacketPlayOutChat packet = new PacketPlayOutChat(comp, (byte) 0);
            for (Player player : players) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int durationTicks) {
        if (title != null) {
            IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title + "\"}"));
            PacketPlayOutTitle pTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(pTitle);
        }
        if (subTitle != null) {
            IChatBaseComponent chatSubTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subTitle + "\"}"));
            PacketPlayOutTitle pSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubTitle);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(pSubTitle);
        }
        PacketPlayOutTitle length = new PacketPlayOutTitle(10, durationTicks, 10);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    @Override
    public void sendActionbar(Player player, String message) {
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message + "\"}"));
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(bar);
    }

    @Override
    public void sendList(Player player, String header, String footer) {
        if (header !=  null) sendListHeader(player, header);
        if (footer !=  null) sendListFooter(player, footer);
    }

    @Override
    public void sendListFooter(Player player, String footer) {
        IChatBaseComponent bottom = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, bottom);
            footerField.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void sendListHeader(Player player, String header) {
        JsonReader reader = new JsonReader(new StringReader("{'text': '" + header + "'}"));
        IChatBaseComponent bottom = IChatBaseComponent.ChatSerializer.a((reader.toString()));
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field footerField = packet.getClass().getDeclaredField("a");
            footerField.setAccessible(true);
            footerField.set(packet, bottom);
            footerField.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public org.bukkit.inventory.ItemStack removeGlow(org.bukkit.inventory.ItemStack item) {
        ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (nmsStack.hasTag()) {
            tag = nmsStack.getTag();
            tag.remove("ench");
            nmsStack.setTag(tag);
            return CraftItemStack.asCraftMirror(nmsStack);
        }
        return item;
    }

    @Override
    public org.bukkit.inventory.ItemStack addGlow(org.bukkit.inventory.ItemStack item) {
        ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
}
