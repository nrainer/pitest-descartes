package fr.inria.stamp.mutationtest.descartes.operators;

import fr.inria.stamp.mutationtest.descartes.operators.parsing.OperatorParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MutationOperatorCreator {

//    private MutationOperatorCreator() {}
//
//    public static MutationOperator fromID(String id) {
//        OperatorParser parser = new OperatorParser(id);
//        Object value = parser.parse();
//        if(parser.hasErrors())
//            throw new WrongOperatorException("Invalid operator id: " + parser.getErrors().get(0));
//        if(value == null)
//            return NullMutationOperator.getInstance();
//        if(value.equals(Void.class)) {
//            return VoidMutationOperator.getInstance();
//        }
//        if(value.equals(EmptyArrayMutationOperator.getInstance().getID())) {
//            return EmptyArrayMutationOperator.getInstance();
//        }
//        try {
//            return new ConstantMutationOperator(id, value);
//        }catch (IllegalArgumentException exc) {
//            throw new WrongOperatorException("Invalid operator id", exc);
//        }
//    }
//
//    public static MutationOperator fromClassName(String className) {
//
//    }

}
