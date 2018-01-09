package fr.inria.stamp.mutationtest.test.fakeoperators;

import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;

public class OperatorMultipleStatic implements MutationOperator {

    public OperatorMultipleStatic(int count) {

    }

    public boolean canMutate(Method method) {
        return false;
    }

    public void generateCode(Method method, MethodVisitor mv) {

    }

    public String getID() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public static OperatorMultipleStatic one() {
        return new OperatorMultipleStatic(1);
    }

    public static OperatorMultipleStatic two() {
        return new OperatorMultipleStatic(2);
    }
}
