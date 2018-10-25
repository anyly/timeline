package com.idearfly.timeline;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * 放映室，用于运行故事
 */
public class Projector {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            Film film = null;
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
                ListIterator<Film> filmListIterator = films.listIterator();
                while (filmListIterator.hasNext()) {
                    try {
                        film = filmListIterator.next();
                        if (film != null) {
                            if (film.play()) {
                                filmListIterator.remove();
                                continue;
                            }
                        } else {
                            filmListIterator.remove();
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    LinkedList<Film> films = new LinkedList<>();

    public Projector add(Film film){
        synchronized (thread) {
            films.add(film);
            thread.notify();
        }
        return this;
    }

    public Projector() {
        thread.start();
    }

    public Projector tryAgain() {
        synchronized (thread) {
            thread.notify();
        }
        return this;
    }
}
