public sealed interface Instruction
        permits Instruction.LockInstruction,
                Instruction.UnlockInstruction,
                Instruction.AllocateInstruction,
                Instruction.BinaryOperationInstruction,
                Instruction.PrintInstruction {

    static Instruction lock(int register) {
        return new LockInstruction(register);
    }

    static Instruction unlock(int register) {
        return new UnlockInstruction(register);
    }

    static Instruction alloc(int register, int argument, MemoryScope memoryScope) {
        return new AllocateInstruction(register, argument, memoryScope);
    }

    static Instruction add(int register, int argument, MemoryScope memoryScope) {
        return new BinaryOperationInstruction(register, argument, BinaryOp.ADD, memoryScope);
    }

    static Instruction sub(int register, int argument, MemoryScope memoryScope) {
        return new BinaryOperationInstruction(register, argument, BinaryOp.SUB, memoryScope);
    }

    static Instruction print(int register, MemoryScope memoryScope) {
        return new PrintInstruction(register, memoryScope);
    }

    enum MemoryScope { LOCAL, SHARED }
    enum BinaryOp { ADD, SUB }

    record LockInstruction(int register) implements Instruction {}
    record UnlockInstruction(int register) implements Instruction {}
    record AllocateInstruction(int register, int argument, MemoryScope memoryScope) implements Instruction {}
    record BinaryOperationInstruction(int register, int argument, BinaryOp binaryOp, MemoryScope memoryScope) implements Instruction {}
    record PrintInstruction(int register, MemoryScope memoryScope) implements Instruction {}
}
