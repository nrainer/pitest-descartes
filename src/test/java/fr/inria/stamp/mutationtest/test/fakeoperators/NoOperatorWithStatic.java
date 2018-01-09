package fr.inria.stamp.mutationtest.test.fakeoperators;

import fr.inria.stamp.mutationtest.descartes.operators.ConstantMutationOperator;
import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;

public class NoOperatorWithStatic {

    public static MutationOperator getAConstant() {
        return new ConstantMutationOperator("3", 3);
    }

}
