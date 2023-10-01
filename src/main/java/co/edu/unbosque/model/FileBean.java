package co.edu.unbosque.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.file.UploadedFile;

/**
 * Esta es la clase backend del proyecto, contiene la implementación del
 * algoritmo KMP, realiza la lectura del archivo y resalta las palabras
 * coincidentes.
 * 
 * @author RootSoftworks
 *
 */
@ManagedBean
@SessionScoped
public class FileBean {

	// Este viene desde el frontend, se pierde cuando se realiza una búsqueda
	private UploadedFile file;

	// Copia para mantener el archivo vivo
	private UploadedFile fileBackup;

	// Contenido del archivo
	private String content;

	// Copia del contenido del archivo, para no dañar la original y poder luego
	// volver a mostrar el original
	private String contentCopy;

	// Este viene del frontend, es la palabra que el usuario desea buscar
	private String textToSearch;

	// El número de coincidencias encontradas
	private int cantCoincidencias;

	// Este texto es el que se usa para agregar las etiquetas <span> y resaltar las
	// coincidencias, luego se re-arma el String en la variable content
	private String highlightedContent;

	// Este viene del frontend, es el checkbox para saber si el usuario desea o no
	// tener en cuenta las mayúsculas
	private Boolean useCapitals;

	/**
	 * Este es el método inicial de una búsqueda, se ejecuta cuando se presiona el
	 * botón "buscar", revisa primero que el archivo si esté cargado y que el campo
	 * de texto de la palabra a buscar no esté vacío
	 */
	public void search1() {

		if (!textToSearch.isEmpty() && content != null && textToSearch != null) {
			highlightedContent = ""; // Reinicia highlightedContent

			// Se hace una copia del texto original
			content = new String(contentCopy);

			// System.out.println(textToSearch);

			this.content = contentCopy;
			cantCoincidencias = 0;
			// cantCoincidencias = doTheSearch();

			// Aplicar el algoritmo KMP para buscar y resaltar coincidencias
			// System.out.println("A buscar...");
			if (useCapitals == true) {

				// Ejecución del algoritmo KMP
				List<Integer> matchPositions = kmpSearchAll(content, textToSearch);
				cantCoincidencias = matchPositions.size();
				// Resaltar coincidencias en base a los resultados del algoritmo
				String highlightedContent = highlightMatchesWithHtml(content, matchPositions);

				// Mostrar lo que se resaltó
				this.content = highlightedContent;
			} else {

				// Ejecución del algoritmo KMP
				List<Integer> matchPositions = kmpSearchAll(content.toLowerCase(), textToSearch.toLowerCase());
				cantCoincidencias = matchPositions.size();
				// Resaltar coincidencias
				String highlightedContent = highlightMatchesWithHtml(content, matchPositions);

				// Mostrar el texto resaltado en la página
				this.content = highlightedContent;
			}

		}
	}

	/**
	 * Este método se ejecuta cuando se carga el archivo. Abre un Stream de entrada
	 * de datos para leer el contenido del archivo y guardarlo en la variable
	 * content
	 */
	public void upload() {
		System.out.println("Creando copia...");

		// Mantener vivo el archivo
		this.fileBackup = file;

		if (fileBackup != null) {
			System.out.println("Cargado " + fileBackup.getFileName());

			try {
				// Abrimos Stream de entrada
				BufferedReader BR = new BufferedReader(new InputStreamReader(fileBackup.getInputStream()));

				// Esto sirve para "construir" el nuevo texto con las etiquetas <span> y el
				// estilo de resaltado
				StringBuilder builder = new StringBuilder();
				String line = null;
				String separator = System.getProperty("line.separator");

				while ((line = BR.readLine()) != null) {
					builder.append(line);
					builder.append(separator);
				}

				// Por si el archivo no tiene contenido
				if (builder.length() == 0) {
					return;
				}

				// Borrar la última separación
				builder.deleteCharAt(builder.length() - 1);
				BR.close();

				System.out.println("Leyendo...");

				// Mandamos el texto modificado al texto que será mostrado en pantalla
				content = builder.toString();
				// Crea una copia del texto original
				contentCopy = new String(content);
			} catch (IOException e) {
				System.out.println("No se pudo releer el archivo");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Implementación del algoritmo KMP, busca la variable pattern en el text
	 * 
	 * @param text
	 * @param pattern
	 * @return
	 */
	public List<Integer> kmpSearchAll(String text, String pattern) {
		List<Integer> positions = new ArrayList<>();
		int[] next = kmpNext(pattern);
		int j = 0;
		for (int i = 0; i < text.length(); i++) {
			while (j > 0 && text.charAt(i) != pattern.charAt(j)) {
				j = next[j - 1];
			}
			if (text.charAt(i) == pattern.charAt(j)) {
				j++;
			}
			if (j == pattern.length()) {
				int startPosition = i - j + 1;

				// Agregamos la coincidencia encontrada
				positions.add(startPosition);

				// Reinicia j para buscar más coincidencias en la misma posición
				j = next[j - 1];
			}
		}
		return positions;
	}

	/**
	 * Este método construye el string con las etiquetas <span> y el estilo
	 * 
	 * @param text
	 * @param matchPositions
	 * @return
	 */
	public String highlightMatchesWithHtml(String text, List<Integer> matchPositions) {
		StringBuilder highlightedText = new StringBuilder();
		int currentIndex = 0;
		// System.out.println("Posiciones: " + matchPositions.toString());
		for (int position : matchPositions) {
			highlightedText.append(text.substring(currentIndex, position)); // Texto antes de la coincidencia
			highlightedText.append("<span style='background-color: yellow;'>");

			// Agregar toda la coincidencia desde la posición inicial hasta el final
			int endIndex = position + textToSearch.length();
			// System.out.println("Resaltando " + text.substring(position, endIndex));
			// System.out.println("Inicia:" + position);
			// System.out.println("Termina: " + endIndex);
			// System.out.println();
			highlightedText.append(text.substring(position, endIndex));

			highlightedText.append("</span>");
			currentIndex = endIndex; // Mover currentIndex al siguiente carácter después de la coincidencia
		}
		// Agregar el texto después de la última coincidencia
		highlightedText.append(text.substring(currentIndex));
		return highlightedText.toString();
	}

	public int[] kmpNext(String pattern) {
		int[] next = new int[pattern.length()];
		int j = 0;
		for (int i = 1; i < pattern.length(); i++) {
			while (j > 0 && pattern.charAt(i) != pattern.charAt(j)) {
				j = next[j - 1];
			}
			if (pattern.charAt(i) == pattern.charAt(j)) {
				j++;
			}
			next[i] = j;
		}
		return next;
	}

	public String readFile() {
		return null;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTextToSearch() {
		return textToSearch;
	}

	public void setTextToSearch(String textToSearch) {
		this.textToSearch = textToSearch;
	}

	public int getCantCoincidencias() {
		return cantCoincidencias;
	}

	public UploadedFile getFileBackup() {
		return fileBackup;
	}

	public void setFileBackup(UploadedFile fileBackup) {
		this.fileBackup = fileBackup;
	}

	public String getContentCopy() {
		return contentCopy;
	}

	public void setContentCopy(String contentCopy) {
		this.contentCopy = contentCopy;
	}

	public String getHighlightedContent() {
		return highlightedContent;
	}

	public void setHighlightedContent(String highlightedContent) {
		this.highlightedContent = highlightedContent;
	}

	public Boolean getUseCapitals() {
		return useCapitals;
	}

	public void setUseCapitals(Boolean useCapitals) {
		this.useCapitals = useCapitals;
	}

	public void setCantCoincidencias(int cantCoincidencias) {
		this.cantCoincidencias = cantCoincidencias;
	}

}
