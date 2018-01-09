package fr.inria.stamp.mutationtest.test.fakeoperators;

import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;

public class OperatorWithStaticMethod implements MutationOperator {

    public OperatorWithStaticMethod(int count) {

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

    public static OperatorWithStaticMethod create() {
        return new OperatorWithStaticMethod(0);
    }
}
