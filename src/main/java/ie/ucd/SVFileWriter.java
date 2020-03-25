package ie.ucd;

import ie.ucd.objects.Project;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SVFileWriter {

    public void writeProjects(ArrayList<Project> projects, int numOfStudents) throws IOException, InvalidFormatException {
        final String[] columns = {"Staff Name", "Research Activity", "Stream"};
        ICsvBeanWriter beanWriter = null;
        try {
            beanWriter = new CsvBeanWriter(new FileWriter("ankish/Projects" + numOfStudents + ".csv"), CsvPreference.STANDARD_PREFERENCE);
            final String[] header = new String[]{"proposedBy", "researchActivity", "stream"};
            Processors processorGetter = new Processors();
            final CellProcessor[] processors = processorGetter.getProjectProcessors();
            beanWriter.writeHeader(columns);

            for (Project project : projects) {
                beanWriter.write(project, header, processors);
            }
        } catch (Exception e) {
            System.out.println("error writing file!");
        } finally {
            if (beanWriter != null) beanWriter.close();
        }
    }
}
