package Bolsa;

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

public class MainBolsa {
    private static final String CARPETA = "src\\Bolsa\\Ficheros\\";

    public static void main(String[] args) {
        try {
            Document bolsa = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(new File(CARPETA + "Bolsa.xml"));
            Document datos = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(new File(CARPETA + "datos.xml"));
            NodeList operaciones = datos.getElementsByTagName("Operacion");

            for (int i = 0; i < operaciones.getLength(); i++) {
                try {
                    aplicarOperacion(bolsa, (Element) operaciones.item(i));
                } catch (IllegalArgumentException e) {
                    Element operacion = (Element) operaciones.item(i);
                    System.out.println("Error en la operación " + (i + 1)
                            + " (" + operacion.getAttribute("id") + "): "
                            + e.getMessage());
                }
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(bolsa),
                    new StreamResult(new File(CARPETA + "bolsaactualizada.xml")));
            System.out.println("Fichero actualizado creado en: "
                    + CARPETA + "bolsaactualizada.xml");
        } catch (Exception e) {
            System.err.println("No se pudo procesar la bolsa: " + e.getMessage());
        }
    }

    private static void aplicarOperacion(Document bolsa, Element operacion) {
        String tipo = operacion.getAttribute("id").trim().toUpperCase();
        String nombre = texto(operacion, "Nombre");
        Element empresa = buscarEmpresa(bolsa, nombre);

        switch (tipo) {
            case "A":
                if (empresa != null) {
                    throw new IllegalArgumentException(
                            "la empresa '" + nombre + "' ya existe.");
                }
                Element nueva = bolsa.createElement("Empresa");
                nueva.setAttribute("indice", texto(operacion, "Indice"));
                agregar(bolsa, nueva, "Nombre", nombre);
                agregar(bolsa, nueva, "Simbolo", texto(operacion, "Simbolo"));
                String precioAlta = texto(operacion, "Precio");
                validarPrecio(precioAlta);
                agregar(bolsa, nueva, "Precio", precioAlta);
                bolsa.getDocumentElement().appendChild(nueva);
                System.out.println("Alta realizada: " + nombre);
                break;
            case "B":
                if (empresa == null) {
                    throw new IllegalArgumentException(
                            "no existe la empresa '" + nombre + "'.");
                }
                bolsa.getDocumentElement().removeChild(empresa);
                System.out.println("Baja realizada: " + nombre);
                break;
            case "M":
                if (empresa == null) {
                    throw new IllegalArgumentException(
                            "no existe la empresa '" + nombre + "'.");
                }
                String precioNuevo = texto(operacion, "Precio");
                validarPrecio(precioNuevo);
                empresa.getElementsByTagName("Precio").item(0)
                        .setTextContent(precioNuevo);
                System.out.println("Precio actualizado para " + nombre
                        + ": " + precioNuevo);
                break;
            default:
                throw new IllegalArgumentException(
                        "operación desconocida '" + tipo + "'.");
        }
    }

    private static Element buscarEmpresa(Document bolsa, String nombre) {
        NodeList empresas = bolsa.getElementsByTagName("Empresa");
        for (int i = 0; i < empresas.getLength(); i++) {
            Element empresa = (Element) empresas.item(i);
            if (texto(empresa, "Nombre").equalsIgnoreCase(nombre)) {
                return empresa;
            }
        }
        return null;
    }

    private static String texto(Element elemento, String etiqueta) {
        NodeList elementos = elemento.getElementsByTagName(etiqueta);
        if (elementos.getLength() == 0
                || elementos.item(0).getTextContent().trim().isEmpty()) {
            throw new IllegalArgumentException("falta el dato '" + etiqueta + "'.");
        }
        return elementos.item(0).getTextContent().trim();
    }

    private static void validarPrecio(String texto) {
        try {
            double precio = Double.parseDouble(texto);
            if (Double.isNaN(precio) || Double.isInfinite(precio)) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "el precio '" + texto + "' no es un número válido.");
        }
    }

    private static void agregar(
            Document documento, Element padre, String etiqueta, String valor) {
        Element elemento = documento.createElement(etiqueta);
        elemento.setTextContent(valor);
        padre.appendChild(elemento);
    }
}
