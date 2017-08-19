package org.azentreprise.arionide.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.debugging.IAm;

public class Storage {
		
	private ZipFile zip;
	private ZipOutputStream stream;
	
	private List<StructureElement> hierarchy;
	private List<StructureElement> inheritance;
	private List<StructureElement> callgraph;

	@IAm("initializing the project storage")
	public Storage(File path) {
		try {
			this.zip = new ZipFile(path);
			this.stream = new ZipOutputStream(new FileOutputStream(path));
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}
	
	@IAm("fetching the project's meta data")
	public InputStream getMetaStream() {
		ZipEntry entry = this.zip.getEntry("project.meta");
		
		assert entry != null;
				
		try {
			return this.zip.getInputStream(entry);
		} catch (IOException exception) {
			Debug.exception(exception);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadHierarchy() {
		ZipEntry entry = this.zip.getEntry("hierarchy.struct");
		
		assert entry != null;
				
		try {
			ObjectInputStream input = new ObjectInputStream(this.zip.getInputStream(entry));
			this.hierarchy = (List<StructureElement>) input.readObject();
		} catch (IOException | ClassNotFoundException exception) {
			Debug.exception(exception);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadInheritance() {
		ZipEntry entry = this.zip.getEntry("inheritance.struct");
		
		assert entry != null;
				
		try {
			ObjectInputStream input = new ObjectInputStream(this.zip.getInputStream(entry));
			this.inheritance = (List<StructureElement>) input.readObject();
		} catch (IOException | ClassNotFoundException exception) {
			Debug.exception(exception);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadCallGraph() {
		ZipEntry entry = this.zip.getEntry("callgraph.struct");
		
		assert entry != null;
				
		try {
			ObjectInputStream input = new ObjectInputStream(this.zip.getInputStream(entry));
			this.callgraph = (List<StructureElement>) input.readObject();
		} catch (IOException | ClassNotFoundException exception) {
			Debug.exception(exception);
		}
	}
	
	public void saveCallGraph() {
	}
	
	public List<StructureElement> getHierarchy() {
		return this.hierarchy;
	}
	
	public List<StructureElement> getInheritance() {
		return this.inheritance;
	}
	
	public List<StructureElement> getCallGraph() {
		return this.callgraph;
	}
	
	@SuppressWarnings("unchecked")
	public void getDataStream(String element) {
		ZipEntry entry = this.zip.getEntry(element.concat(".data"));
		
		assert entry != null;
		
		try {
			ObjectInputStream input = new ObjectInputStream(this.zip.getInputStream(entry));
			this.callgraph = (List<StructureElement>) input.readObject();
		} catch (IOException | ClassNotFoundException exception) {
			Debug.exception(exception);
		}
	}
}
