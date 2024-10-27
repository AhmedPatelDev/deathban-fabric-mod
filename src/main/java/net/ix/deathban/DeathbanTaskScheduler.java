package net.ix.deathban;

import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeathbanTaskScheduler {
    private static final List<ScheduledTask> tasks = new ArrayList<>();

    public static void scheduleTask(Runnable task, int delayTicks) {
        tasks.add(new ScheduledTask(task, delayTicks));
    }

    public static void tick(MinecraftServer server) {
        Iterator<ScheduledTask> iterator = tasks.iterator();

        while (iterator.hasNext()) {
            ScheduledTask scheduledTask = iterator.next();
            scheduledTask.ticksRemaining--;

            if (scheduledTask.ticksRemaining <= 0) {
                scheduledTask.task.run();
                iterator.remove();
            }
        }
    }

    private static class ScheduledTask {
        private final Runnable task;
        private int ticksRemaining;

        public ScheduledTask(Runnable task, int ticksRemaining) {
            this.task = task;
            this.ticksRemaining = ticksRemaining;
        }
    }
}
