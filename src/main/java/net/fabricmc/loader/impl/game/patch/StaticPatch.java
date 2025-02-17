package net.fabricmc.loader.impl.game.patch;

import java.util.function.Consumer;
import java.util.function.Function;
import org.objectweb.asm.tree.ClassNode;
import net.fabricmc.loader.impl.launch.FabricLauncher;

public abstract class StaticPatch extends Patch {

	public abstract void process(FabricLauncher launcher, Function<String, ClassNode> classSource, Consumer<ClassNode> classEmitter);
}
