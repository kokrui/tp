package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Balance;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;

public class FindExpressionParserTest {

    @Test
    public void fromPrefix_invalidPrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> FindExpressionParser.FindSupportedField.createFromPrefix(" "));
        assertThrows(ParseException.class, () -> FindExpressionParser.FindSupportedField.createFromPrefix("asdf"));

        // correct prefix syntax but invalid prefix
        assertThrows(ParseException.class, () -> FindExpressionParser.FindSupportedField.createFromPrefix("tgg/"));
        assertThrows(ParseException.class, () -> FindExpressionParser.FindSupportedField.createFromPrefix("e2a/"));
        assertThrows(ParseException.class, () -> FindExpressionParser.FindSupportedField.createFromPrefix("lin/"));
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertThrows(ParseException.class, () -> FindExpressionParser.parseToPredicate(new ArrayList<>()));
    }

    @Test
    public void conditionNodeToPredicate_validField_returnsPredicate() throws ParseException {
        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.NAME, "Alice").toPredicate()
                instanceof Predicate);

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.PHONE, "12345").toPredicate()
                instanceof Predicate);

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.EMAIL, "haha@haha.com").toPredicate()
                instanceof Predicate);

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.ADDRESS, "123, haha street").toPredicate()
                instanceof Predicate);

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.TAG, "friends").toPredicate()
                instanceof Predicate);

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.TELEGRAM, "haha").toPredicate()
                instanceof Predicate);

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.SECONDARY_EMAIL, "second@haha.com").toPredicate()
                instanceof Predicate);

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.LINKEDIN, "elonmusk").toPredicate()
                instanceof Predicate);

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BIRTHDAY, "12").toPredicate()
                instanceof Predicate);

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.NOTE, "cool").toPredicate()
                instanceof Predicate);

    }

    @Test
    public void conditionNodeToPredicate_invalidField_throwsException() {
        assertThrows(NullPointerException.class, () -> new FindExpressionParser.ConditionNode(
                null, "Alice").toPredicate());
    }

    @Test
    public void conditionNodeToPredicate_validOptionalField_evaluatesFalseIfEmpty() throws ParseException {
        Person person = new Person(
                new Name("Alice"),
                new Phone("12345"),
                new Email("alice@a.com"),
                new Address("123, alice street"),
                Set.of(new Tag("friends"))
        );

        assertFalse(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BIRTHDAY, "Dec").toPredicate().test(person));

        assertFalse(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.TELEGRAM, "haha").toPredicate().test(person));

        assertFalse(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.SECONDARY_EMAIL, "a@a.com").toPredicate().test(person));

        assertFalse(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.LINKEDIN, "elonmusk").toPredicate().test(person));

        assertFalse(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.NOTE, "cool").toPredicate().test(person));
    }

    @Test
    public void conditionNodeToPredicate_validOptionalField_evaluatesTrueIfNotEmpty() throws ParseException {
        Person person = new Person(
                new Name("Alice"),
                new Phone("12345"),
                new Email("alice@a.com"),
                new Address("123, alice street"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new Telegram("@aliceeee")),
                Set.of(new Tag("friends")),
                Optional.of(999),
                FXCollections.observableArrayList(new ArrayList<>()),
                new Balance(0)
        );

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.TELEGRAM, "@aliceeee")
                .toPredicate().test(person));
    }

    @Test
    public void conditionNodeToPredicate_validBalanceField_evaluatesCorrectly() throws ParseException {
        Person youOwe = new Person(
                new Name("Alice"),
                new Phone("12345"),
                new Email("alice@a.com"),
                new Address("123, alice street"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new Telegram("@aliceeee")),
                Set.of(new Tag("friends")),
                Optional.of(999),
                FXCollections.observableArrayList(new ArrayList<>()),
                new Balance(-500)
        );

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BALANCE, "-1.12")
                .toPredicate().test(youOwe));

        // equal and with dollar sign
        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BALANCE, "-$5.0")
                .toPredicate().test(youOwe));

        Person owesYou = new Person(
                new Name("Alice"),
                new Phone("12345"),
                new Email("alice@a.com"),
                new Address("123, alice street"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new Telegram("@aliceeee")),
                Set.of(new Tag("friends")),
                Optional.of(999),
                FXCollections.observableArrayList(new ArrayList<>()),
                new Balance(50000)
        );

        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BALANCE, "5.2")
                .toPredicate().test(owesYou));

        // equal and with dollar sign
        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BALANCE, "$50.00")
                .toPredicate().test(owesYou));
    }

    @Test
    public void conditionNodeToPredicate_invalidBalanceField_evaluatesCorrectly() {
        Person youOwe = new Person(
                new Name("Alice"),
                new Phone("12345"),
                new Email("alice@a.com"),
                new Address("123, alice street"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new Telegram("@aliceeee")),
                Set.of(new Tag("friends")),
                Optional.of(999),
                FXCollections.observableArrayList(new ArrayList<>()),
                new Balance(-500)
        );

        assertThrows(ParseException.class, () -> new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BALANCE, "1.123")
                .toPredicate().test(youOwe));

        assertThrows(ParseException.class, () -> new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BALANCE, "1.1.1")
                .toPredicate().test(youOwe));

        assertThrows(ParseException.class, () -> new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BALANCE, "$-5")
                .toPredicate().test(youOwe));

        assertThrows(ParseException.class, () -> new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BALANCE, "$ABCD")
                .toPredicate().test(youOwe));

        assertThrows(ParseException.class, () -> new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.BALANCE, "$$5")
                .toPredicate().test(youOwe));
    }

    @Test
    public void conditionNoteToPredicate_validConditionWithQuotedKeyword_returnsPredicate()
            throws ParseException {
        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.NAME, "\"Alice Bob\"").toPredicate()
                instanceof Predicate);


        assertTrue(new FindExpressionParser.ConditionNode(
                FindExpressionParser.FindSupportedField.ADDRESS, "\"alice park\"").toPredicate()
                instanceof Predicate);
    }

    @Test
    public void conditionNodeToPredicate_invalidConditionWithQuotedEmptyKeyword_throwsParseException() {
        assertThrows(ParseException.class, () -> FindExpressionParser.parseToPredicate(
                List.of(new FindFilterStringTokenizer.Token(
                        FindFilterStringTokenizer.Token.Type.CONDITION, "n/\"\""))
                )
        );
    }
}
