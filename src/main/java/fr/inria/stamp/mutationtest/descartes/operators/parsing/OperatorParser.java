package fr.inria.stamp.mutationtest.descartes.operators.parsing;

import fr.inria.stamp.mutationtest.descartes.operators.*;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
            errors.add("Unexpected error: " + exc.getMessage());
        }

    }

    private MutationOperator getOperatorFromClassName(String className) {
        try {
            Class<?> clazz = Class.forName(className);

            //Search for a default constructor
            if(!MutationOperator.class.isAssignableFrom(clazz)) {
                error("Class " + className + " does not implement the MutationOperator interface.");
                return null;
            }


            Constructor<?> ctor = null;
            for(Constructor<?> current : clazz.getConstructors()) {
                if (current.getParameterTypes().length == 0)
                    ctor = current;
            }

            //Constructor was found, return the instance
            if(ctor != null)
                return (MutationOperator)ctor.newInstance();


            //Constructor wasn't found, search for a static getInstance method with no parameters
            Method method = clazz.getMethod("getInstance");
            return (MutationOperator) method.invoke(null);

        }
        catch(ClassNotFoundException exc) {
            error("Operator class " + className + " was not found.");
        }
        catch(NoSuchMethodException exc) {
            error("Operator class " + className + " had no default constructor nor a static method getInstance with no parameters.");
        }
        catch(SecurityException exc) {
            error("Security problem detected while instanciating class " + className);
        }
        catch(InstantiationException exc) {
            error("Could not instantiate operator class " + className);
        }
        catch(IllegalAccessException exc) {
            error("Illegal access detected while instanciating operator class " + className);
        }
        catch (InvocationTargetException exc) {
            error("Could not instantiate operator class " + className);
        }
        catch(Exception exc) {
            error("An unexpected error occurred while instanciating operator class " + className);
        }
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
        if(castTo == TokenType.SHORT_KWD)
            data = number.byteValue();
        else
            data = number.shortValue();

        result = new ConstantMutationOperator(input, data);
    }
}
