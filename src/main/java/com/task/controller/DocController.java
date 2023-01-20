package com.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.controller.DTO.DocRequestDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;


@RestController
public class DocController {
    private final String Doc_typeHeader = "Тип документа";
    private  final String Participant_innHeader = "ИНН участника оборота товаров";
    private  final String Production_dateHeader = "Дата производства";
    private  final String Producer_innHeader = "ИНН производителя товара";
    private  final String Owner_innHeader = "ИНН собственника товаров";
    private  final String Production_typeHeader = "Тип производственного заказа";
    private  final String Doc_statusHeader = "Заявка на ввод товаров в оборот (собственное производство)";
    private  final String certificate_documentHeader = "КИ";
    private  final String certificate_document_numberHeader = "КИТУ";
    private  final String tnved_codeHeader = "Код товарной номенклатуры (10 знаков)";
    private  final String production_dateHeader = "Дата производства товара";
    private  final String uit_codeHeader = "Документ обязательной сертификации";
    private  final String reg_numberHeader = "Номер документа";
    private  final String reg_dateHeader = "Дата документа";
    private  final String SpaceHeader = "-";
    private final String StartXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<vvod action_id=\"05\" version=\"5\">";
    private final String EndXml = "</vvod>";
    private static final String CSV_FILE = "D:\\LP_INTRODUCE_GOODS_CSV.csv";
    private static final String JSON_FILE = "D:\\LP_INTRODUCE_GOODS_JSON.json";
    private static final String XML_FILE = "D:\\LP_INTRODUCE_GOODS_XML.xml";
    private static final String Sign = "SIGN-GLAFDFASDF34234HFJSKS5REWRYJF445345SDFSDFSDFFGKIGSDYHJWEGR12";

    private final Integer AllRequest = 30;
    private final Integer requestLimit = 50;
    private final Integer TimeLimit = 3;


    @PostMapping("/RequestDocData")
    public String RequestDocData(@RequestBody DocRequestDTO docRequestDTO) throws IOException {
        if (AllRequest>requestLimit){
            try {
                TimeUnit.SECONDS.sleep(TimeLimit);
                printJSON(docRequestDTO,Sign);
                printCSV(docRequestDTO, Sign);
                printXML(docRequestDTO, Sign);
                return "Done, data loaded with delay";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            printJSON(docRequestDTO,Sign);
            printCSV(docRequestDTO, Sign);
            printXML(docRequestDTO, Sign);
            return "Done, data loaded";
        }
    }

    public void printJSON(DocRequestDTO docRequestDTO, String sign) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String JSON = objectMapper.writeValueAsString(docRequestDTO);
        FileWriter nFile = new FileWriter(JSON_FILE);
        nFile.write(JSON + sign);
        nFile.close();
    };

    public void printCSV (DocRequestDTO docRequestDTO, String sign) throws IOException {
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(CSV_FILE));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader(Doc_typeHeader, Participant_innHeader,Production_dateHeader,
                                Producer_innHeader,Owner_innHeader,Production_typeHeader,Doc_statusHeader,"Перечень товаров",
                                certificate_documentHeader,certificate_document_numberHeader,tnved_codeHeader,production_dateHeader,
                                uit_codeHeader,reg_numberHeader,reg_dateHeader,"Sign"));
        ) {
            csvPrinter.printRecord(docRequestDTO.getDoc_type(), docRequestDTO.getParticipant_inn(),
                                    docRequestDTO.getProduction_date(),docRequestDTO.getProducer_inn(),
                                    docRequestDTO.getOwner_inn(),docRequestDTO.getProduction_type(),
                                    docRequestDTO.getDoc_status(),SpaceHeader,docRequestDTO.getProducts().get(0).getCertificate_document(),
                    docRequestDTO.getProducts().get(0).getCertificate_document_number(),
                    docRequestDTO.getProducts().get(0).getTnved_code(),docRequestDTO.getProducts().get(0).getProduction_date(),
                    docRequestDTO.getProducts().get(0).getUit_code(),docRequestDTO.getReg_number(),docRequestDTO.getReg_date(),
                    Sign);
            csvPrinter.flush();
        }
    }

    public void printXML (DocRequestDTO docRequestDTO, String sign) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String JSON = objectMapper.writeValueAsString(docRequestDTO);
        JSONObject JsonForXML = new JSONObject(JSON);
        String xml = XML.toString(JsonForXML);
        String xmlWithPrefix = StartXml + xml + EndXml + sign;
        FileWriter nFile = new FileWriter(XML_FILE);
        nFile.write(xmlWithPrefix);
        nFile.close();
    }
}
