package ch.innovazion.arionide.lang.programs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.LanguageManager;
import ch.innovazion.arionide.lang.Program;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.project.Storage;

public class SkeletonBuilder extends Program<Skeleton> {
	
	public SkeletonBuilder(Storage storage) {
		super(storage);
	}

	public Skeleton run(int rootStructure) {
		Skeleton skeleton = new Skeleton();
		
		Callable root = getCallable(rootStructure);
		
		Language lang = LanguageManager.get(root.getLanguage());
		
		if(lang != null) {
			build(root, skeleton, lang.getInstructionSet());
			return skeleton;
		} else {
			return null;
		}
	}
	
	private void build(Callable target, Skeleton skeleton, Map<String, Instruction> instructionSet) {
		skeleton.registerData(getState(target.getIdentifier()));
		skeleton.registerRodata(getConstants(target.getIdentifier()));
		skeleton.registerBSS(getProperties(target.getIdentifier()));
		
		List<Callable> code = getInstructions(target.getIdentifier());
				
		int length = 0;
		
		List<Callable> next = new ArrayList<>();
		
		for(Callable codeElement : code) {
			Instruction instr = instructionSet.get(codeElement.getName());
			
			if(instr != null) {
				length += instr.getLength();
				
				for(Parameter param : codeElement.getSpecification().getParameters()) {
					ParameterValue value = param.getValue();
					
					if(value instanceof Reference) {
						Reference ref = (Reference) value;
						Callable nextTarget = ref.getTarget();
						
						if(nextTarget != null) {
							next.add(nextTarget);
						} else {
							System.err.println("Invalid reference: " + ref.toString());
							throw new RuntimeException("See console for details");
						}
					}
				}
			} else {
				System.err.println("Invalid instruction: " + codeElement.toString());
				throw new RuntimeException("See console for details");
			}
		}
		
		skeleton.registerText(target, length);
		
		for(Callable callable : next) {
			build(callable, skeleton, instructionSet);
		}
	}
}
