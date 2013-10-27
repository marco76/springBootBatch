package ch.javaee.springBootBatch.tokenizer;

import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.file.transform.RangeArrayPropertyEditor;

public class PersonFixedLineTokenizer extends FixedLengthTokenizer {

    public PersonFixedLineTokenizer() {
        RangeArrayPropertyEditor range = new RangeArrayPropertyEditor();

        range.setAsText("1-30,31-60,61-");
        setNames(new String[]{"firstName", "familyName", "year"
        });
        setColumns((Range[]) range.getValue());

    }

}
