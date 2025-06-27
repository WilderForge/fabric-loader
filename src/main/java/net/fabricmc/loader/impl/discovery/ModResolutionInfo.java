/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.loader.impl.discovery;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.List;

import net.fabricmc.loader.impl.lib.gson.JsonWriter;

public class ModResolutionInfo {
	//updated for every breaking change
	private static final int SCHEMA = 1;

	//updated when a non-breaking change is added. Resets to 1 on a breaking change. If a program expects version 1 but gets version 2, it should be able to load it with no issue.
	//An example of when this could occur is when new metadata is added, but old metadata is not changed.
	private static final int SCHEMA_VERSION = 1;

	private final List<ModCandidateImpl> modsResolved;
	private final ModResolutionException exception;

	public ModResolutionInfo(List<ModCandidateImpl> resolvedMods, ModResolutionException exception) {
		this.modsResolved = resolvedMods;
		this.exception = exception;
	}

	public List<ModCandidateImpl> getResolvedMods() {
		return modsResolved;
	}

	public ModResolutionException getException() {
		return exception;
	}

	public boolean launchable() {
		return exception == null;
	}

	public String serialize() {
		StringWriter s = new StringWriter();

		try (JsonWriter writer = new JsonWriter(s)) {
			writer.beginObject();
			writer.name("type");
			writer.value("discovery");

			writer.name("schema");
			writer.value(SCHEMA);

			writer.name("schema_version");
			writer.value(SCHEMA_VERSION);

			writer.name("mods");

			writer.beginArray();

			for (ModCandidateImpl mod : modsResolved) {
				serializeMod(mod, writer);
			}

			writer.endArray();

			writer.name("exception");
			serializeThrowable(exception, writer);

			return s.toString();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void serializeMod(ModCandidateImpl mod, JsonWriter writer) throws IOException {
		writer.beginObject();
		writer.name("modid");
		writer.value(mod.getId());
		writer.name("version");
		writer.value(mod.getVersion().toString());
		writer.name("type");
		writer.value(mod.getMetadata().getType());

		writer.endObject();
	}

	private void serializeThrowable(Throwable t, JsonWriter writer) throws IOException {
		if (t != null) {
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			writer.value(sw.toString());
		} else {
			writer.nullValue();
		}
	}
}
