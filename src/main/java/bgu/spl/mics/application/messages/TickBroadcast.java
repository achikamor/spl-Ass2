package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    public int currentTick;
    public int duration;

    public TickBroadcast(int currentTick,int duration){
        this.currentTick=currentTick;
        this.duration=duration;
    }
}
