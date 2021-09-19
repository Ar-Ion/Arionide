package ch.innovazion.arionide.ui.render.font.latex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import ch.innovazion.arionide.debugging.Debug;

public class LatexBackend extends Thread {
	
	private final Map<String, TextureData> textureCache;
	private final Queue<String> inputQueue = new ConcurrentLinkedQueue<>();
	private final Map<String, Process> processQueue = new HashMap<>();
	private final Map<String, File> loadQueue = new HashMap<>();
	
	protected LatexBackend(Map<String, TextureData> textureCache) {
		super("Latex Backend");
		this.textureCache = textureCache;
		start();
	}
	
	protected void requestLatex(String input) {
		synchronized(inputQueue) {
			if(!inputQueue.contains(input) && !processQueue.containsKey(input) && !loadQueue.containsKey(input)) { // If it is not already in the pipeline
				inputQueue.add(input);
			}
		}
	}
	
	public void run() {
		while(true) {		
			Iterator<String> it = inputQueue.iterator();
			
			while(it.hasNext()) {
				try {
					String input = it.next();
					
					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(input.getBytes());
					String filename = new String();
					
					for (byte b : md.digest()) {
						filename += String.format("%02x", b);
			        }
					
					File dir = Files.createTempDirectory("arionide").toFile();
					dir.mkdirs();
					File latex = new File(dir, filename + ".tex");
					File texture = new File(dir, filename + ".png");
					
					createLatexFile(latex, input);

					ProcessBuilder pb = new ProcessBuilder("/Library/TeX/texbin/latex", "-shell-escape", latex.getAbsolutePath());
					pb.environment().put("PATH", System.getenv("PATH") + ":/usr/local/bin:/Library/TeX/texbin");
					pb.directory(dir);
					pb.inheritIO();
					Process process = pb.start();
					
					processQueue.put(input, process);
					loadQueue.put(input, texture);
					
					it.remove();
				} catch (Exception exception) {
					Debug.exception(exception);
				}
			}
			
			Iterator<Entry<String, Process>> it2 = processQueue.entrySet().iterator();
			
			while(it2.hasNext()) {
				Entry<String, Process> process = it2.next();
				
				if(!process.getValue().isAlive() && process.getValue().exitValue() == 0) {
					try {
						File output = loadQueue.remove(process.getKey());
						
						InputStream stream = new FileInputStream(output);
						TextureData texture = TextureIO.newTextureData(GLProfile.get(GLProfile.GL4), stream, false, TextureIO.PNG);
						
						textureCache.put(process.getKey(), texture);
						
						it2.remove();
					} catch (IOException exception) {
						Debug.exception(exception);
					}
				}
			}
			
			Thread.yield();
		}
	}
	
	private void createLatexFile(File latex, String input) throws IOException, NoSuchAlgorithmException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(latex));
		writer.write("\\documentclass[convert={density=1200}]{standalone}\n");
		writer.write("\\usepackage{xcolor}\n");
		writer.write("\\pagecolor{black}\n");
		writer.write("\\color{white}\n");
		writer.write("\\begin{document}\n");
		writer.write(input + "\n");
		writer.write("\\end{document}\n");
		writer.close();
	}
}
