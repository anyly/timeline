package org.idear.timeline;

public interface Timeline {
    public Timeline name(String name);

    public Timeline meanwhile(Dispatcher dispatcher);

    public Timeline then(Plot plot);

    public Story construct();
}
