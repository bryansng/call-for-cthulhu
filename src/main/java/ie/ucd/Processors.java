package ie.ucd;

import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class Processors {
    CellProcessor[] getProjectProcessors() {
        return new CellProcessor[]{
                new NotNull(), //staffName
                new NotNull(), //researchActicity
                new NotNull() //stream
        };
    }
}
