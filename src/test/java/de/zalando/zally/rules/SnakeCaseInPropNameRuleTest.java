package de.zalando.zally.rules;

import de.zalando.zally.Violation;
import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.models.properties.StringProperty;
import org.junit.Test;

import java.util.List;

import static de.zalando.zally.rules.SnakeCaseInPropNameRule.isSnakeCase;
import static org.assertj.core.api.Assertions.assertThat;

public class SnakeCaseInPropNameRuleTest {

    SnakeCaseInPropNameRule rule = new SnakeCaseInPropNameRule();

    Swagger testSwagger = new Swagger();
    ModelImpl testDefinition1 = new ModelImpl();
    ModelImpl testDefinition2 = new ModelImpl();
    StringProperty testPorperty1 = new StringProperty();
    StringProperty testPorperty2 = new StringProperty();

    @Test
    public void isSnakeCaseForEmpty() {
        assertThat(isSnakeCase("")).isFalse();
    }

    @Test
    public void isSnakeCaseForCorrectInput() {
        assertThat(isSnakeCase("customer_number")).isTrue();
    }

    @Test
    public void isSnakeCaseForOnlyLowerCase() {
        assertThat(isSnakeCase("customer")).isTrue();
    }

    @Test
    public void isSnakeCaseForUnderscore() {
        assertThat(isSnakeCase("_")).isFalse();
    }

    @Test
    public void isSnakeCaseForAllLowerCaseAndHyphen() {
        assertThat(isSnakeCase("customer-number")).isFalse();
    }

    @Test
    public void isSnakeCaseForStartWithUnderscore() {
        assertThat(isSnakeCase("_customer_number")).isFalse();
    }

    @Test
    public void isSnakeCaseForUpperCaseAndUnderscore() {
        assertThat(isSnakeCase("CUSTOMER_NUMBER")).isFalse();
    }


    @Test
    public void validateEmptyPath(){
        assertThat(rule.validate(testSwagger)).isEmpty();
    }

    @Test
    public void validateNormalProperty(){
        testDefinition1.addProperty("test_property", testPorperty1);
        testSwagger.addDefinition("ExampleDefintion", testDefinition1);
        assertThat(rule.validate(testSwagger)).isEmpty();
    }

    @Test
    public void validateMultipleNormalProperties(){
        testDefinition1.addProperty("test_property", testPorperty1);
        testDefinition1.addProperty("test_property_two", testPorperty2);
        testSwagger.addDefinition("ExampleDefintion", testDefinition1);
        assertThat(rule.validate(testSwagger)).isEmpty();
    }

    @Test
    public void validateMultipleNormalPropertiesInMultipleDefinitions(){
        testDefinition1.addProperty("test_property", testPorperty1);
        testDefinition2.addProperty("test_property_two", testPorperty2);
        testSwagger.addDefinition("ExampleDefintion", testDefinition1);
        testSwagger.addDefinition("ExampleDefintionTwo", testDefinition2);
        assertThat(rule.validate(testSwagger)).isEmpty();
    }

    @Test
    public void validateFalseProperty(){
        testDefinition1.addProperty("TEST_PROPERTY", testPorperty1);
        testSwagger.addDefinition("ExampleDefintion", testDefinition1);
        List<Violation> result = rule.validate(testSwagger);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getDescription()).contains("\n Violating property: TEST_PROPERTY in definition: ExampleDefintion");
    }

    @Test
    public void validateMultipleFalsePropertiesInMultipleDefinitions(){
        testDefinition1.addProperty("TEST_PROPERTY", testPorperty1);
        testDefinition2.addProperty("test_property_TWO", testPorperty2);
        testSwagger.addDefinition("ExampleDefintion", testDefinition1);
        testSwagger.addDefinition("ExampleDefintionTwo", testDefinition2);
        List<Violation> result = rule.validate(testSwagger);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getDescription()).contains("\n Violating property: TEST_PROPERTY in definition: ExampleDefintion");
        assertThat(result.get(1).getDescription()).contains("\n Violating property: test_property_TWO in definition: ExampleDefintionTwo");
    }
}