package org.idear.timeline;

/**
 * 故事
 */
public class Story {

    private Story() {

    }

    public static Timeline timeline() {
        return new Timeline() {
            private Story story = new Story();

            @Override
            public Timeline name(String name) {
                return this;
            }

            @Override
            public Timeline meanwhile(Dispatcher dispatcher) {
                return this;
            }

            @Override
            public Timeline then(Plot plot) {
                return this;
            }

            @Override
            public Story construct() {
                return story;
            }
        };
    }
}
