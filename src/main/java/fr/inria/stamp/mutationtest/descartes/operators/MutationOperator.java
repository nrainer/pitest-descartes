package fr.inria.stamp.mutationtest.descartes.operators;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;
import fr.inria.stamp.mutationtest.descartes.operators.parsing.OperatorParser;

/**
 * Mutation operator definition
 */
public interface MutationOperator {

    /**
     * Returns a value indicating whether the operator can transform the given method.
     *
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if the mutation can be performed
     */
    boolean canMutate(Method method);

    /**
     * Generates the mutated code for the given method.
     * @param method method to be mutated
     * @param mv Method visitor for code generation.
     */
    void generateCode(Method method, MethodVisitor mv);

    /**
     * Gets the ID of this mutation operator to be used in the final report.
     * @return A string identifying the mutation operator.
     */
    String getID();

    /**
     * Gets the description of this mutation operator to be used in the final report.
     * @return A string containing the description of the mutation operator.
     */
    String getDescription();


    /**
     * Constructs a mutation operator instance from the given the identifier.
     * @param id String identifying the operator instance to construct.
     * @return A mutation operator instance matching the given identifier.
     * @throws WrongOperatorException if no operator can be obtained from the given identifier.
     */
    static MutationOperator fromID(String id) throws WrongOperatorException {
        OperatorParser parser = new OperatorParser();
        MutationOperator result = parser.parse(id);
        if(parser.hasErrors())
            throw new WrongOperatorException(id, parser.getFirstError());
        return result;
    }

}
