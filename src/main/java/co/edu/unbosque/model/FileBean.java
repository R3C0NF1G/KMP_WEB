package co.edu.unbosque.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.file.UploadedFile;

@ManagedBean
@SessionScoped
public class FileBean {

	private UploadedFile file;
	private UploadedFile fileBackup;
//	private File actualFile;
	private String content;
	private String contentCopy;
	private String textToSearch;
	private int cantCoincidencias;
	private String highlightedContent;

	public void search1() {
		highlightedContent = ""; // Reinicia highlightedContent
		// Reiniciar el texto
		System.out.println("Reinciando texto...");
		content = new String(contentCopy);
		System.out.println(textToSearch);
		if (!textToSearch.isEmpty() && content != null && textToSearch != null) {

			this.content = contentCopy;
			cantCoincidencias = 0;
			// cantCoincidencias = doTheSearch();

			// Aplicar el algoritmo KMP para buscar y resaltar coincidencias
			System.out.println("A buscar");
			List<Integer> matchPositions = kmpSearchAll(content, textToSearch);
			cantCoincidencias = matchPositions.size();

			// Resaltar coincidencias
			String highlightedContent = highlightMatchesWithHtml(content, matchPositions);

			// Asigna el contenido resaltado a la propiedad highlightedContent
			// highlightedContent = highlightedContent.replace("\n", "<br/>");
			this.content = highlightedContent;

		}
	}

	public void upload() {
		System.out.println("Creando copia...");
		this.fileBackup = file;
		if (fileBackup != null) {
			System.out.println("Cargado " + fileBackup.getFileName());

			try {
				BufferedReader BR = new BufferedReader(new InputStreamReader(fileBackup.getInputStream()));
				StringBuilder builder = new StringBuilder();
				String line = null;
				String separator = System.getProperty("line.separator");

				while ((line = BR.readLine()) != null) {
					builder.append(line);
					builder.append(separator);
				}

				builder.deleteCharAt(builder.length() - 1);
				BR.close();

				System.out.println("Leyendo...");
				content = builder.toString();
				// Crea una copia del texto original
				contentCopy = new String(content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("No se pudo releer el archivo");
				e.printStackTrace();
			}
		}
	}

	public void resetContent() {
		try {
			BufferedReader BR = new BufferedReader(new InputStreamReader(fileBackup.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			String separator = System.getProperty("line.separator");

			while ((line = BR.readLine()) != null) {
				builder.append(line);
				builder.append(separator);
			}

			builder.deleteCharAt(builder.length() - 1);
			BR.close();

			System.out.println("Leyendo...");
			content = builder.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("No se pudo releer el archivo");
			e.printStackTrace();
		}
	}

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
				positions.add(startPosition);

				// Reinicia j para buscar más coincidencias en la misma posición
				j = next[j - 1];
			}
		}
		return positions;
	}

	public String highlightMatchesWithHtml(String text, List<Integer> matchPositions) {
		StringBuilder highlightedText = new StringBuilder();
		int currentIndex = 0;
		System.out.println("Posiciones: " + matchPositions.toString());
		for (int position : matchPositions) {
			highlightedText.append(text.substring(currentIndex, position)); // Texto antes de la coincidencia
			highlightedText.append("<span style='background-color: yellow;'>");

			// Agregar toda la coincidencia desde la posición inicial hasta el final
			int endIndex = position + textToSearch.length();
			System.out.println("Resaltando " + text.substring(position, endIndex));
			System.out.println("Inicia:" + position);
			System.out.println("Termina: " + endIndex);
			System.out.println();
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

}
