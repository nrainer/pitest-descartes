package fr.inria.stamp.mutationtest.descartes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

import fr.inria.stamp.mutationtest.descartes.operators.*;
import fr.inria.stamp.mutationtest.descartes.operators.parsing.OperatorParser;
import org.pitest.reloc.asm.commons.Method;

import org.pitest.functional.predicate.*;

import org.pitest.mutationtest.MutationEngineFactory;
import org.pitest.mutationtest.engine.MutationEngine;

public class DescartesEngineFactory implements MutationEngineFactory{

    /**
     * Creates the engine instance from the given configuration
     * @param mutateStaticInitializers
     * @param excludedMethods
     * @param loggingClasses
     * @param mutators
     * @param detectInLinedCode In our case this can be ignored
     * @return
     */
    public MutationEngine createEngine(final boolean mutateStaticInitializers,
                                       final Predicate<String> excludedMethods,
                                       final Collection<String> loggingClasses,
                                       final Collection<String> mutators,
                                       boolean detectInLinedCode) {
        return new DescartesMutationEngine(getMethodFilter(mutateStaticInitializers, excludedMethods),
                getLoggingClassesSet(loggingClasses), getMutationOperators(mutators));
    }

    private static Predicate<Method> getMethodFilter(final boolean mutateStaticInitializers, final Predicate<String> excludedMethods) {
        //Only filters defined by the user. Other filters are applied in code.
        return new Predicate<Method>() {
            public Boolean apply(Method method) {
                String name = method.getName();
                return
                       (name.equals("<clinit>") && !mutateStaticInitializers)
                       || excludedMethods.apply(name);
            }
        };
    }

    private static Set<String> getLoggingClassesSet(Collection<String> loggingClasses) {
        return new HashSet<String>(loggingClasses);
    }

    private static Collection<MutationOperator> getMutationOperators(Collection<String> mutators) {
        LinkedList<MutationOperator> result = new LinkedList<MutationOperator>();
        if(mutators.size() == 0) {
            //Default operators. //TODO: If the list grows put in a separated method
            result.add(VoidMutationOperator.getInstance());
            result.add(NullMutationOperator.getInstance());
            return result;
        }
        OperatorParser parser = new OperatorParser();
        for (String id : mutators) {
                MutationOperator operator = parser.parse(id);
                if(!parser.hasErrors())
                    result.add(operator);
                else
                    org.pitest.util.Log.getLogger().log(Level.SEVERE, "Illegal operator: " + id + ". Details: " + parser.getFirstError());
        }
        return result;
    }

    public String name() {
        return "descartes";
    }

    public String description() {
        return "Engine for extreme mutation operators";
    }
}
