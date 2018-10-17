package org.idear.timeline;

import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * 分发器，用于并发
 */
public class Dispatcher {
    private String name;
    LinkedHashSet<Plot> multiLine = new LinkedHashSet<>();
    private LinkedHashSet<Event> toBeContinued = new LinkedHashSet<>();
    LinkedHashSet<String> ploatName = null;

    public Dispatcher(String name) {
        this.name = name;
    }

    public Dispatcher line(Plot plot) {
        multiLine.add(plot);
        return this;
    }

    public Dispatcher line(String plot) {
        if (ploatName == null) {
            ploatName = new LinkedHashSet<>();
        }
        ploatName.add(plot);
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
    }

    public boolean isCompleted() {
        LinkedHashSet<Event> newList = new LinkedHashSet<>();
        for (Event event: toBeContinued) {
            if (!event.ending()) {
                newList.add(event);
            }
        }
        toBeContinued = newList;
        if (toBeContinued.size() > 0) {
            return false;
        }
        return true;
    }
}
