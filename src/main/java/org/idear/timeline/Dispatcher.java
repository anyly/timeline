package org.idear.timeline;

import java.util.LinkedList;

/**
 * 分发器，用于并发
 */
public class Dispatcher {
    private String name;
    private LinkedList<Plot> multiLine = new LinkedList<>();
    private LinkedList<Event> toBeContinued = new LinkedList<>();

    public Dispatcher(String name) {
        this.name = name;
    }

    public Dispatcher line(Plot plot) {
        multiLine.add(plot);
        return this;
    }

    public void dispatch() {
        for (Plot plot: multiLine) {
            if (plot instanceof Event) {
                Event event = (Event)plot;
                if (event.when()) {
                    event.doing();
                    if (!event.ending()) {
                        toBeContinued.add(event);
                    }
                }
            } else {
                plot.doing();
            }
        }
        isCompleted();
    }

    public void isCompleted() {
        LinkedList<Event> newList = new LinkedList<>();
        for (Event event: toBeContinued) {
            if (!event.ending()) {
                newList.add(event);
            }
        }
        toBeContinued = newList;
        if (toBeContinued.size() > 0) {
            throw new NotCompletedException();
        }
    }
}
