package co.edu.unbosque.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.file.UploadedFile;

@ManagedBean
@SessionScoped
public class FileBean {

	private UploadedFile file;
//	private File actualFile;
	private String content;
	private String textToSearch;

	public void search() {
		System.out.println(textToSearch);
		if (!textToSearch.isEmpty() && content != null && textToSearch != null) {
			doTheSearch();
		}
	}

	private void doTheSearch() {
		// Implementa aquí tu algoritmo de búsqueda KMP para encontrar todas las
		// coincidencias
		// y aplica el subrayado a las coincidencias encontradas en el texto
		// Puedes usar métodos como String.replaceAll() o StringBuilder para hacerlo.

		// Ejemplo simple que subraya todas las coincidencias en el texto:
		// texto = texto.replaceAll(palabra, "<span style='text-decoration:
		// underline;'>" + palabra + "</span>");
		
		textToSearch = textToSearch.toLowerCase();
		content = content.toLowerCase();
		
		int M = textToSearch.length();
        int N = content.length();
 
        // create lps[] that will hold the longest
        // prefix suffix values for pattern
        int lps[] = new int[M];
        int j = 0; // index for pat[]
 
        // Preprocess the pattern (calculate lps[]
        // array)
        computeLPSArray(textToSearch, M, lps);
 
        int i = 0; // index for txt[]
        while (i < N) {
            if (textToSearch.charAt(j) == content.charAt(i)) {
                j++;
                i++;
            }
            if (j == M) {
                System.out.println("Found pattern "
                                   + "at index " + (i - j));
                
                j = lps[j - 1];
  
            }
 
            // mismatch after j matches
            else if (i < N && textToSearch.charAt(j) != content.charAt(i)) {
                // Do not match lps[0..lps[j-1]] characters,
                // they will match anyway
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
	}
	
	 void computeLPSArray(String pat, int M, int lps[])
	    {
	        // length of the previous longest prefix suffix
	        int len = 0;
	        int i = 1;
	        lps[0] = 0; // lps[0] is always 0
	 
	        // the loop calculates lps[i] for i = 1 to M-1
	        while (i < M) {
	            if (pat.charAt(i) == pat.charAt(len)) {
	                len++;
	                lps[i] = len;
	                i++;
	            }
	            else // (pat[i] != pat[len])
	            {
	                // This is tricky. Consider the example.
	                // AAACAAAA and i = 7. The idea is similar
	                // to search step.
	                if (len != 0) {
	                    len = lps[len - 1];
	 
	                    // Also, note that we do not increment
	                    // i here
	                }
	                else // if (len == 0)
	                {
	                    lps[i] = len;
	                    i++;
	                }
	            }
	        }
	        
	        String tmp = textToSearch;
	        
	        content = content.replaceAll("\\b" + tmp + "\\b", "<span style='background-color:yellow;'>" + tmp + "</span>");
	        
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

	public String getTextToSearch() {
		return textToSearch;
	}

	public void setTextToSearch(String textToSearch) {
		this.textToSearch = textToSearch;
	}

}
