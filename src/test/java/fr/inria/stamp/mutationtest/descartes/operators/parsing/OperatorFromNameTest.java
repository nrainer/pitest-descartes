package fr.inria.stamp.mutationtest.descartes.operators.parsing;

import fr.inria.stamp.mutationtest.descartes.operators.ConstantMutationOperator;
import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;
import fr.inria.stamp.mutationtest.descartes.operators.VoidMutationOperator;
import fr.inria.stamp.mutationtest.descartes.operators.WrongOperatorException;
import fr.inria.stamp.mutationtest.test.fakeoperators.*;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OperatorFromNameTest {

    @Test
    public void shouldCreateOperator() {

        Class<?>[] classes = {
                RegularOperator.class,
                OperatorWithStaticMethod.class,
                ContainerClass.PublicInnerStatic.class
        };

        for(Class<?> clazz : classes) {
            assertThat("Failed to create an instance of: " + clazz.getName(),
                    MutationOperator.fromID(clazz.getName()), instanceOf(clazz));
        }
    }

    @Test
    public void shouldNotCreateOperator() {

        Class<?>[] classes = {
                OperatorWithNoDefaultCtor.class,
                OperatorMultipleStatic.class,
                ContainerClass.class,
                ContainerClass.InnerNonStatic.class
        };

        for(Class<?> clazz : classes) {
            try {
                MutationOperator.fromID(clazz.getName());
                fail("Instance of " + clazz.getName() + " was created.");
            }
            catch (WrongOperatorException exc) {}
        }
    }

    @Test
    public void shouldCreateSingleton() {
        MutationOperator operator = MutationOperator.fromID("fr.inria.stamp.mutationtest.descartes.operators.VoidMutationOperator");
        assertEquals(VoidMutationOperator.getInstance(), operator);
    }

    @Test(expected = WrongOperatorException.class)
    public void shouldNotCreateNonAccessibleInnerClasses() {
        MutationOperator.fromID("fr.inria.stamp.mutationtest.test.fakeoperators.ContainerClass$NonPublicInnerStatic");
    }

    @Test
    public void shouldCreateOperatorFromClassWithStaticMethod() {
        MutationOperator operator = MutationOperator.fromID(NoOperatorWithStatic.class.getName());
        assertThat(operator, instanceOf(ConstantMutationOperator.class));
    }

    @Test(expected = WrongOperatorException.class)
    public void shouldNotCreatWithANonExistingClass() {
        MutationOperator.fromID("this.class.does.not.Exist");
    }





}
