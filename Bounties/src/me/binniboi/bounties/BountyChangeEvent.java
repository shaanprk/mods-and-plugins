package me.binniboi.bounties;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BountyChangeEvent extends Event implements Cancellable {

    private HandlerList list = new HandlerList();

    Player p;
    double change;
    boolean cancelled;

    public BountyChangeEvent(Player p, double change) {
        this.p = p;
        this.change = change;
    }

    public Player getPlayer() {
        return p;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double newChange) {
        this.change = newChange;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean flag) {
        cancelled = flag;
    }

    @Override
    public HandlerList getHandlers() {
        return list;
    }
}