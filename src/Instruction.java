import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract sealed class Instruction
        permits Instruction.LockInstruction,
                Instruction.UnlockInstruction,
                Instruction.AllocateInstruction,
                Instruction.AddInstruction,
                Instruction.SubtractInstruction,
                Instruction.PrintInstruction {

    @Contract("_ -> new")
    public static @NotNull LockInstruction lock(int reg) {
        return new LockInstruction(reg);
    }

    @Contract("_ -> new")
    public static @NotNull UnlockInstruction unlock(int reg) {
        return new UnlockInstruction(reg);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull AllocateInstruction alloc(int arg, int reg, boolean shr) {
        return new AllocateInstruction(arg, reg, shr);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull AddInstruction add(int arg, int reg, boolean shr) {
        return new AddInstruction(arg, reg, shr);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull SubtractInstruction sub(int arg, int reg, boolean shr) {
        return new SubtractInstruction(arg, reg, shr);
    }

    @Contract("_, _ -> new")
    public static @NotNull PrintInstruction print(int reg, boolean shr) {
        return new PrintInstruction(reg, shr);
    }

    public static final class LockInstruction extends Instruction {
        public final int register;
        public final boolean shared;
        public LockInstruction(int register) {
            this.register = register;
            this.shared = true;
        }
    }

    public static final class UnlockInstruction extends Instruction {
        public final int register;
        public final boolean shared;
        public UnlockInstruction(int register) {
            this.register = register;
            this.shared = true;
        }
    }

    public static final class AllocateInstruction extends Instruction {
        public final int argument;
        public final int register;
        public final boolean shared;
        public AllocateInstruction(int argument, int register, boolean shared) {
            this.argument = argument;
            this.register = register;
            this.shared = shared;
        }
    }

    public static final class AddInstruction extends Instruction {
        public final int argument;
        public final int register;
        public final boolean shared;
        public AddInstruction(int argument, int register, boolean shared) {
            this.argument = argument;
            this.register = register;
            this.shared = shared;
        }
    }

    public static final class SubtractInstruction extends Instruction {
        public final int argument;
        public final int register;
        public final boolean shared;
        public SubtractInstruction(int argument, int register, boolean shared) {
            this.argument = argument;
            this.register = register;
            this.shared = shared;
        }
    }

    public static final class PrintInstruction extends Instruction {
        public final int register;
        public final boolean shared;
        public PrintInstruction(int register, boolean shared) {
            this.register = register;
            this.shared = shared;
        }
    }
}
