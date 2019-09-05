package net.manaty.octopusync.service.report;

import net.manaty.octopusync.model.Timestamped;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class ReportEventProcessor {

    public interface EventVisitor {
        void visit(Class<?> eventType, Timestamped event);
        void finish();
    }

    private final Map<Class<?>, Iterator<? extends Timestamped>> iteratorsByType;
    private final PriorityQueue<ReportEvent> queue;

    public ReportEventProcessor(Map<Class<?>, Iterator<? extends Timestamped>> iteratorsByType) {
        this.iteratorsByType = iteratorsByType;
        this.queue = new PriorityQueue<>(Timestamped.ASC_COMPARATOR);
    }

    public void visitEvents(EventVisitor visitor) {
        // initially, put one object of each type into queue
        iteratorsByType.forEach((type, iterator) -> {
            if (iterator.hasNext()) {
                queue.add(new ReportEvent(type, iterator, iterator.next()));
            }
        });

        // visit events one by one,
        // in the order of their timestamps,
        // unless there are no events left
        ReportEvent event;
        while ((event = queue.poll()) != null) {
            event.visit(visitor);
            ReportEvent next = event.next();
            if (next != null) {
                queue.add(next);
            }
        }
        visitor.finish();
    }

    private class ReportEvent implements Timestamped {

        private final Class<?> type;
        private final Iterator<? extends Timestamped> iterator;
        private final Timestamped event;

        private ReportEvent(Class<?> type, Iterator<? extends Timestamped> iterator, Timestamped event) {
            this.type = type;
            this.iterator = iterator;
            this.event = event;
        }

        public @Nullable ReportEvent next() {
            return iterator.hasNext() ? new ReportEvent(type, iterator, iterator.next()) : null;
        }

        @Override
        public long timestamp() {
            return event.timestamp();
        }

        public void visit(EventVisitor visitor) {
            visitor.visit(type, event);
        }
    }
}
