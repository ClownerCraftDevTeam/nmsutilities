package me.nikl.nmsutilities;

import io.netty.handler.codec.DecoderException;
import net.minecraft.server.v1_8_R1.ChatMessage;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.ItemStack;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R1.NBTTagList;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;
import net.minecraft.server.v1_8_R1.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Created by niklas on 10/17/16.
 *
 * nms utility for 1.8.R1
 */
public class NmsUtility_1_8_R1 implements NmsUtility {
    private boolean checkInventoryTitleLength = false;

    public NmsUtility_1_8_R1() {
        try {
            Inventory inventory = Bukkit.createInventory(null, 27, "This title is longer then 32 characters!");
        } catch (Exception e) {
            checkInventoryTitleLength = true;
        }
    }

    @Override
    public void updateInventoryTitle(Player player, String newTitle) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        newTitle = ChatColor.translateAlternateColorCodes('&', newTitle);
        if (checkInventoryTitleLength && newTitle.length() > 32) {
            newTitle = newTitle.substring(0, 28) + "...";
        }
        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId
                , "minecraft:chest", new ChatMessage(newTitle)
                , player.getOpenInventory().getTopInventory().getSize());
        try {
            ep.playerConnection.sendPacket(packet);
            ep.updateInventory(ep.activeContainer);
        } catch (DecoderException ex) {
            if (!checkInventoryTitleLength) {
                checkInventoryTitleLength = true;
                updateInventoryTitle(player, newTitle);
            } else {
                Bukkit.getConsoleSender().sendMessage("DecoderException while trying to send new title < 32 chars O.o");
            }
        }
    }

    @Override
    public void sendJSON(Player player, String json) {
        IChatBaseComponent comp = ChatSerializer.a(json);
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, (byte) 0);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void sendJSON(Player player, Collection<String> json) {
        for (String message : json) {
            IChatBaseComponent comp = ChatSerializer.a(message);
            PacketPlayOutChat packet = new PacketPlayOutChat(comp, (byte) 0);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void sendJSON(Collection<Player> players, String json) {
        IChatBaseComponent comp = ChatSerializer.a(json);
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, (byte) 0);
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void sendJSON(Collection<Player> players, Collection<String> json) {
        for (String message : json) {
            IChatBaseComponent comp = ChatSerializer.a(message);
            PacketPlayOutChat packet = new PacketPlayOutChat(comp, (byte) 0);
            for (Player player : players) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle) {
        if (title != null) {
            IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + title + "\",color:" + ChatColor.GOLD.name().toLowerCase() + "}");
            PacketPlayOutTitle pTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(pTitle);
        }
        if (subTitle != null) {
            IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + subTitle + "\",color:" + ChatColor.GOLD.name().toLowerCase() + "}");
            PacketPlayOutTitle pSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(pSubTitle);
        }
        PacketPlayOutTitle length = new PacketPlayOutTitle(5, 20, 5);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    @Override
    public void sendActionbar(Player player, String message) {
        IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(bar);
    }


    @Override
    public void sendListFooter(Player player, String footer) {
        IChatBaseComponent bottom = ChatSerializer.a("{text: '" + ChatColor.translateAlternateColorCodes('&', footer) + "'}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, bottom);
            footerField.setAccessible(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void sendListHeader(Player player, String header) {
        IChatBaseComponent bottom = ChatSerializer.a("{text: '" + ChatColor.translateAlternateColorCodes('&', header) + "'}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field footerField = packet.getClass().getDeclaredField("a");
            footerField.setAccessible(true);
            footerField.set(packet, bottom);
            footerField.setAccessible(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
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
