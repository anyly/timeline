package test;

import org.idear.timeline.Plot;
import org.idear.timeline.Projector;
import org.idear.timeline.Story;

public class TestProjector {
    public static void main(String[] args) {
        new Projector().add(Story.timeline().then(new Plot("故事1") {
            @Override
            public void doing() {
                System.out.println(getName());
            }
        }).construct())
                .add(Story.timeline().then(new Plot("故事2") {
            @Override
            public void doing() {
                System.out.println(getName());
            }
        }).construct());
    }
}
