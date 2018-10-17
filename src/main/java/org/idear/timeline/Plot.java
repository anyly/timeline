package org.idear.timeline;

/**
 * 故事情节
 */
public abstract class Plot {
    private String name;

    public Plot(String name) {
        this.name = name;
    }

    /**
     * 故事内容
     */
    public abstract void doing();

}
