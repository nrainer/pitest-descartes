package fr.inria.stamp.mutationtest.descartes.operators.parsing;

import fr.inria.stamp.mutationtest.descartes.operators.*;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class OperatorParser {

    private OperatorLexer lexer;

    private Token lookahead;

    private String input;

    public OperatorParser() {
        errors = new LinkedList<String>();
        reset();
    }

    private void reset() {
        lexer = null;
        result = null;
        input = null;
        errors.clear();
    }

    private MutationOperator result;
    public MutationOperator getResult() {
        return result;
    }

    private final List<String> errors;
    public List<String> getErrors() {
        return errors;
    }

    public String getFirstError() {
        if(hasErrors())
            return errors.get(0);
        return null;
    }

    public boolean hasErrors() {
        return  errors.size() > 0;
    }

    private boolean match(TokenType token) throws IOException{
        if(lookahead.getType() == token) {
            try{
                next();
                return true;
            } catch(IOException exc) {
                return false;
            }
        }
        return false;
    }

    private void next() throws IOException {
        lookahead = lexer.nextToken();
    }

    private boolean lookaheadIs(TokenType token) {
        return lookahead.getType() == token;
    }

    private boolean lookaheadIsOneOf(TokenType... tokens) {
        for(int i=0; i < tokens.length; i++ )
            if(lookaheadIs(tokens[i]))
                return true;
        return false;
    }

    public MutationOperator parse(String input) {
        reset();
        lexer = new OperatorLexer(new StringReader((input)));
        this.input = input;

        parseInput();

        if (hasErrors())
            result = null;

        return result;
    }

    private void parseInput() {
        try {
            next();
            switch (lookahead.getType()) {
                case NULL_KWD:
                    result = NullMutationOperator.getInstance();
                    break;
                case VOID_KWD:
                    result = VoidMutationOperator.getInstance();
                    break;
                case EMPTY_KWD:
                    result = EmptyArrayMutationOperator.getInstance();
                    break;
                case TRUE_KWD:
                case FALSE_KWD:
                case CHAR_LITERAL:
                case INT_LITERAL:
                case STRING_LITERAL:
                case LONG_LITERAL:
                case FLOAT_LITERAL:
                case DOUBLE_LITERAL:
                    result = new ConstantMutationOperator(input, lookahead.getData());
                    break;
                case MINUS:
                    parseNegatedNumber();
                    break;
                case LPAR:
                    parseCastedInteger();
                    break;
                case QUALIFIED_NAME:
                    result = getOperatorFromClassName((String)lookahead.getData());
                    break;
                default:
                    unexpectedTokenError();
                    break;
            }
            if(hasErrors())
                return;

            next();
            if(!match(TokenType.EOF))
                unexpectedTokenError();

        } catch(IOException exc) {
            error("Unexpected error: " + exc.getMessage());
        }

    }

    private MutationOperator getOperatorFromClassName(String className) {

        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            error(e.getMessage());
            return null;
        }

        MutationOperator result = null;
        List<String> operatorErrors = new LinkedList<>();

        if(MutationOperator.class.isAssignableFrom(clazz)) {
            result = getOperatorFromImplementation(clazz, operatorErrors);
        }
        if(result == null) //Could not create the instance or it is not a MutationOperator implementation
        {
            result = getOperatorFromMethod(clazz, operatorErrors);
        }
        if(result == null) //No attempt was successful
        {
            errors.add(operatorErrors.get(0)); //Get the first error
        }
        return result;
    }

    private MutationOperator getOperatorFromImplementation(Class<?> clazz, List<String> errors) {
        try {
            return (MutationOperator) clazz.newInstance();
        }
        catch (Exception e) {
            error(e.getMessage());
            return null;
        }
    }

    private MutationOperator getOperatorFromMethod(Class<?> clazz, List<String> errors) {
        //A class with a single public static method that returns a MutationOperator can be used.
        Method[] methods = Arrays.stream(clazz.getMethods()).filter(
                m -> m.getParameterCount() == 0 &&
                        m.isAccessible() &&
                        Modifier.isStatic(m.getModifiers()) &&
                        clazz.isAssignableFrom(m.getReturnType()) && m.isAccessible()).
                toArray(Method[]::new);


        if(methods.length == 1) //There is only one
        {
            try {
                return (MutationOperator) methods[0].invoke(null);
            }
            catch (Exception e) {
                error("Could not create a MutationOperator instance using " + clazz.getName() + "." + methods[0].getName() + ". Details: " + e.getMessage());
                return null;
            }
        }

        if(methods.length == 0) {
            error("Class " + clazz.getName() + " is not a MutationOperator implementation nor have an accessible static method with no parameters that returns a MutationOperator instance.");
            return null;
        }

        error("Class " + clazz.getName() + " has more than one accessible static method with no parameters that return an instance of MutationOperator.");
        return null;
    }

    private void unexpectedTokenError() {
        error("Unexpected token type: " + lookahead.getType().name());
    }

    private void error(String message) {
        errors.add(message);
    }

    private void parseNegatedNumber() throws IOException {
        if(!match(TokenType.MINUS)) {
            unexpectedTokenError();
            return;
        }
        Object data = null;
        switch (lookahead.getType()){
            case INT_LITERAL:
                data = - (Integer)lookahead.getData();
                break;
            case LONG_LITERAL:
                data = - (Long) lookahead.getData();
                break;
            case FLOAT_LITERAL:
                data = - (Float) lookahead.getData();
                break;
            case DOUBLE_LITERAL:
                data = - (Double) lookahead.getData();
                break;
            default:
                unexpectedTokenError();
                return;
        }
        result = new ConstantMutationOperator(input, data);
    }

    private void parseCastedInteger () throws IOException {
        //Parsing
        if(!match(TokenType.LPAR)) {
            unexpectedTokenError();
            return;
        }
        if(!lookaheadIsOneOf(TokenType.SHORT_KWD, TokenType.BYTE_KWD)) {
            error("Integer casting only supported for short and byte types.");
            return;
        }

        TokenType castTo = lookahead.getType();
        next();

        if(!match(TokenType.RPAR)) {
            unexpectedTokenError();
            return;
        }

        boolean negated = false;

        if(negated = lookaheadIs(TokenType.MINUS)) {
            next();
        }

        if(!lookaheadIs(TokenType.INT_LITERAL)) {
            error("Only integer literals can be casted.");
            return;
        }

        //Evaluation
        Integer number = (Integer)lookahead.getData();
        if(negated)
            number = -number;
        Object data;
        if(castTo == TokenType.BYTE_KWD)
            data = number.byteValue();
        else
            data = number.shortValue();

        result = new ConstantMutationOperator(input, data);
    }
}
