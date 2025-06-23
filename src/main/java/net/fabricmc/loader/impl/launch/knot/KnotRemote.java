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

package net.fabricmc.loader.impl.launch.knot;

import static net.fabricmc.loader.impl.util.SystemProperties.WEBSOCKET_ADDRESS;
import static net.fabricmc.loader.impl.util.SystemProperties.WEBSOCKET_CLIENT_ID;
import static net.fabricmc.loader.impl.util.SystemProperties.WEBSOCKET_PORT;
import static net.fabricmc.loader.impl.util.SystemProperties.WEBSOCKET_TIMEOUT;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

public class KnotRemote {
	private static Socket socket;
	private static String id;

	public static class Client {
		public static void main(String[] args) {
			connect(args);
			Knot.launch(args, EnvType.CLIENT);
		}
	}

	public static class Server {
		public static void main(String[] args) {
			connect(args);
			Knot.launch(args, EnvType.SERVER);
		}
	}

	private static void connect(String[] args) {
		final String address = System.getProperty(WEBSOCKET_ADDRESS, "localhost");
		Log.info(LogCategory.WEBSOCKET, "Address: " + address);

		final int port = Integer.parseInt(System.getProperty(WEBSOCKET_PORT, "61218"));
		Log.info(LogCategory.WEBSOCKET, "Port: " + port);

		id = System.getProperty(WEBSOCKET_CLIENT_ID);
		Log.info(LogCategory.WEBSOCKET, "ID: " + id);

		final int timeout = Integer.parseInt(System.getProperty(WEBSOCKET_TIMEOUT, "60")) * 10;
		Log.info(LogCategory.WEBSOCKET, "Timeout: " + timeout);
		startWatchdog(timeout);

		final String connectionString = address + " on port " + port;

		InetSocketAddress socketAddress = new InetSocketAddress(address, port);

		if (socket != null) {
			throw new IllegalStateException("Cannot reuse remote " + id);
		}

		socket = new Socket();
		Log.info(LogCategory.WEBSOCKET, "Connecting to " + connectionString);

		try {
			socket.connect(socketAddress, timeout);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to connect to " + connectionString, e);
		}

		Log.info(LogCategory.WEBSOCKET, "Successfully connected to " + connectionString);
	}

	private static void startWatchdog(int timeout) {
		Thread watcher = new Thread(() -> {
			Log.info(LogCategory.WEBSOCKET, "Watchdog started");

			try {
				Thread.sleep(timeout);
				Log.error(LogCategory.WEBSOCKET, "Launch timeout reached (" + timeout + "ms). Exiting JVM.");
				System.exit(3);
			} catch (InterruptedException ignored) {
				//ignored
			}
		});
		watcher.setDaemon(true);
		watcher.start();
	}
}
