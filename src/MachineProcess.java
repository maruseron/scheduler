import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MachineProcess {

    private String name = "unnamed";
    private int ip = 0;
    private boolean compiled = false;
    private boolean started = false;
    private boolean suspended = false;
    private final List<Instruction> instructions = new ArrayList<>();
    public final int[] registers = new int[1024];
    private boolean lockSuspended = false;
    private MachineMonoSemaphore semaphore = null;

    @Contract(" -> new")
    public static @NotNull MachineProcessBuilder builder() {
        return new MachineProcessBuilder();
    }

    public String getName() {
        return name;
    }

    public boolean isCompiled() {
        return compiled;
    }

    public void setStarted() {
        started = true;
        suspended = false;
    }

    public boolean hasStarted() {
        return started;
    }

    public void suspend() {
        suspended = true;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public int getIp() {
        return ip;
    }

    public int raisePointer() {
        return ip++;
    }

    public boolean atEnd() {
        return ip == instructions.size();
    }

    public List<Instruction> instructions() {
        return Collections.unmodifiableList(instructions);
    }

    public boolean isLockSuspended() {
        return lockSuspended;
    }

    public void setLockSuspended(boolean lockSuspended) {
        this.lockSuspended = lockSuspended;
    }

    public void setSemaphore(MachineMonoSemaphore semaphore) {
        this.semaphore = semaphore;
    }

    public MachineMonoSemaphore getSemaphore() {
        return semaphore;
    }

    public static class MachineProcessBuilder {
        MachineProcess instance;

        public MachineProcessBuilder() {
            instance = new MachineProcess();
        }
        public MachineProcessBuilder add(Instruction i) {
            if (!instance.compiled) {
                instance.instructions.add(i);
            }
            return this;
        }

        public MachineProcess compile() {
            instance.compiled = true;
            return instance;
        }

        public MachineProcessBuilder setName(String name) {
            instance.name = name;
            return this;
        }
    }
}
