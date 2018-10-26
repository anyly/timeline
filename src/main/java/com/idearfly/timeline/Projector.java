package com.idearfly.timeline;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 放映室，用于运行故事
 */
public class Projector {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            Film film = null;
            for (;;) {
                while ((film = films.peek()) != null) {
                    try {
                        if (film == null) {
                            films.take();
                            break;
                        } else {
                            if (film.play()) {
                                films.take();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    LinkedBlockingQueue<Film> films = new LinkedBlockingQueue<>();

    public Projector add(Film film){
        films.add(film);
        return this;
    }

    public Projector(String name) {
        thread.setName(name + " " + Projector.class.getSimpleName());
        thread.start();
    }
}
