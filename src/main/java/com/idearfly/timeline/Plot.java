package com.idearfly.timeline;

/**
 * 故事情节
 */
public abstract class Plot implements Stage {
    private String name;

    public Plot(String name) {
        this.name = name;
    }

    /**
     * 故事内容
     */
    public abstract void doing();

    public String getName() {
        return name;
    }
}
