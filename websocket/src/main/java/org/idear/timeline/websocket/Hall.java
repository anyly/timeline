package org.idear.timeline.websocket;

import org.idear.timeline.Projector;

import java.util.LinkedHashMap;

/**
 * 导演,编排故事
 */
public abstract class Hall {
    private Projector projector = new Projector();
    private LinkedHashMap<Integer, Room> allRooms = new LinkedHashMap<>();

    private int currentNo = 1000;

    public Room room(int no) {
        return allRooms.get(no);
    }

    public synchronized int createRoom(Room room) {
        currentNo++;
        room.setNo(currentNo);
        room.projector = projector;
        allRooms.put(currentNo, room);
        return currentNo;
    }

    public synchronized void deleteRoom(int no) {
        allRooms.remove(no);
    }
}
