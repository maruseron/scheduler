public class Main {
    public static void main(String[] args) {
        var machine = new Machine();
        var process1 = MachineProcess.builder()
                .setName("PROCESS 1")
                .add(Instruction.lock(0))
                .add(Instruction.alloc(5, 0, true))
                .add(Instruction.add(3, 0, true))
                .add(Instruction.print(0, true))
                .add(Instruction.unlock(0))
                .compile();

        var process2 = MachineProcess.builder()
                .setName("PROCESS 2")
                .add(Instruction.lock(0))
                .add(Instruction.alloc(12, 0, true))
                .add(Instruction.add(2, 0, true))
                .add(Instruction.print(0, true))
                .add(Instruction.unlock(0))
                .compile();

        machine.setAverageMicroTimePolicy(2);
        machine.fork(process1);
        machine.fork(process2);
        machine.run();
    }
}