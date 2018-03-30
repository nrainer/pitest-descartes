package fr.inria.stamp.mutationtest.descartes.operators.parsing;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

import fr.inria.stamp.mutationtest.descartes.operators.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class OperatorParserTest {

    //TODO: Add more test cases
    //TODO: Add a test class with failing test cases
    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"void", VoidMutationOperator.getInstance()},
                {"null", NullMutationOperator.getInstance()},
                {"empty", EmptyArrayMutationOperator.getInstance()},
                {"3", new ConstantMutationOperator("3", 3)},
                {"3L", new ConstantMutationOperator("3L", 3L)},
                {"(byte)3", new ConstantMutationOperator("(byte)3", (byte)3)},
                {"(short)3", new ConstantMutationOperator("(short)3", (short)3)},
                {"3.0", new ConstantMutationOperator("3.0",  3.0)},
                {"3.0f", new ConstantMutationOperator("3.0f", 3.0f)},
                {"true", new ConstantMutationOperator("true", true)},
                {"false", new ConstantMutationOperator("false", false)},
                {"'a'", new ConstantMutationOperator("'a'", 'a')},
                {"\"string\"", new ConstantMutationOperator("string", "string")},
                {"-4", new ConstantMutationOperator("-4", -4)},
                {"-4L", new ConstantMutationOperator("-4L", -4L)},
                {"(byte)-4", new ConstantMutationOperator("(byte)-4", (byte)-4)},
                {"(short)-5", new ConstantMutationOperator("(short)-4", (short)-5)},
                {"-56.0", new ConstantMutationOperator("-56.0", -56.0)},
                {"-0.3f", new ConstantMutationOperator("-0.3f", -0.3f)}
        });
    }

    @Parameter
    public String input;

    @Parameter(1)
    public Object output;

    private OperatorParser parser = new OperatorParser();

    @Test
    public void shouldParseValue() {
        assertFalse("Errors found while parsing: " + input, parser.hasErrors());
        assertEquals( output, parser.parse(input));
    }

}