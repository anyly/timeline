package test;


import com.idearfly.timeline.Dispatcher;
import com.idearfly.timeline.Event;
import com.idearfly.timeline.Plot;
import com.idearfly.timeline.Story;

public class TestStory {
    public static void main(String[] args) {
        Story story = Story.
                configuration()
                .name("故事")
                .plot(new Plot("预设1") {
                    @Override
                    public void doing() {
                        System.out.println(getName());
                    }
                })
                .plot(new Plot("预设2") {
                    @Override
                    public void doing() {
                        System.out.println(getName());
                    }
                })
                .timeline()
                .meanwhile(new Dispatcher("并行")
                        .line(new Event("并行事件") {
                            @Override
                            public void doing() {
                                System.out.println(getName());
                            }

                            @Override
                            public boolean when() {
                                return true;
                            }

                            @Override
                            public boolean ending() {
                                return true;
                            }
                        })
                        .line(new Event("并行未触发事件") {
                            @Override
                            public void doing() {
                                System.out.println(getName());
                            }

                            @Override
                            public boolean when() {
                                return false;
                            }

                            @Override
                            public boolean ending() {
                                return false;
                            }
                        })
                        .line("预设1")
                )
                .then(new Event("串行事件") {
                    @Override
                    public void doing() {
                        System.out.println(getName());
                    }

                    @Override
                    public boolean when() {
                        return true;
                    }

                    @Override
                    public boolean ending() {
                        return true;
                    }
                })
                .then(new Plot("串行剧情") {
                    @Override
                    public void doing() {
                        System.out.println(getName());
                    }
                })
                .then("预设2")
                .construct();

        story.play();
    }
}
