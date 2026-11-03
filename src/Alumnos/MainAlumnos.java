package Alumnos;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Locale;

public class MainAlumnos {
    public static void main(String[] args) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document alumnos = factory.newDocumentBuilder()
                    .parse(new File("src\\Alumnos\\Ficheros\\alumnos.xml"));
            NodeList listaAlumnos = alumnos.getElementsByTagName("alumno");
            Document resultados = factory.newDocumentBuilder().newDocument();
            Element raiz = resultados.createElement("NotasMedias");
            Element profesor = (Element) alumnos.getElementsByTagName("profesor").item(0);
            String modulo = texto(profesor, "modulo");
            raiz.setAttribute("modulo", modulo);
            resultados.appendChild(raiz);

            double sumaNotas = 0;
            for (int i = 0; i < listaAlumnos.getLength(); i++) {
                Element alumno = (Element) listaAlumnos.item(i);
                String dni = alumno.getAttribute("dni");
                double practicas = (Double.parseDouble(texto(alumno, "practica1"))
                        + Double.parseDouble(texto(alumno, "practica2"))) / 2;
                double notaMedia = practicas * 0.30
                        + Double.parseDouble(texto(alumno, "examen")) * 0.40
                        + Double.parseDouble(texto(alumno, "final")) * 0.30;
                sumaNotas += notaMedia;

                System.out.println(dni + " = " + notaMedia);
                Element resultado = resultados.createElement("Alumno");
                agregarElemento(resultados, resultado, "DNI", dni);
                agregarElemento(resultados, resultado, "NotaMedia",
                        String.valueOf(notaMedia));
                raiz.appendChild(resultado);
            }

            if (listaAlumnos.getLength() == 0) {
                throw new Exception("El XML no contiene alumnos.");
            }

            double mediaTotal = Math.floor(
                    sumaNotas / listaAlumnos.getLength() * 100) / 100;
            agregarElemento(resultados, raiz, "MediaTotal",
                    String.format(Locale.US, "%.2f", mediaTotal));

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(resultados),
                    new StreamResult(new File(
                            "src\\Alumnos\\Ficheros\\notasmedias.xml")));

            System.out.println("Media total del módulo " + modulo + "="
                    + String.format(Locale.US, "%.2f", mediaTotal));
        } catch (Exception e) {
            System.err.println("Error al procesar el XML: " + e.getMessage());
        }
    }

    private static String texto(Element elemento, String etiqueta) {
        return elemento.getElementsByTagName(etiqueta).item(0)
                .getTextContent().trim();
    }

    private static void agregarElemento(
            Document documento, Element padre, String etiqueta, String valor) {
        Element elemento = documento.createElement(etiqueta);
        elemento.setTextContent(valor);
        padre.appendChild(elemento);
    }
}
