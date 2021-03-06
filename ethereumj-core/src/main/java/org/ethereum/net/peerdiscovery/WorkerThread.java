package org.ethereum.net.peerdiscovery;

import org.ethereum.net.client.PeerClient;
import org.ethereum.net.peerdiscovery.PeerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Roman Mandeleil 
 * Created on: 22/05/2014 09:26
 */
public class WorkerThread implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger("peerdiscovery");

	private PeerData peer;
	private PeerClient clientPeer;
	private ThreadPoolExecutor poolExecutor;

	public WorkerThread(PeerData peer, ThreadPoolExecutor poolExecutor) {
		this.peer = peer;
		this.poolExecutor = poolExecutor;
	}

	@Override
	public void run() {
		logger.debug("{} start", Thread.currentThread().getName());
		processCommand();
		logger.debug("{} end", Thread.currentThread().getName());

		poolExecutor.execute(this);
	}

	private void processCommand() {

		try {
			clientPeer = new PeerClient(true);
			clientPeer.connect(peer.getAddress().getHostAddress(), peer.getPort());
			peer.setOnline(true);
		} catch (Throwable e) {
			if (peer.isOnline())
				logger.info("Peer: [{}] went offline, due to: [{}]", peer
						.getAddress().getHostAddress(), e);
			peer.setOnline(false);
		} finally {
			logger.info("Peer: " + peer.toString() + " is "
					+ (peer.isOnline() ? "online" : "offline"));
			peer.setLastCheckTime(System.currentTimeMillis());

		}
	}

	@Override
	public String toString() {
		return "Worker for: " + this.peer.toString();
	}
}
