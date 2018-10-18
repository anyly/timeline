package org.idear.timeline;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * 放映室，用于运行故事
 */
public class Projector {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            Story story = null;
            for (;;) {
                try {
                    for (;;){
                        story = stories.getFirst();
                        if (story != null) {
                            if (story.play()) {
                                stories.poll();
                            }
                        } else {
                            stories.poll();
                        }
                    }
                } catch (NoSuchElementException e) {

                }
            }
        }
    });

    LinkedList<Story> stories = new LinkedList<>();

    public Projector add(Story story){
        stories.add(story);
        return this;
    }

    public Projector() {
        thread.start();
    }
}
