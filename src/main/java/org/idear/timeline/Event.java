package org.idear.timeline;

/**
 * 故事事件
 */
public abstract class Event extends Plot {

    public Event(String name) {
        super(name);
    }

    /**
     * 事件发生条件
     * @return
     */
    public abstract boolean when();

    /**
     * 故事完结条件
     * @return
     */
    public abstract boolean ending();
}
