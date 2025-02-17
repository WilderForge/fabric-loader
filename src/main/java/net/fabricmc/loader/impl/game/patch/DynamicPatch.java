package net.fabricmc.loader.impl.game.patch;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public abstract class DynamicPatch extends Patch {

	public abstract String name();
	
	public String toString() {
		return name();
	}
	
	public abstract boolean handlesClass(ClassNode node, Type classType);
	
	public abstract void processClass(ClassNode node, Type type);
	
}
