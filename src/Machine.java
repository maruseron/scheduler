import java.util.*;

public class Machine {
    // ideally an instance value, but hoping making this static final inlines most checks for better
    // performance
    public static final boolean debugMode = true;
    public final MachineMonoSemaphore[] semaphores = new MachineMonoSemaphore[1024];
    {
        for (int i = 0; i < 1024; i++) {
            semaphores[i] = new MachineMonoSemaphore(this);
        }
    }

    public final int[] registers = new int[1024];
    public final Deque<MachineProcess> queue = new LinkedList<>();
    private int instructionsPerCycle = 2;

    public void fork(MachineProcess p) {
        queue.add(p);
    }

    public void setAverageMicroTimePolicy(int instructions) {
        this.instructionsPerCycle = instructions;
    }

    public boolean isOnDebugMode() {
        return debugMode;
    }

    public void run() {
        while (!queue.isEmpty()) {
            var process = queue.poll();
            if (!process.isCompiled())
                throw new Error("[ ERROR ] Uncompiled process found");
            process.setStarted();
            var instructionsAtCurrentProcess = 0;
            while (!process.atEnd()) {
                if (process.isLockSuspended()) break;
                if (instructionsAtCurrentProcess >= instructionsPerCycle) { // si se agotó el tiempo de proceso,
                    queue.add(process);                                                                   // devolvemos el proceso a la cola
                    process.suspend();
                    break;
                } else {
                    // si no, "ejecutamos" la instrucción actual y movemos el puntero
                    // System.out.println(process.getName() + " -> " + process.instructions().get(process.raisePointer()));
                    exec(process, process.instructions().get(process.getIp()));
                    instructionsAtCurrentProcess++;
                }
            }
        }
    }

    public void exec(MachineProcess process, Instruction ins) {

        switch (ins) {
            case Instruction.LockInstruction lock -> {
                if (debugMode) System.out.println("[ DEBUG ] INSTRUCTION - PROCESS: " + process.getName() + " -> LOCK INSTRUCTION");
                if (lock.shared) {
                    var semaphore = semaphores[lock.register];
                    if (semaphore.lock(process))
                        process.raisePointer();
                }
            }
            case Instruction.UnlockInstruction unlock -> {
                if (debugMode) System.out.println("[ DEBUG ] INSTRUCTION - PROCESS: " + process.getName() + " -> UNLOCK INSTRUCTION");
                if (unlock.shared) {
                    var semaphore = semaphores[unlock.register];
                    semaphore.unlock(process);
                }
                process.raisePointer();
            }
            case Instruction.AllocateInstruction alloc -> {
                if (alloc.shared) {
                    if (semaphores[alloc.register] == process.getSemaphore()) {
                        if (debugMode) System.out.println("[ DEBUG ] INSTRUCTION - PROCESS: " + process.getName() + " -> ALLOCATE INSTRUCTION THROUGH SEMAPHORE");
                        process.raisePointer();
                        registers[alloc.register] = alloc.argument;
                    }
                } else {
                    if (debugMode) System.out.println("[ DEBUG ] INSTRUCTION - PROCESS: " + process.getName() + " -> ALLOCATE INSTRUCTION ON PROCESS REGISTER");
                    process.raisePointer();
                    process.registers[alloc.register] = alloc.argument;
                }
            }
            case Instruction.AddInstruction add -> {
                if (add.shared) {
                    if (semaphores[add.register] == process.getSemaphore()) {
                        if (debugMode) System.out.println("[ DEBUG ] INSTRUCTION - PROCESS: " + process.getName() + " -> ADD INSTRUCTION THROUGH SEMAPHORE");
                        process.raisePointer();
                        registers[add.register] += add.argument;
                    }
                } else {
                    if (debugMode) System.out.println("[ DEBUG ] INSTRUCTION - PROCESS: " + process.getName() + " -> ADD INSTRUCTION ON PROCESS REGISTER");
                    process.raisePointer();
                    process.registers[add.register] += add.argument;
                }
            }
            // TODO: make shared memory aware
            case Instruction.SubtractInstruction sub -> {
                if (debugMode) System.out.println("[ DEBUG ] INSTRUCTION - PROCESS: " + process.getName() + " -> SUBTRACT INSTRUCTION");
                process.raisePointer();
                var registers = sub.shared ? this.registers : process.registers;
                registers[sub.register] -= sub.argument;
            }
            case Instruction.PrintInstruction print -> {
                if (debugMode) System.out.println("[ DEBUG ] INSTRUCTION - PROCESS: " + process.getName() + " -> PRINT INSTRUCTION");
                process.raisePointer();
                var registers = print.shared ? this.registers : process.registers;
                System.out.println("[ PRINT ] PROCESS: " + process.getName() + " -> " + registers[print.register]);
            }
        }
    }
}
