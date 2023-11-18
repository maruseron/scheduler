public class Logger {
    public static void instruction(final String processName, final Instruction ins) {
        final var message = "[ DEBUG ] (INSTRUCTION: " + switch (ins) {
            case Instruction.LockInstruction(var register) ->
                STR."LOCK) PROCESS: \{processName} - LOCK ON REGISTER \{register}";
            case Instruction.UnlockInstruction(var register) ->
                STR."UNLOCK) PROCESS: \{processName} - RELEASE LOCK ON REGISTER \{register}";
            case Instruction.AllocateInstruction(var register, var argument, var memScope) ->
                STR."ALLOC) PROCESS: \{processName} - ALLOCATE \{argument} ON REGISTER \{register} AT \{memScope}";
            case Instruction.BinaryOperationInstruction(var register, var argument, var operation, var memScope) ->
                STR."BINARY) PROCESS: \{processName} - \{operation} \{argument} TO \{register} AT \{memScope}";
            case Instruction.PrintInstruction(var register, var memScope) ->
                STR."PRINT) PROCESS: \{processName} - PRINT \{register} FROM \{memScope}";
        };
        System.out.println(message);
    }

    public static void lock(final String processName, final boolean suspended) {
        System.out.println(STR."[ DEBUG ] (SEMAPHORE) \{processName} \{suspended ? "SUSPENDED ON LOCK" : "LOCKED SEMAPHORE"}");
    }

    public static void error(final Error error) {
        System.out.println(STR."[ ERROR ]: \{error.getClass()}: \{error.getMessage()}");
        System.exit(42);
    }
}
