import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Machine {
    // ideally an instance value, but hoping making this static final inlines most checks for better
    // performance
    public static final boolean debugMode = true;
    private int instructionsPerCycle = 2;

    public final Deque<MachineProcess> queue = new LinkedList<>();
    private final List<Integer> registers =
            Arrays.asList(Stream.generate(() -> 0).limit(1024).toArray(Integer[]::new));
    private final List<MachineMonoSemaphore> semaphores =
            Arrays.asList(Stream.generate(() -> this)
                    .limit(1024)
                    .map(MachineMonoSemaphore::new)
                    .toArray(MachineMonoSemaphore[]::new));

    public void fork(MachineProcess p) {
        queue.add(p);
    }

    public void setAverageMicroTimePolicy(int instructions) {
        this.instructionsPerCycle = instructions;
    }

    public void run() {
        while (!queue.isEmpty()) {
            var process = queue.poll();
            if (!process.state.contains(MachineProcess.State.COMPILED))
                Logger.error(new Error(STR."UNCOMPILED PROCESS: \{process.name}"));
            process.start();
            var instructionsAtCurrentProcess = 0;
            while (!process.atEnd()) {
                if (process.state.contains(MachineProcess.State.LOCK_SUSPENDED)) break;
                if (instructionsAtCurrentProcess >= instructionsPerCycle) {                         // if we've exhausted the amount of instructions per cycle,
                    queue.add(process);                                                             // we return the process to the queue
                    process.suspend();
                    break;
                }

                exec(process, process.currentInstruction());
                instructionsAtCurrentProcess++;
            }
        }
    }

    public void exec(MachineProcess process, Instruction ins) {
        if (debugMode) Logger.instruction(process.name, ins);
        switch (ins) {
            case Instruction.LockInstruction(var register) -> {
                var semaphore = semaphores.get(register);
                var locked = semaphore.lock(process);
                if (locked)
                    process.raisePointer();
            }
            case Instruction.UnlockInstruction(var register) -> {
                var semaphore = semaphores.get(register);
                semaphore.unlock(process);
                process.raisePointer();
            }
            case Instruction.AllocateInstruction(var register, var argument, var memoryScope) -> {
                selectRegisters(process, register, memoryScope, true).set(register, argument);
                process.raisePointer();
            }
            case Instruction.BinaryOperationInstruction(var register, var argument, var operation, var memoryScope) -> {
                var usedRegisters = selectRegisters(process, register, memoryScope, true);

                switch (operation) {
                    case ADD -> usedRegisters.set(register, usedRegisters.get(register) + argument);
                    case SUB -> usedRegisters.set(register, usedRegisters.get(register) - argument);
                }
                process.raisePointer();
            }
            case Instruction.PrintInstruction(var register, var memoryScope) -> {
                System.out.println(selectRegisters(process, register, memoryScope).get(register));
                process.raisePointer();
            }
        }
    }

    public List<Integer> selectRegisters(MachineProcess process, int register,
                                         Instruction.MemoryScope memoryScope) {
        return selectRegisters(process, register, memoryScope, false);
    }

    public List<Integer> selectRegisters(MachineProcess process, int register,
                                         Instruction.MemoryScope memoryScope,
                                         boolean mutating) {
        return switch (memoryScope) {
            case SHARED -> {
                if (mutating && !(semaphores.get(register) == process.semaphore))
                    throw new Error(STR."PROCESS \{process.name} ATTEMPTED TO REACH REGISTER LOCKED BY OTHER");

                yield registers;
            }
            case LOCAL -> process.registers;
        };
    }
}
