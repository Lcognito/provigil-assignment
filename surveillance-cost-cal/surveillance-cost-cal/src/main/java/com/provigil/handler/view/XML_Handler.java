/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.provigil.handler.view;

import com.provigil.controller.SurveillanceCalculator;
import com.provigil.models.Result;
import com.provigil.models.Subscription;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Kalyan
 */
public class XML_Handler {

    private static final SurveillanceCalculator calc = new SurveillanceCalculator();
    private static String inputFilename;
    private static String inputFilePath;

    private boolean validateInput(String inputPath) {
        if (!inputPath.contains(System.getProperty("file.separator"))) {
            throw new IllegalArgumentException("Invalid path");
        }

        File inputXMLFile = new File(inputPath);
        if (inputXMLFile.isDirectory())
            throw new IllegalArgumentException("Path provided is a directory and not a file");

        if (!inputXMLFile.exists())
            throw new IllegalArgumentException("File doesn't exist");

        if (!validateXML(inputXMLFile))
            throw new IllegalArgumentException("Invalid XML document");

        return true;
    }

    private boolean validateXML(File ipXML) {
        try {
            SchemaFactory factory
                    = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(System.getProperty("user.dir") + "\\subscriptions.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(ipXML));
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        } catch (SAXException e1) {
            System.out.println("SAX Exception: " + e1.getMessage());
            return false;
        }

        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        XML_Handler handler = new XML_Handler();

        Scanner scanr = new Scanner(System.in);

        System.out.println("Please provide the path to the input XML file");

        StringBuffer buffer = new StringBuffer();
        do {
            buffer.append(scanr.nextLine());
        }
        while(buffer.length() == 0);

        if (handler.validateInput(buffer.toString())) {

            ArrayList<Subscription> sublist = handler.parseDocument(buffer.toString());

            if (sublist.isEmpty())
                throw new IllegalArgumentException("Invalid subscription plan data");

            ArrayList<Result> resultsList = calc.calculateSubscription(sublist);
            File inputFile = new File(buffer.toString());
            inputFilename = inputFile.getName();
            inputFilename = inputFilename.substring(0, inputFilename.lastIndexOf("."));
            inputFilePath = inputFile.getParent();
            handler.writeToXML(resultsList);
        }
    }



    public ArrayList<Subscription> parseDocument(String path) {
        ArrayList<Subscription> subscriptionsList = new ArrayList();

        try {

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader eventReader =
                    factory.createXMLEventReader(new FileReader(path));

            boolean id = false, plan = false, area = false, location = false;
            ArrayList<String> listOfSubValues = new ArrayList();

            while(eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                switch(event.getEventType()) {

                   case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = event.asStartElement();
                        String qName = startElement.getName().getLocalPart();

                        if (qName.equalsIgnoreCase("id")) {
                            id = true;
                        } else if (qName.equalsIgnoreCase("area")) {
                           area = true;
                        } else if (qName.equalsIgnoreCase("plan")) {
                           plan = true;
                        }
                        else if (qName.equalsIgnoreCase("location")) {
                           location = true;
                        }
                    break;

                   case XMLStreamConstants.CHARACTERS:

                        Characters characters = event.asCharacters();

                        if(id) {
                            listOfSubValues.add(0, characters.getData().trim());
                            id = false;
                        }
                        if(area) {
                            listOfSubValues.add(1, characters.getData().trim());
                            area = false;
                        }
                        if(plan) {
                            listOfSubValues.add(2, characters.getData().trim());
                            plan = false;
                        }
                        if(location) {
                            listOfSubValues.add(3, characters.getData().trim());
                            location = false;
                        }
                    break;

                }

                if (listOfSubValues.size() == 4) {
                    Subscription newSub = new Subscription();
                    newSub.setId(listOfSubValues.get(0));
                    newSub.setArea(listOfSubValues.get(1));
                    newSub.setPlan(listOfSubValues.get(2));
                    newSub.setLocation(listOfSubValues.get(3));
                    subscriptionsList.add(newSub);
                    listOfSubValues.clear();
                }

            }   // end of while
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XML_Handler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(XML_Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return subscriptionsList;
    }

    public void writeToXML(ArrayList<Result> results) {
        try {
            DocumentBuilderFactory dbFactory =
            DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("result");
            doc.appendChild(rootElement);

            for (Result res : results) {
                Element subsElement = doc.createElement("subscription");
                rootElement.appendChild(subsElement);

                Element idElement = doc.createElement("id");
                idElement.appendChild(doc.createTextNode(res.getId()));
                subsElement.appendChild(idElement);

                Element costElement = doc.createElement("cost");
                costElement.appendChild(doc.createTextNode(res.getCost()));
                subsElement.appendChild(costElement);
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(inputFilePath + "\\" + inputFilename + "_result.xml"));
            transformer.transform(source, result);
            System.out.println("Result file written to: " + " " + inputFilePath + "\\" + inputFilename + "_result.xml");

        } catch (Exception e) {
            e.printStackTrace();
         }
    }

}
