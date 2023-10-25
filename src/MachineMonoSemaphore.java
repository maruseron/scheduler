import java.util.Deque;
import java.util.LinkedList;

public class MachineMonoSemaphore {
    private final Machine owner;
    private boolean taken;
    private final Deque<MachineProcess> queue = new LinkedList<>();

    public MachineMonoSemaphore(Machine owner) {
        this.owner = owner;
        this.taken = false;
    }
    public boolean lock(MachineProcess process) {
        if (!taken) {
            if (Machine.debugMode) System.out.println("[ DEBUG ] PROCESS: " + process.getName() + " -> SEMAPHORE LOCKED");
            process.setSemaphore(this);
            return taken = true;
        } else {
            if (Machine.debugMode) System.out.println("[ DEBUG ] PROCESS: " + process.getName() + " -> SUSPENDED ON LOCK");
            process.setLockSuspended(true);
            process.suspend();
            queue.add(process);
            return false;
        }
    }

    public boolean unlock(MachineProcess process) {
        process.setSemaphore(null);
        if (taken && !queue.isEmpty()) {
            var unlocked = queue.poll();
            unlocked.setLockSuspended(false);
            owner.queue.add(unlocked);
        }
        return !(taken = false);
    }
}
