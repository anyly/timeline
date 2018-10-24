package com.idearfly.timeline;

import java.util.LinkedList;

/**
 * 放映室，用于运行故事
 */
public class Projector {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            Story story = null;
            for (;;) {
                if (stories.size() == 0) {
                    synchronized (thread) {
                        try {
                            thread.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                int i=0;
                while (i<stories.size()) {
                    try {
                        story = stories.get(i);
                        if (story != null) {
                            if (story.play()) {
                                stories.remove(i);
                                continue;
                            }
                        } else {
                            stories.remove(i);
                            continue;
                        }
                        i++;
                    } catch (Exception e) {

                    }
                }
            }
        }
    });

    LinkedList<Story> stories = new LinkedList<>();

    public Projector add(Story story){
        stories.add(story);
        thread.notify();
        return this;
    }

    public Projector() {
        thread.start();
    }

    public Projector tryAgain() {
        thread.notify();
        return this;
    }
}
