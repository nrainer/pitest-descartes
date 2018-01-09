package fr.inria.stamp.mutationtest.descartes.operators;


import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;
import fr.inria.stamp.mutationtest.test.*;
import org.pitest.reloc.asm.commons.Method;

@RunWith(Parameterized.class)
public class MutationOperatorTest {

    Collection<Method> targets;

    @Before
    public void initialize() {
        targets = TestUtils.getMethods(Calculator.class);
    }

    @Parameter
    public String operatorID;

    @Parameter(1)
    public String expectedMethod;

    @Parameters(name="{index}: Searching methods with operator: {0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList( new Object[][]{
                {"void"       , "clear"},
                {"1"          , "getCeiling"},
                {"(byte)2"    , "getByte"},
                {"(short)3"   , "getShort"},
                {"23456L"     , "getSquare"},
                {"'c'"        , "getRandomOperatorSymbol"},
                {"3.14"       , "add"},
                {"1.2f"       , "getSomething"},
                {"true"       , "isOdd"},
                {"\"string\"" , "getScreen"},
        });
    }

    @Test
    public void shouldFilterMethods() {
        MutationOperator operator = MutationOperator.fromID(operatorID);
        for (Method method: targets) {
            if(operator.canMutate(method))
                assertEquals("Wrong method accepted", expectedMethod, method.getName());
        }
    }



}
