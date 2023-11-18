import java.util.Deque;
import java.util.LinkedList;

public final class MachineMonoSemaphore {
    private final Machine owner;
    private boolean taken;
    private final Deque<MachineProcess> queue = new LinkedList<>();

    public MachineMonoSemaphore(Machine owner) {
        this.owner = owner;
        this.taken = false;
    }
    public boolean lock(MachineProcess process) {
        if (Machine.debugMode)
            Logger.lock(process.name, taken);
        if (!taken) {
            process.semaphore = this;
            return taken = true;
        } else {
            process.lockSuspend();
            process.suspend();
            queue.add(process);
            return false;
        }
    }

    public boolean unlock(MachineProcess process) {
        process.semaphore = null;
        if (taken && !queue.isEmpty()) {
            var unlocked = queue.poll();
            unlocked.liftSuspend();
            owner.queue.add(unlocked);
        }
        return !(taken = false); // intellij keeps thinking this is a condition ?
    }

    @Override
    public String toString() {
        return STR."MachineMonoSemaphore@\{hashCode()}{owner=\{owner}, taken=\{taken}}";
    }
}
