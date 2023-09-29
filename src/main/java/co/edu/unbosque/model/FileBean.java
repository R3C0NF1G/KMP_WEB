package co.edu.unbosque.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.file.UploadedFile;

@ManagedBean
@SessionScoped
public class FileBean {

	private UploadedFile file;
//	private File actualFile;
	private String content;

	public void search() {
		System.out.println("Hola");
	}

	public void upload() {
		if (file != null) {
			System.out.println("Cargado " + file.getFileName());
			String fileName = file.getFileName();

			String path = "KMP_WEB\\rec\\";
			// actualFile = new File(path + fileName);

			try {
				BufferedReader BR = new BufferedReader(new InputStreamReader(file.getInputStream()));
				StringBuilder builder = new StringBuilder();
				String line = null;
				String separator = System.getProperty("line.separator");

				while ((line = BR.readLine()) != null) {
					builder.append(line);
					builder.append(separator);
				}

				builder.deleteCharAt(builder.length() - 1);
				BR.close();

				content = builder.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

}
