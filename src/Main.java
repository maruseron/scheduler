public class Main {
    public static void main(String[] args) {
        var machine = new Machine();
        var process1 = MachineProcess.builder("PROCESS 1")
                .add(Instruction.lock(0))
                .add(Instruction.alloc(0, 5, Instruction.MemoryScope.SHARED))
                .add(Instruction.add(0, 5, Instruction.MemoryScope.SHARED))
                .add(Instruction.print(0, Instruction.MemoryScope.SHARED))
                .add(Instruction.unlock(0))
                .compile();

        var process2 = MachineProcess.builder("PROCESS 2")
                .add(Instruction.lock(0))
                .add(Instruction.alloc(0, 12, Instruction.MemoryScope.SHARED))
                .add(Instruction.add(0, 2, Instruction.MemoryScope.SHARED))
                .add(Instruction.print(0, Instruction.MemoryScope.SHARED))
                .add(Instruction.unlock(0))
                .compile();

        machine.setAverageMicroTimePolicy(2);
        machine.fork(process1);
        machine.fork(process2);
        machine.run();
    }
}