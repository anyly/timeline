package com.idearfly.timeline;

public interface Timeline {

    public Timeline meanwhile(Dispatcher dispatcher);

    public Timeline then(Plot plot);

    public Timeline then(String plot);

    public Story construct();

    public Dispatcher dispatcher(String name);
}
