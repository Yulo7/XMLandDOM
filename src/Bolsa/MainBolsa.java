package Bolsa;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class MainBolsa {
    private static final File CARPETA = new File("src\\Bolsa\\Ficheros");
    private static final File FICHERO_BOLSA = new File(CARPETA, "Bolsa.xml");
    private static final File FICHERO_DATOS = new File(CARPETA, "datos.xml");
    private static final File FICHERO_ACTUALIZADO = new File(CARPETA, "bolsaactualizada.xml");

    public static void main(String[] args) {
        try {
            Document bolsa = parsear(FICHERO_BOLSA);
            Document operaciones = parsear(FICHERO_DATOS);
            NodeList listaOperaciones = operaciones.getElementsByTagName("Operacion");

            for (int i = 0; i < listaOperaciones.getLength(); i++) {
                Element operacion = (Element) listaOperaciones.item(i);
                try {
                    aplicarOperacion(bolsa, operacion);
                } catch (IllegalArgumentException e) {
                    String id = operacion.getAttribute("id");
                    System.out.println("Error en la operación " + (i + 1)
                            + " (" + id + "): " + e.getMessage());
                }
            }

            guardar(bolsa, FICHERO_ACTUALIZADO);
            System.out.println("Fichero actualizado creado en: "
                    + FICHERO_ACTUALIZADO.getPath());
        } catch (Exception e) {
            System.err.println("No se pudo procesar la bolsa: " + e.getMessage());
        }
    }

    private static Document parsear(File fichero) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(fichero);
    }

    private static void aplicarOperacion(Document bolsa, Element operacion) {
        String tipo = operacion.getAttribute("id").trim().toUpperCase();
        String nombre = obtenerTexto(operacion, "Nombre");
        Element empresa = buscarEmpresa(bolsa, nombre);

        switch (tipo) {
            case "A":
                if (empresa != null) {
                    throw new IllegalArgumentException(
                            "la empresa '" + nombre + "' ya existe.");
                }

                String indice = obtenerTexto(operacion, "Indice");
                String simbolo = obtenerTexto(operacion, "Simbolo");
                String precioAlta = obtenerTexto(operacion, "Precio");
                validarPrecio(precioAlta);

                Element nuevaEmpresa = bolsa.createElement("Empresa");
                nuevaEmpresa.setAttribute("indice", indice);
                anadirElemento(bolsa, nuevaEmpresa, "Nombre", nombre);
                anadirElemento(bolsa, nuevaEmpresa, "Simbolo", simbolo);
                anadirElemento(bolsa, nuevaEmpresa, "Precio", precioAlta);
                bolsa.getDocumentElement().appendChild(nuevaEmpresa);
                System.out.println("Alta realizada: " + nombre);
                break;

            case "B":
                if (empresa == null) {
                    throw new IllegalArgumentException(
                            "no existe la empresa '" + nombre + "'.");
                }
                empresa.getParentNode().removeChild(empresa);
                System.out.println("Baja realizada: " + nombre);
                break;

            case "M":
                if (empresa == null) {
                    throw new IllegalArgumentException(
                            "no existe la empresa '" + nombre + "'.");
                }
                String precioNuevo = obtenerTexto(operacion, "Precio");
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
            NodeList nombres = empresa.getElementsByTagName("Nombre");
            if (nombres.getLength() > 0
                    && nombres.item(0).getTextContent().trim().equalsIgnoreCase(nombre)) {
                return empresa;
            }
        }
        return null;
    }

    private static String obtenerTexto(Element elemento, String etiqueta) {
        NodeList elementos = elemento.getElementsByTagName(etiqueta);
        if (elementos.getLength() == 0) {
            throw new IllegalArgumentException(
                    "falta el dato obligatorio '" + etiqueta + "'.");
        }
        String valor = elementos.item(0).getTextContent().trim();
        if (valor.isEmpty()) {
            throw new IllegalArgumentException(
                    "el dato '" + etiqueta + "' no puede estar vacío.");
        }
        return valor;
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

    private static void anadirElemento(
            Document documento, Element padre, String etiqueta, String valor) {
        Element elemento = documento.createElement(etiqueta);
        elemento.setTextContent(valor);
        padre.appendChild(elemento);
    }

    private static void guardar(Document documento, File fichero)
            throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(new DOMSource(documento), new StreamResult(fichero));
    }
}
