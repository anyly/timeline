package com.idearfly.timeline;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 故事
 */
public class Story {
    // 故事名称
    private String name;
    // 初始剧本
    private LinkedList<Stage> sequence = new LinkedList();
    // 完成的部分
    private LinkedList<Stage> completed = new LinkedList();

    private Map<String, Plot> plotMap = new LinkedHashMap<>();

    private Story() {

    }

    public static class Configuration {
        private Story _story = new Story();

        public Configuration name(String name) {
            _story.name = name;
            return this;
        }

        public Configuration plot(Plot plot) {
            _story.plotMap.put(plot.getName(), plot);
            return this;
        }

        public Timeline timeline() {
            return Story.timeline(_story);
        }

        public Story construct() {
            return _story;
        }
    }

    public static Configuration configuration() {
        return new Configuration();
    }

    public static Timeline timeline() {
        return timeline(null);
    }

    private static Timeline timeline(Story story) {
        if (story == null) {
            story =  new Story();
        }
        Story finalStory = story;
        return new Timeline() {
            private Story _story = finalStory;

            @Override
            public Timeline meanwhile(Dispatcher dispatcher) {
                for (String plot: dispatcher.ploatName) {
                    Plot handler = _story.plotMap.get(plot);
                    if (handler == null) {
                        throw new NullPointerException(String.format("%s is not installed to Story yet"));
                    }
                    dispatcher.multiLine.add(handler);
                }
                dispatcher.ploatName = null;
                _story.sequence.add(dispatcher);
                return this;
            }

            @Override
            public Timeline then(Plot plot) {
                _story.sequence.add(plot);
                return this;
            }

            @Override
            public Timeline then(String plot) {
                Plot handler = _story.plotMap.get(plot);
                if (handler == null) {
                    throw new NullPointerException(String.format("%s is not installed to Story yet"));
                }
                _story.sequence.add(handler);
                return this;
            }

            @Override
            public Story construct() {
                return _story;
            }

            @Override
            public Dispatcher dispatcher(String name) {
                Dispatcher dispatcher = new Dispatcher(name);
                return dispatcher;
            }
        };
    }

    public boolean play() {
        // 是否已经完成
        if (isCompleted()) {
            return true;
        }
        // 当前步骤是否通过
        if (!checkAllowCondition()) {
            return false;
        }

        // 下一步执行
        int nextIndex = completed.size();
        for (int i=nextIndex; i<sequence.size(); i++) {
            Stage handler = sequence.get(i);
            if (handler instanceof Dispatcher) {
                Dispatcher dispatcher = (Dispatcher)handler;
                dispatcher.dispatch();
                if (!dispatcher.isCompleted()) {
                    return false;
                }
            } else if (handler instanceof Event) {
                Event event = (Event)handler;
                if (event.when()) {
                    event.doing();
                    if (!event.ending()) {
                        return false;
                    }
                }

            } else if (handler instanceof Plot) {
                Plot plot = (Plot) handler;
                plot.doing();
            }
            completed.add(handler);
        }
        return true;
    }

    public boolean checkAllowCondition() {
        try {
            Stage last = completed.getLast();
            if (last instanceof Event) {
                Event event = (Event)last;
                if (!event.ending()) {
                    return false;
                }
            } else if (last instanceof Dispatcher) {
                Dispatcher dispatcher = (Dispatcher)last;
                if (!dispatcher.isCompleted()) {
                    return false;
                }
            }
        } catch (NoSuchElementException e) {

        }
        return true;
    }

    public boolean isCompleted() {
        return completed.size() == sequence.size();
    }

    public String currentStage() {
        Stage last = completed.getLast();
        return last.getName();
    }
}
