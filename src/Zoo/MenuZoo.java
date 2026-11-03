package Zoo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Scanner;

public class MenuZoo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;
        while (!salir) {
            System.out.println("Menú:");
            System.out.println("1. Crear Zoo.xml");
            System.out.println("2. Mostrar Zoo.xml");
            System.out.println("3. Crear un Zoo2.xml");
            System.out.println("4. Buscar un animal");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine();
            switch (opcion) {
                case 1:
                    crearZooXML();
                    break;
                case 2:
                    MostrarZooXML();
                    break;
                case 3:
                    CrearZoo2XML();
                    break;
                case 4:
                    BuscarAnimal();
                    break;
                case 5:
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }
    public static void crearZooXML() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element Zoo = doc.createElement("Zoo");
            doc.appendChild(Zoo);
            Element Felinos = doc.createElement("Felinos");
            Zoo.appendChild(Felinos);
            Element Felino = doc.createElement("Felino");
            Felinos.appendChild(Felino);
            Element Nombre = doc.createElement("Nombre");
            Felino.appendChild(Nombre);
            Nombre.appendChild(doc.createTextNode("León"));
            Element Origen = doc.createElement("Origen");
            Felino.appendChild(Origen);
            Origen.appendChild(doc.createTextNode("África"));
            Element Peso = doc.createElement("Peso");
            Felino.appendChild(Peso);
            Peso.appendChild(doc.createTextNode("155"));
            Element Felino2 = doc.createElement("Felino");
            Felinos.appendChild(Felino2);
            Element Nombre2 = doc.createElement("Nombre");
            Felino2.appendChild(Nombre2);
            Nombre2.appendChild(doc.createTextNode("Tigre"));
            Element Origen2 = doc.createElement("Origen");
            Felino2.appendChild(Origen2);
            Origen2.appendChild(doc.createTextNode("Asia"));
            Element Peso2 = doc.createElement("Peso");
            Felino2.appendChild(Peso2);
            Peso2.appendChild(doc.createTextNode("215"));
            Element Felino3 = doc.createElement("Felino");
            Felinos.appendChild(Felino3);
            Element Nombre3 = doc.createElement("Nombre");
            Felino3.appendChild(Nombre3);
            Nombre3.appendChild(doc.createTextNode("Lince"));
            Element Origen3 = doc.createElement("Origen");
            Felino3.appendChild(Origen3);
            Origen3.appendChild(doc.createTextNode("Europa"));
            Element Peso3 = doc.createElement("Peso");
            Felino3.appendChild(Peso3);
            Peso3.appendChild(doc.createTextNode("20"));
            Element Felino4 = doc.createElement("Felino");
            Felinos.appendChild(Felino4);
            Element Nombre4 = doc.createElement("Nombre");
            Felino4.appendChild(Nombre4);
            Nombre4.appendChild(doc.createTextNode("Pantera Gris"));
            Element Origen4 = doc.createElement("Origen");
            Felino4.appendChild(Origen4);
            Origen4.appendChild(doc.createTextNode("America"));
            Element Peso4 = doc.createElement("Peso");
            Felino4.appendChild(Peso4);
            Peso4.appendChild(doc.createTextNode("95"));

            Transformer transformer =
                    TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc),
                    new StreamResult("src/Zoo/FicheroZoo/Zoo.xml"));
            System.out.println("Archivo Zoo.xml creado exitosamente.");



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void MostrarZooXML() {
        System.out.println("Mostrando contenido del archivo Zoo.xml:");
        try {
            // Crea el objeto de configuración del analizador DOM
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            // Ignora comentarios del XML
            factory.setIgnoringComments(true);
            // Ignora espacios en blanco entre etiquetas
            factory.setIgnoringElementContentWhitespace(true);
            // Crea el analizador DOM
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Analiza el documento XML y genera el árbol DOM
            Document documento = builder.parse(new File("src/Zoo/FicheroZoo/Zoo.xml"));
            NodeList felinos = documento.getElementsByTagName("Felino");
            for (int i = 0; i < felinos.getLength(); i++) {
                Element felino = (Element) felinos.item(i);
                System.out.println("Nombre: " + felino.getElementsByTagName("Nombre").item(0).getTextContent());
                System.out.println("Origen: " + felino.getElementsByTagName("Origen").item(0).getTextContent());
                System.out.println("Peso: " + felino.getElementsByTagName("Peso").item(0).getTextContent());
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void CrearZoo2XML() {
            try {

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document documento = builder.parse(new File("src/Zoo/FicheroZoo/Zoo.xml"));


                NodeList felinos = documento.getElementsByTagName("Felino");

                for (int i = 0; i < felinos.getLength(); i++) {
                    Element felino = (Element) felinos.item(i);

                    Element pesoElement = (Element) felino.getElementsByTagName("Peso").item(0);

                    int pesoActual = Integer.parseInt(pesoElement.getTextContent().trim());

                    int pesoNuevo = pesoActual - 5;

                    pesoElement.setTextContent(String.valueOf(pesoNuevo));
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

                transformer.transform(
                        new DOMSource(documento),
                        new StreamResult(new File("src/Zoo/FicheroZoo/Zoo2.xml"))
                );

                // 8. Mostrar por pantalla el contenido del archivo nuevo
                System.out.println("Contenido de Zoo2.xml:");
                System.out.println("----------------------");
                for (int i = 0; i < felinos.getLength(); i++) {
                    Element felino = (Element) felinos.item(i);
                    System.out.println("Nombre: " + felino.getElementsByTagName("Nombre").item(0).getTextContent());
                    System.out.println("Origen: " + felino.getElementsByTagName("Origen").item(0).getTextContent());
                    System.out.println("Peso: " + felino.getElementsByTagName("Peso").item(0).getTextContent() + " kg");
                    System.out.println();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public static void BuscarAnimal() {
        System.out.println("Buscando un animal en Zoo.xml:");
        System.out.println("1. Buscar por nombre");
        System.out.println("2. Buscar por origen");
        System.out.println("3. Buscar por peso");
        System.out.print("Seleccione una opción: ");
        Scanner scanner = new Scanner(System.in);
        int opcion = scanner.nextInt();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(new File("src/Zoo/FicheroZoo/Zoo.xml"));

            NodeList felinos = documento.getElementsByTagName("Felino");

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el nombre del animal: ");
                    String nombre = scanner.next();
                    for (int i = 0; i < felinos.getLength(); i++) {
                        Element felino = (Element) felinos.item(i);
                        String nombreActual = felino.getElementsByTagName("Nombre").item(0).getTextContent();
                        if (nombreActual.equals(nombre)) {
                            System.out.println("Nombre: " + nombreActual);
                            System.out.println("Origen: " + felino.getElementsByTagName("Origen").item(0).getTextContent());
                            System.out.println("Peso: " + felino.getElementsByTagName("Peso").item(0).getTextContent());
                        }
                    }
                    break;

                case 2:
                    System.out.print("Ingrese el origen del animal: ");
                    String origen = scanner.next();
                    for (int i = 0; i < felinos.getLength(); i++) {
                        Element felino = (Element) felinos.item(i);
                        String origenActual = felino.getElementsByTagName("Origen").item(0).getTextContent();
                        if (origenActual.equals(origen)) {
                            System.out.println("Nombre: " + felino.getElementsByTagName("Nombre").item(0).getTextContent());
                            System.out.println("Origen: " + origenActual);
                            System.out.println("Peso: " + felino.getElementsByTagName("Peso").item(0).getTextContent());
                        }
                    }
                    break;

                case 3:
                    System.out.print("Ingrese el peso del animal: ");
                    String peso = scanner.next();
                    System.out.println("1. Mayor que " + peso);
                    System.out.println("2. Menor que " + peso);
                    System.out.print("Seleccione una opción: ");
                    int subOpcion = scanner.nextInt();
                    for (int i = 0; i < felinos.getLength(); i++) {
                        Element felino = (Element) felinos.item(i);
                        String pesoActual = felino.getElementsByTagName("Peso").item(0).getTextContent();
                        if (subOpcion == 1 && Integer.parseInt(pesoActual) >= Integer.parseInt(peso)) {
                            System.out.println("Nombre: " + felino.getElementsByTagName("Nombre").item(0).getTextContent());
                            System.out.println("Origen: " + felino.getElementsByTagName("Origen").item(0).getTextContent());
                            System.out.println("Peso: " + pesoActual);
                        } else if (subOpcion == 2 && Integer.parseInt(pesoActual) <= Integer.parseInt(peso)) {
                            System.out.println("Nombre: " + felino.getElementsByTagName("Nombre").item(0).getTextContent());
                            System.out.println("Origen: " + felino.getElementsByTagName("Origen").item(0).getTextContent());
                            System.out.println("Peso: " + pesoActual);
                        }
                    }

                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
