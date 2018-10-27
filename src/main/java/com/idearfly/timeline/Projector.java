package com.idearfly.timeline;

import java.util.*;

/**
 * 放映室，用于运行故事
 */
public class Projector {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            for (;;) {
                synchronized (thread) {
                    if (films.size() == 0) {
                        try {
                            thread.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                int i=0;
                while (i < films.size()) {
                    Film film = films.get(i);
                    try {
                        if (film == null) {
                            films.remove(i);
                            continue;
                        } else {
                            if (film.play()) {
                                films.remove(i);
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }
        }
    });

    ArrayList<Film> films = new ArrayList<>();

    public Projector add(Film film){
        synchronized (thread) {
            films.add(film);
            thread.notify();
        }
        return this;
    }

    public Projector(String name) {
        thread.setName(name + " " + Projector.class.getSimpleName());
        thread.start();
    }
}
