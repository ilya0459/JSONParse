import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileName = "data.csv";
        String fileXml = "data.xml";

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);
        String fileNameJson = "data.json";

        writeString(json, fileNameJson);

        List<Employee> list2 = parseXML(fileXml);
        json = listToJson(list2);
        fileNameJson = "data2.json";
        writeString(json, fileNameJson);

    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return staff;
    }


    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);

        return json;
    }


    public static void writeString(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileXml) {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(fileXml));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        List<Employee> employeeList = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!nodeList.item(i).getNodeName().equals("employee")) {
                continue;
            }
            long id = 0;
            String firstName = "";
            String lastName = "";
            String country = "";
            int age = 0;

            NodeList employeeChilds = nodeList.item(i).getChildNodes();
            for (int j = 0; j < employeeChilds.getLength(); j++) {
                if (employeeChilds.item(j).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                switch (employeeChilds.item(j).getNodeName()) {
                    case "id": {
                        id = Long.valueOf(employeeChilds.item(j).getTextContent());
                        break;
                    }
                    case "firstName": {
                        firstName = employeeChilds.item(j).getTextContent();
                        break;
                    }
                    case "lastName": {
                        lastName = employeeChilds.item(j).getTextContent();
                        break;
                    }
                    case "country": {
                        country = employeeChilds.item(j).getTextContent();
                        break;
                    }
                    case "age": {
                        age = Integer.valueOf(employeeChilds.item(j).getTextContent());
                        break;
                    }
                }
            }
            Employee employee = new Employee(id, firstName, lastName, country, age);
            employeeList.add(employee);
        }
        return employeeList;
    }

}