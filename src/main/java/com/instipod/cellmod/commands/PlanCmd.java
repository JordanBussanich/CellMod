package com.instipod.cellmod.commands;

import com.instipod.cellmod.CellMod;
import com.instipod.cellmod.TLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlanCmd implements CommandExecutor {
    private CellMod plugin;
    
    public PlanCmd(CellMod p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        Player p = null;
        try {
            p = (Player) cs;
        } catch (Exception ex) {
            cs.sendMessage("Only players may use this command.");
            return true;
        }
        if (plugin.hasPermission(p, "cellmod.use")) {
            if (plugin.getPlayerCarrier(p) != null) {
                if (strings.length < 1) {
                    p.sendMessage(plugin.lang.getProperty("CarrierIs") + ChatColor.GREEN + plugin.getPlayerCarrier(p).getName());
                    ResultSet rs = plugin.getResult("SELECT * FROM players WHERE Player='" + p.getName() + "';");
                    String number = null;
                    String count = null;
                     try {
                         while (rs.next()) {
                            number = rs.getString("Number");
                            count = rs.getString("Plan");
                     }
                    } catch (Exception ex) {
                        TLogger.log(Level.SEVERE, "Failed to read player number!");
                    }
                    p.sendMessage(plugin.lang.getProperty("NumberIs") + ChatColor.GREEN + number);
                    p.sendMessage(plugin.lang.getProperty("PlanCount1") + ChatColor.RED + count + ChatColor.WHITE + plugin.lang.getProperty("PlanCount2"));
                    p.sendMessage(plugin.lang.getProperty("BuyMessages"));
                } else {
                    if (strings.length > 1) {
                        if (strings[0].equals("buy")) {
                            Integer amount = 0;
                            try {
                                amount = Integer.parseInt(strings[1]);
                            } catch (Exception ex) {
                                p.sendMessage(ChatColor.RED + "Message amount must be a number!");
                            }
                            Double cost = amount * plugin.getPlayerCarrier(p).getCost();
                            if (plugin.economy.hasMoney(p, cost)) {
                                plugin.economy.takeMoney(p, cost);
                                Integer newcount = amount + plugin.getPlayerCarrier(p).getPlayerRemainingMessages(p);
                                plugin.runUpdateQuery("UPDATE players SET Plan='" + newcount.toString() + "' WHERE Player='" + p.getName() + "';");
                                p.sendMessage(ChatColor.GREEN  + plugin.lang.getProperty("GoodPurchase"));
                            } else {
                                cs.sendMessage(ChatColor.RED + plugin.lang.getProperty("MoreMoney"));
                            }
                        }
                    }
                }
            } else {
                cs.sendMessage(ChatColor.RED + plugin.lang.getProperty("JoinNetwork"));
            }
        } else {
            cs.sendMessage(ChatColor.RED + plugin.lang.getProperty("NoPermission"));
        }
        return true;
    }
    
}