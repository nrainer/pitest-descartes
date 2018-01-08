package fr.inria.stamp.mutationtest.descartes.operators.parsing;

import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;

import org.junit.Test;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class OperatorFromNameTest {

    static class DummyOperator implements MutationOperator {

        @Override
        public boolean canMutate(Method method) {
            return false;
        }

        @Override
        public void generateCode(Method method, MethodVisitor mv) {

        }

        @Override
        public String getID() {
            return null;
        }

        @Override
        public String getDescription() {
            return null;
        }
    }

    @Test
    public void shouldCreateOperatorFromName() {
        final String name = "fr.inria.stamp.mutationtest.descartes.operators.parsing.OperatorFromNameTest$DummyOperator";
        MutationOperator operator  = MutationOperator.fromID(name);
        assertThat(operator, instanceOf(DummyOperator.class));
    }

}
