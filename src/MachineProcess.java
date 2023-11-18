import java.util.*;
import java.util.stream.Stream;

public final class MachineProcess {
    public final String name;
    public final EnumSet<State> state;
    public final List<Instruction> instructions;
    public final List<Integer> registers;

    public MachineMonoSemaphore semaphore;
    public int ip;

    public MachineProcess(String name) {
        this.name = name == null ? "unnamed" : name;
        this.state = EnumSet.noneOf(State.class);
        this.instructions = new ArrayList<>();
        this.registers = Arrays.asList(
                Stream.generate(() -> 0).limit(1024).toArray(Integer[]::new));

        this.semaphore = null;
        this.ip = 0;
    }

    public enum State { SUSPENDED, COMPILED, STARTED, LOCK_SUSPENDED }

    public Instruction currentInstruction() {
        return instructions.get(ip);
    }

    public static MachineProcessBuilder builder(final String name) {
        return new MachineProcessBuilder(name);
    }

    public void start() {
        state.add(State.STARTED);
        state.remove(State.SUSPENDED);
    }

    public void suspend() {
        state.add(State.SUSPENDED);
    }

    public void raisePointer() {
        ip++;
    }

    public boolean atEnd() {
        return ip == instructions.size();
    }

    public void lockSuspend() {
        state.add(State.LOCK_SUSPENDED);
    }

    public void liftSuspend() {
        state.remove(State.LOCK_SUSPENDED);
    }

    public static class MachineProcessBuilder {
        private final MachineProcess instance;

        public MachineProcessBuilder(final String name) {
            instance = new MachineProcess(name);
        }

        public MachineProcessBuilder add(Instruction i) {
            if (!instance.state.contains(State.COMPILED)) {
                instance.instructions.add(i);
            }
            return this;
        }

        public MachineProcess compile() {
            instance.state.add(State.COMPILED);
            return instance;
        }

        @Override
        public String toString() {
            return STR."MachineProcessBuilder[MachineProcess \{instance.name}]";
        }
    }
}
