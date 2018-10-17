package test;


import org.idear.timeline.*;

public class TestStory {
    public static void main(String[] args) {
        Story story = Story.
                timeline()
                .name("故事")
                .meanwhile(new Dispatcher("并行")
                        .line(new Event("并行1") {
                            @Override
                            public void doing() {

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
                        .line(new Event("并行2") {
                            @Override
                            public void doing() {

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
                )
                .then(new Plot("串行") {
                    @Override
                    public void doing() {

                    }
                })
                .construct();
    }
}
