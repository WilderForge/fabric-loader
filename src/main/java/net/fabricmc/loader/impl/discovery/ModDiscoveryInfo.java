package net.fabricmc.loader.impl.discovery;

import java.util.List;

public class ModDiscoveryInfo {

	private final List<ModCandidateImpl> modsFound;
	private final ModResolutionException exception;
	
	public ModDiscoveryInfo(List<ModCandidateImpl> discoveredMods, ModResolutionException exception) {
		this.modsFound = discoveredMods;
		this.exception = exception;
	}
	
	public List<ModCandidateImpl> getFoundMods() {
		return modsFound;
	}
	
	public ModResolutionException getException() {
		return exception;
	}
	
	public boolean launchable() {
		return exception == null;
	}
	
}
