package ch.javaee.springBootBatch.processor;

import ch.javaee.springBootBatch.model.Person;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    @Override
    public Person process(final Person person) throws Exception {

        return person;
    }
}