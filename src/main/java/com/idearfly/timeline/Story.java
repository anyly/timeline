package com.idearfly.timeline;

import java.util.*;

/**
 * 故事
 */
public class Story implements Film {
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

        sequence.poll();
        // 下一步执行
        Stage handler = null;
        while ((handler = sequence.peek()) != null) {
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
            sequence.poll();
        }
        return true;
    }

    /**
     * 判断是否满足当前条件，满足时进入下一步，不满足时暂停
     * @return
     */
    public boolean checkAllowCondition() {
        Stage current = sequence.peek();
        if (current == null) {
            return false;
        }
        if (current instanceof Event) {
            Event event = (Event)current;
            if (!event.ending()) {
                return false;
            }
        } else if (current instanceof Dispatcher) {
            Dispatcher dispatcher = (Dispatcher)current;
            if (!dispatcher.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否已完成
     * @return
     */
    public boolean isCompleted() {
        return sequence.isEmpty();
    }

    /**
     * 当前阶段
     * @return
     */
    public String currentStage() {
        Stage current = sequence.peek();
        if (current == null) {
            return null;
        }
        return current.getName();
    }
}
