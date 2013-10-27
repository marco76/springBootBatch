package ch.javaee.springBootBatch.tokenizer;

import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.file.transform.RangeArrayPropertyEditor;

public class PersonFixedLengthTokenizer extends FixedLengthTokenizer {

    public PersonFixedLengthTokenizer() {
        RangeArrayPropertyEditor range = new RangeArrayPropertyEditor();
        // we defines how to split the text line, from column 1 to 30 assign the content to 'firstName'
        range.setAsText("1-30,31-60,61-");
        // names have to be the same as the properties in the model class
        setNames(new String[]{"firstName", "familyName", "year"
        });
        setColumns((Range[]) range.getValue());

    }

}
